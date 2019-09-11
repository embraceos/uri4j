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
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CoderResult;
import java.nio.charset.CodingErrorAction;
import java.nio.charset.StandardCharsets;

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

    private static final ThreadLocal<UTF8Encoder> ENCODERS = ThreadLocal.withInitial(UTF8Encoder::new);

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
    public UriEncoder encodeUtf8(Appendable dst, String str) throws UncheckedIOException {
        try {
            UTF8Encoder encoder = ENCODERS.get();
            char[] chars = str.toCharArray();
            for (int i = 0, end = chars.length - 1; i <= end; i++) {
                char c = chars[i];
                if (mask.match(c)) {
                    dst.append(c);
                } else {
                    do {
                        Verify.verify(encoder.isWritable(), "cb should have remaining");
                        if (!Character.isSurrogate(c) || i == end) {
                            if (encoder.write(c).isWritable()) continue;
                        } else if (/* Character.isSurrogate(c) && */encoder.writable() >= 2) {
                            if (encoder.write(c).write(chars[++i]).isWritable()) continue;
                        } else i-- /* rollback pointer and encode existing */;
                        encoder.encode(dst);
                    } while (i < end && !mask.match(chars[i + 1]) /* side-effects */ && (c = chars[++i]) >= 0);
                    if (!encoder.isEmpty()) encoder.encode(dst);
                    Verify.verify(encoder.isEmpty());
                }
            }

            return this;
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private static class UTF8Encoder {

        private static final float MAX_BYTES_PER_CHAR_IN_UTF8 = StandardCharsets.UTF_8.newEncoder().maxBytesPerChar();
        private static final int CHAR_BUFFER_CAPACITY = 16;
        private static final int BYTE_BUFFER_CAPACITY = (int) Math.ceil(CHAR_BUFFER_CAPACITY * MAX_BYTES_PER_CHAR_IN_UTF8);

        private final CharBuffer cb = CharBuffer.allocate(CHAR_BUFFER_CAPACITY);
        private final ByteBuffer bb = ByteBuffer.allocate(BYTE_BUFFER_CAPACITY);
        private final CharsetEncoder encoder = StandardCharsets.UTF_8.newEncoder()
            .onMalformedInput(CodingErrorAction.REPLACE)
            .onUnmappableCharacter(CodingErrorAction.REPLACE);

        UTF8Encoder() {
        }

        UTF8Encoder write(char c) {
            cb.put(c);
            return this;
        }

        void encode(Appendable dst) throws IOException {
            cb.flip();
            bb.clear();
            encoder.reset();

            CoderResult cr = encoder.encode(cb, bb, true);
            Verify.verify(cr.isUnderflow());
            cr = encoder.flush(bb);
            Verify.verify(cr.isUnderflow());

            bb.flip();
            while (bb.hasRemaining()) {
                dst.append('%').append(Hex.toString(bb.get()));
            }

            cb.clear();
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
