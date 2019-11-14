/*
 * Copyright 2019-2019 Carrick Hong (洪灿昆)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.embraceos.uri4j.internal.impl;

import org.embraceos.uri4j.UriEncoder;
import org.embraceos.uri4j.internal.*;

import javax.annotation.concurrent.ThreadSafe;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.*;
import java.util.HashMap;
import java.util.Map;

/**
 * This method always replaces malformed-input and unmappable-character
 * sequences with charset's default replacement byte array.
 *
 * @author Carrick Hong (洪灿昆)
 */
@ThreadSafe
public class UriEncoderImpl implements UriEncoder {

    public static final UriEncoderImpl UNRESOLVED = new UriEncoderImpl(UriMasks.UNRESERVED);
    public static final UriEncoderImpl USER_INFO = new UriEncoderImpl(UriMasks.USER_INFO);
    public static final UriEncoderImpl HOST = new UriEncoderImpl(UriMasks.REG_NAME);
    public static final UriEncoderImpl PATH = new UriEncoderImpl(UriMasks.PATH);
    public static final UriEncoderImpl SEGMENT = new UriEncoderImpl(UriMasks.SEGMENT);
    public static final UriEncoderImpl QUERY = new UriEncoderImpl(UriMasks.QUERY);
    public static final UriEncoderImpl FRAGMENT = new UriEncoderImpl(UriMasks.FRAGMENT);
    public static final UriEncoderImpl URI = new UriEncoderImpl(UriMasks.URIC);

    private static final ThreadLocal<Map<Charset, Encoder>> ENCODERS = ThreadLocal.withInitial(HashMap::new);

    private final AsciiMask mask;

    private UriEncoderImpl(AsciiMask mask) throws IllegalArgumentException {
        Preconditions.checkArgument(UriMasks.URIC.contains(mask),
            "contains ASCII character which is not allowed by RFC3986 specification");
        this.mask = mask;
    }

    public static UriEncoderImpl allow(String str) throws IllegalArgumentException {
        return new UriEncoderImpl(AsciiMask.allow(str));
    }

    public static UriEncoderImpl extra(String str) throws IllegalArgumentException {
        return new UriEncoderImpl(AsciiMask.combine(UriMasks.UNRESERVED, AsciiMask.allow(str)));
    }

    private static Encoder getEncoder(Charset charset) {
        return ENCODERS.get().computeIfAbsent(charset, Encoder::new);
    }

