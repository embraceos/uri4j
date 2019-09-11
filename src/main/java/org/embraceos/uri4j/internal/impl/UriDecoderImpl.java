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

import org.embraceos.uri4j.UriDecoder;
import org.embraceos.uri4j.UriSyntaxException;
import org.embraceos.uri4j.internal.Hex;
import org.embraceos.uri4j.internal.Preconditions;
import org.embraceos.uri4j.internal.UriMasks;

import javax.annotation.concurrent.ThreadSafe;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

/**
 * This method always replaces malformed-input and unmappable-character
 * sequences with charset's default replacement string.
 *
 * @author Carrick Hong (洪灿昆)
 */
@ThreadSafe
public class UriDecoderImpl implements UriDecoder {

    public static final UriDecoderImpl INSTANCE = new UriDecoderImpl();

    @Override
    public byte[] decode(String encoded, int off, int len) throws UriSyntaxException, IndexOutOfBoundsException {
        Preconditions.checkPositiveRange(encoded.length(), off, off + len);
        byte[] bytes = new byte[len]; // don't use ByteArrayOutputStream to avoid synchronization and index checking
        int size = 0;
        for (int i = 0; i < len; i++) {
            int base = off + i;
            char c = encoded.charAt(base);
            if (c == '%') {
                bytes[size++] = parseTriplet(encoded, base, off + len);
                i += 2;
            } else {
                bytes[size++] = (byte) check(c, base);
            }
        }
        return Arrays.copyOf(bytes, size);
    }

    @Override
    public String decodeUtf8(String encoded, int off, int len) throws UriSyntaxException, IndexOutOfBoundsException {
        StringBuilder sb = null;
        byte[] bytes = null; // don't use ByteArrayOutputStream to avoid synchronization and index checking
        for (int i = 0; i < len; i++) {
            int base = off + i;
            char c = encoded.charAt(base);
            if (c == '%') {
                if (sb == null) {
                    sb = new StringBuilder(encoded.substring(off, base));
                    bytes = new byte[len];
                }
                int size = 0;
                do {
                    bytes[size++] = parseTriplet(encoded, base, off + len);
                    i += 2;
                } while (i + 1 < len && encoded.charAt(i + 1) == '%' /* side-effects */ && (base = off + (++i)) >= Integer.MIN_VALUE);
                sb.append(new String(bytes, 0, size, StandardCharsets.UTF_8));
            } else {
                if (sb != null) sb.append(check(c, base));
            }
        }
        return sb == null ? encoded : sb.toString();
    }

    private byte parseTriplet(String str, int off, int limit) throws UriSyntaxException {
        if (off + 2 >= limit) {
            throw new UriSyntaxException("uncompleted percent-encoding triplet at index " + off + ": " + str.substring(off, limit));
        }
        char h = str.charAt(off + 1), l = str.charAt(off + 2);
        if (!Hex.isHex(h) || !Hex.isHex(l)) {
            throw new UriSyntaxException("invalid percent-encoding triplet at index " + off + ": %" + h + l);
        }
        return Hex.toByte(h, l);
    }

    private char check(char c, int index) throws UriSyntaxException {
        if (!UriMasks.URIC.match(c)) {
            throw new UriSyntaxException("Invalid character at index " + index + ": " + c);
        }
        return c;
    }

}
