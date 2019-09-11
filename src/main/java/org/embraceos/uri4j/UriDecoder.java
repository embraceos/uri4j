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

package org.embraceos.uri4j;

import org.apiguardian.api.API;
import org.embraceos.uri4j.internal.impl.UriDecoderImpl;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * An engine that can transform a String that contains
 * <a href="https://tools.ietf.org/html/rfc3986#section-2.1">percent-encoded octets</a>
 * into a sequence of raw bytes.
 *
 * @author Carrick Hong (洪灿昆)
 * @see UriEncoder
 */
@API(status = API.Status.STABLE)
public interface UriDecoder {

    /**
     * Returns an UriDecoder instance which's thread-safe and always replaces malformed-input
     * and unmappable-character sequences with charset's default replacement string.
     *
     * @return an UriDecoder
     */
    static UriDecoder get() {
        return UriDecoderImpl.INSTANCE;
    }

    /**
     * Decodes the encoded string to raw bytes.
     *
     * @param encoded The string to be decoded
     * @return The decoded raw bytes
     * @throws UriSyntaxException when there is something wrong with the syntax of given string
     * @see #decode(String, int, int)
     */
    default byte[] decode(String encoded) throws UriSyntaxException {
        return decode(encoded, 0, encoded.length());
    }

    /**
     * Decodes specified range of the encoded string to raw bytes.
     *
     * @param encoded The string to be decoded
     * @param off     The start offset
     * @param len     The number of chars to decode
     * @return The decoded raw bytes
     * @throws UriSyntaxException        when there is something wrong with the syntax of given string
     * @throws IndexOutOfBoundsException if the specified range isn't within the given string
     * @see #decode(String)
     */
    byte[] decode(String encoded, int off, int len) throws UriSyntaxException, IndexOutOfBoundsException;

    /**
     * Decodes the encoded string to raw bytes and then decodes that bytes into string
     * using the specified {@link Charset charset}.
     *
     * @param encoded The string to be decoded
     * @param charset The charset to be used to construct string
     * @return The decoded string with specified charset
     * @throws UriSyntaxException when there is something wrong with the syntax of given string
     * @see #decodeUtf8(String)
     * @see #decode(String, int, int, Charset)
     */
    default String decode(String encoded, Charset charset) throws UriSyntaxException {
        return decode(encoded, 0, encoded.length(), charset);
    }

    /**
     * Decodes specified range of the encoded string to raw bytes and then decodes
     * that bytes into string using the specified {@link Charset charset}.
     *
     * @param encoded The string to be decoded
     * @param off     The start offset
     * @param len     The number of chars to decode
     * @param charset The charset to be used to construct string
     * @return The decoded string with specified charset
     * @throws UriSyntaxException        when there is something wrong with the syntax of given string
     * @throws IndexOutOfBoundsException if the specified range isn't within the given string
     * @see #decodeUtf8(String, int, int)
     * @see #decode(String, Charset)
     */
    default String decode(String encoded, int off, int len, Charset charset) throws UriSyntaxException, IndexOutOfBoundsException {
        if (StandardCharsets.UTF_8.equals(charset)) {
            return decodeUtf8(encoded, off, len);
        }
        return new String(decode(encoded, off, len), charset);
    }

    /**
     * Decodes the encoded string to raw bytes and then decodes that bytes into string
     * using the UTF-8 charset.
     *
     * @param encoded The string to be decoded
     * @return The decoded string with UTF-8 charset
     * @throws UriSyntaxException when there is something wrong with the syntax of given string
     * @see #decode(String, Charset)
     * @see #decodeUtf8(String, int, int)
     */
    default String decodeUtf8(String encoded) throws UriSyntaxException {
        return decodeUtf8(encoded, 0, encoded.length());
    }

    /**
     * Decodes specified range of the encoded string to raw bytes and then decodes that bytes
     * into string using the UTF-8 charset.
     *
     * @param encoded The string to be decoded
     * @param off     The start offset
     * @param len     The number of chars to decode
     * @return The decoded string with UTF-8 charset
     * @throws UriSyntaxException        when there is something wrong with the syntax of given string
     * @throws IndexOutOfBoundsException if the specified range isn't within the given string
     * @see #decode(String, int, int, Charset)
     * @see #decodeUtf8(String)
     */
    String decodeUtf8(String encoded, int off, int len) throws UriSyntaxException, IndexOutOfBoundsException;

}