    @Override
    public UriEncoder encode(Appendable dst, byte[] bytes) throws UncheckedIOException {
        try {
            for (byte b : bytes) {
                char c = (char) ((int) b & 0xFF);
                if (mask.match(c)) {
                    dst.append(c);
                } else {
                    dst.append('%').append(Hex.toString(b));
                }
            }
            return this;
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public UriEncoder encode(Appendable dst, String str, Charset charset, boolean mixed) throws UncheckedIOException {
        if (StandardCharsets.UTF_8.equals(charset)) {
            return encodeUtf8(dst, str, mixed);
        }

        if (!mixed) {
            return encode(dst, str.getBytes(charset));
        }

        if (str.isEmpty()) return this;

        try {
            Encoder encoder = getEncoder(charset);
            ByteProcessor processor = b -> {
                try {
                    char c = (char) ((int) b & 0xFF);
                    if (mask.match(c)) {
                        dst.append(c);
                    } else {
                        dst.append('%').append(Hex.toString(b));
                    }
                } catch (IOException e) {
                    throw new UncheckedIOException(e);
                }
            };

            int index = 0;
            int off = indexOfTriplet(str, 0);
            while (off != -1) {
                encoder.encode(processor, str, index, off);
                char h = Character.toUpperCase(str.charAt(off + 1)), l = Character.toUpperCase(str.charAt(off + 2));
                dst.append(str.charAt(off)).append(h).append(l);
                index = off = off + 3;
                off = indexOfTriplet(str, off);
            }
            encoder.encode(processor, str, index, str.length());

            return this;
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public UriEncoder encodeUtf8(Appendable dst, String str, boolean mixed) throws UncheckedIOException {
        try {
            Encoder encoder = getEncoder(StandardCharsets.UTF_8);
            ByteProcessor processor = b -> {
                try {
                    dst.append('%').append(Hex.toString(b));
                } catch (IOException e) {
                    throw new UncheckedIOException(e);
                }
            };

            for (int i = 0, end = str.length() - 1; i <= end; i++) {
                char c = str.charAt(i);
                if (mask.match(c)) {
                    dst.append(c);
                } else if (mixed && isTriplet(str, i)) {
                    char h = Character.toUpperCase(str.charAt(++i)), l = Character.toUpperCase(str.charAt(++i));
                    dst.append(c).append(h).append(l);
                } else {
                    int to = i + 1;
                    while (to <= end && !mask.match(str.charAt(to)) && !(mixed && isTriplet(str, to))) to++;
                    encoder.encode(processor, str, i, to);
                    i = to - 1;
                }
            }

            return this;
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private int indexOfTriplet(String str, int off) {
        Preconditions.checkIndex(off, str.length());

        int end = str.length() - 2;
        for (int i = off; i < end; i++) {
            if (isTriplet(str, i)) return i;
        }
        return -1;
    }

    private boolean isTriplet(String str, int off) throws IndexOutOfBoundsException {
        Preconditions.checkIndex(off, str.length());

        if (str.length() - off < 3) return false;
        if (str.charAt(off) != '%') return false;

        char h = str.charAt(off + 1), l = str.charAt(off + 2);
        return Hex.isHex(h) && Hex.isHex(l);
    }

    private interface ByteProcessor {
        void process(byte b);
    }

    private static class Encoder {

        private static final int CHAR_BUFFER_CAPACITY = 16;

        private final CharsetEncoder encoder;
        private final CharBuffer cb;
        private final ByteBuffer bb;

        Encoder(Charset charset) {
            this.encoder = charset.newEncoder()
                .onMalformedInput(CodingErrorAction.REPLACE)
                .onUnmappableCharacter(CodingErrorAction.REPLACE);

            float maxBytesPerChar = this.encoder.maxBytesPerChar();
            int byteBufferCapacity = (int) Math.ceil(CHAR_BUFFER_CAPACITY * maxBytesPerChar);

            this.cb = CharBuffer.allocate(CHAR_BUFFER_CAPACITY);
            this.bb = ByteBuffer.allocate(byteBufferCapacity);
        }

        Encoder write(char c) throws BufferOverflowException {
            cb.put(c);
            return this;
        }

        void encode(ByteProcessor processor) {
            cb.flip();
            bb.clear();
            encoder.reset();

            CoderResult cr = encoder.encode(cb, bb, true);
            Verify.verify(cr.isUnderflow());
            cr = encoder.flush(bb);
            Verify.verify(cr.isUnderflow());

            bb.flip();
            while (bb.hasRemaining()) {
                processor.process(bb.get());
            }

            cb.clear();
        }

        void encode(ByteProcessor processor, String str, int from, int to) {
            Preconditions.checkPositiveRange(str.length(), from, to);
            Preconditions.checkState(isEmpty());

            for (int i = from, end = to - 1; i <= end; i++) {
                char c = str.charAt(i);
                Verify.verify(isWritable(), "cb should have remaining");

                if (!Character.isSurrogate(c) || i == end) {
                    if (write(c).isWritable()) continue;
                } else if (/* Character.isSurrogate(c) && */ writable() >= 2) {
                    if (write(c).write(str.charAt(++i)).isWritable()) continue;
                } else i-- /* rollback pointer and encode existing */;

                encode(processor);
            }

            if (!isEmpty()) encode(processor);
            Verify.verify(isEmpty());
        }

        int writable() {
            return cb.remaining();
        }

        boolean isWritable() {
            return cb.hasRemaining();
        }

        boolean isEmpty() {
            return cb.position() == 0;
        }

    }

}
