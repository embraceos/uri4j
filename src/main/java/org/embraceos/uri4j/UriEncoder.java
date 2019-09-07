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

import java.io.UncheckedIOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * An engine that can transform a sequence of raw bytes into a String by
 * <a href="https://tools.ietf.org/html/rfc3986#section-2.1">percent-encoding</a>
 * the bytes that are outside the allowed set, and decoding allowed bytes as ASCII characters.
 * Instances can have different allowed set.
 *
 * @author Carrick Hong (洪灿昆)
 * @see UriDecoder
 */
@API(status = API.Status.STABLE)
public interface UriEncoder {

    /**
     * Encodes the raw bytes to percent-encoded string.
     *
     * @param bytes The raw bytes to be encoded
     * @return The percent-encoded string
     * @see #encode(Appendable, byte[])
     */
    default String encode(byte[] bytes) {
        StringBuilder sb = new StringBuilder(bytes.length << 1);
        encode(sb, bytes);
        return sb.toString();
    }

    /**
     * Encodes the raw bytes to percent-encoded string and then appends it to {@code dst}.
     *
     * @param dst   The {@link Appendable} to which percent-encoded string will be appended
     * @param bytes The raw bytes to be encoded
     * @return This encoder
     * @throws UncheckedIOException when there is something wrong appending data to dst
     * @see #encode(byte[])
     */
    UriEncoder encode(Appendable dst, byte[] bytes) throws UncheckedIOException;

    /**
     * Encodes the given string to bytes using given charset and than encodes that bytes
     * to percent-encoded string.
     *
     * @param str     The string to be encoded
     * @param charset The charset to be used to encode string to bytes
     * @return The percent-encoded string
     * @see #encode(Appendable, String, Charset)
     * @see #encodeUtf8(String)
     */
    default String encode(String str, Charset charset) {
        StringBuilder sb = new StringBuilder(str.length() << 1);
        encode(sb, str, charset);
        return sb.toString();
    }

    /**
     * Encodes the given string to bytes using given charset and than encodes that bytes
     * to percent-encoded string and then appends it to {@code dst}.
     *
     * @param dst     The {@link Appendable} to which percent-encoded string will be appended
     * @param str     The string to be encoded
     * @param charset The charset to be used to encode string to bytes
     * @return The percent-encoded string
     * @throws UncheckedIOException when there is something wrong appending data to dst
     * @see #encode(String, Charset)
     * @see #encodeUtf8(Appendable, String)
     */
    default UriEncoder encode(Appendable dst, String str, Charset charset) throws UncheckedIOException {
        if (StandardCharsets.UTF_8.equals(charset)) {
            return encodeUtf8(dst, str);
        }
        return encode(dst, str.getBytes(charset));
    }

    /**
     * Encodes the given string to bytes using UTF-8 charset and than encodes that bytes
     * to percent-encoded string.
     *
     * @param str The string to be encoded
     * @return The percent-encoded string
     * @see #encodeUtf8(Appendable, String)
     * @see #encode(String, Charset)
     */
    default String encodeUtf8(String str) {
        StringBuilder sb = new StringBuilder(str.length() << 1);
        encodeUtf8(sb, str);
        return sb.toString();
    }

    /**
     * Encodes the given string to bytes using UTF-8 charset and than encodes that bytes
     * to percent-encoded string and then appends it to {@code dst}.
     *
     * @param dst The {@link Appendable} to which percent-encoded string will be appended
     * @param str The string to be encoded
     * @return The percent-encoded string
     * @throws UncheckedIOException when there is something wrong appending data to dst
     * @see #encodeUtf8(String)
     * @see #encode(Appendable, String, Charset)
     */
    UriEncoder encodeUtf8(Appendable dst, String str) throws UncheckedIOException;

}
