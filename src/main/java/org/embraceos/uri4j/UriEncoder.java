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
import org.embraceos.uri4j.internal.impl.UriEncoderImpl;

import javax.annotation.concurrent.ThreadSafe;
import java.io.UncheckedIOException;
import java.nio.charset.Charset;

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
@ThreadSafe
public interface UriEncoder {

    /**
     * Returns a thread-safe UriEncoder which allows all unresolved characters only,
     * and will always replace malformed-input and unmappable-character
     * sequences with charset's default replacement byte array.
     *
     * @return an UriEncoder
     */
    static UriEncoder forData() {
        return UriEncoderImpl.UNRESOLVED;
    }

    /**
     * Returns a thread-safe UriEncoder which allows unresolved characters and the other given ones,
     * and will always replace malformed-input and unmappable-character
     * sequences with charset's default replacement byte array.
     *
     * @param allowedDelims allowed delimiters
     * @return an UriEncoder
     * @throws IllegalArgumentException if the given delimiters contain ones that is not allowed by RFC3986
     */
    static UriEncoder forComponent(String allowedDelims) throws IllegalArgumentException {
        return UriEncoderImpl.extra(allowedDelims);
    }

    /**
     * Returns a thread-safe UriEncoder to be used to encode userinfo components,
     * which will always replace malformed-input and unmappable-character
     * sequences with charset's default replacement byte array.
     *
     * @return an UriEncoder
     */
    static UriEncoder forUserInfo() {
        return UriEncoderImpl.USER_INFO;
    }

    /**
     * Returns a thread-safe UriEncoder to be used to encode host components treated as
     * <a href="https://tools.ietf.org/html/rfc3986#section-3.2.2">reg-name</a>,
     * which will always replace malformed-input and unmappable-character
     * sequences with charset's default replacement byte array.
     *
     * @return an UriEncoder
     */
    static UriEncoder forHost() {
        return UriEncoderImpl.HOST;
    }

    /**
     * Returns a thread-safe UriEncoder to be used to encode path components,
     * which will always replace malformed-input and unmappable-character
     * sequences with charset's default replacement byte array.
     *
     * @return an UriEncoder
     */
    static UriEncoder forPath() {
        return UriEncoderImpl.PATH;
    }

    /**
     * Returns a thread-safe UriEncoder to be used to encode path segment components,
     * which will always replace malformed-input and unmappable-character
     * sequences with charset's default replacement byte array.
     *
     * @return an UriEncoder
     */
    static UriEncoder forSegment() {
        return UriEncoderImpl.SEGMENT;
    }

    /**
     * Returns a thread-safe UriEncoder to be used to encode query components,
     * which will always replace malformed-input and unmappable-character
     * sequences with charset's default replacement byte array.
     *
     * @return an UriEncoder
     */
    static UriEncoder forQuery() {
        return UriEncoderImpl.QUERY;
    }

    /**
     * Returns a thread-safe UriEncoder to be used to encode fragment components,
     * which will always replace malformed-input and unmappable-character
     * sequences with charset's default replacement byte array.
     *
     * @return an UriEncoder
     */
    static UriEncoder forFragment() {
        return UriEncoderImpl.FRAGMENT;
    }

    /**
     * Returns a thread-safe UriEncoder to be used to encode whole URI,
     * which can't encode components by their according requirements but just encodes
     * all characters that's not allowed to present in URI, and
     * will always replace malformed-input and unmappable-character
     * sequences with charset's default replacement byte array.
     *
     * <p> It is preferred to use a more specific UriEncoder to get more precise control
     * over URI encoding such as the one returned by {@link #forPath()}.
     *
     * @return an UriEncoder
     */
    static UriEncoder forURI() {
        return UriEncoderImpl.URI;
    }

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
     * @see #encode(String, Charset, boolean)
     * @see #encode(Appendable, String, Charset)
     * @see #encodeUtf8(String)
     */
    default String encode(String str, Charset charset) {
        return encode(str, charset, false);
    }

    /**
     * Encodes the given string to bytes using given charset and than encodes that bytes
     * to percent-encoded string and then appends it to {@code dst}.
     *
     * @param dst     The {@link Appendable} to which percent-encoded string will be appended
     * @param str     The string to be encoded
     * @param charset The charset to be used to encode string to bytes
     * @return This encoder
     * @throws UncheckedIOException when there is something wrong appending data to dst
     * @see #encode(Appendable, String, Charset, boolean)
     * @see #encode(String, Charset)
     * @see #encodeUtf8(Appendable, String)
     */
    default UriEncoder encode(Appendable dst, String str, Charset charset) throws UncheckedIOException {
        return encode(dst, str, charset, false);
    }

    /**
     * Encodes the given string to bytes using given charset and than encodes that bytes
     * to percent-encoded string.
     *
     * <p> This method can encode str in mixed mode or not, depending on the given {@code mixed}
     * argument. With mixed mode on, the percent-encoding triplets in given {@code str} would be
     * preserved, i.e., not to be encoded. So, the meaning of mixed mode is that there are some
     * percent-encoding triplets mixed here already. For example, path segment "a%20b" would be
     * encoded to "a%2520b" with charset utf-8 and mixed mode off, but would be encoded to "a%20b"
     * with same charset and mixed mode on.
     *
     * @param str     The string to be encoded
     * @param charset The charset to be used to encode string to bytes
     * @param mixed   Whether to encode given str in mixed mode or not
     * @return The percent-encoded string
     * @see #encode(String, Charset)
     * @see #encode(Appendable, String, Charset)
     * @see #encodeUtf8(String)
     */
    default String encode(String str, Charset charset, boolean mixed) {
        StringBuilder sb = new StringBuilder(str.length() << 1);
        encode(sb, str, charset, mixed);
        return sb.toString();
    }

    /**
     * Encodes the given string to bytes using given charset and than encodes that bytes
     * to percent-encoded string and then appends it to {@code dst}.
     *
     * <p> This method can encode str in mixed mode or not, depending on the given {@code mixed}
     * argument. With mixed mode on, the percent-encoding triplets in given {@code str} would be
     * preserved, i.e., not to be encoded. So, the meaning of mixed mode is that there are some
     * percent-encoding triplets mixed here already. For example, path segment "a%20b" would be
     * encoded to "a%2520b" with charset utf-8 and mixed mode off, but would be encoded to "a%20b"
     * with same charset and mixed mode on.
     *
     * @param dst     The {@link Appendable} to which percent-encoded string will be appended
     * @param str     The string to be encoded
     * @param charset The charset to be used to encode string to bytes
     * @param mixed   Whether to encode given str in mixed mode or not
     * @return This encoder
     * @throws UncheckedIOException when there is something wrong appending data to dst
     * @see #encode(Appendable, String, Charset)
     * @see #encode(String, Charset)
     * @see #encodeUtf8(Appendable, String)
     */
    UriEncoder encode(Appendable dst, String str, Charset charset, boolean mixed) throws UncheckedIOException;

    /**
     * Encodes the given string to bytes using UTF-8 charset and than encodes that bytes
     * to percent-encoded string.
     *
     * @param str The string to be encoded
     * @return The percent-encoded string
     * @see #encodeUtf8(String, boolean)
     * @see #encodeUtf8(Appendable, String)
     * @see #encode(String, Charset)
     */
    default String encodeUtf8(String str) {
        return encodeUtf8(str, false);
    }

    /**
     * Encodes the given string to bytes using UTF-8 charset and than encodes that bytes
     * to percent-encoded string and then appends it to {@code dst}.
     *
     * @param dst The {@link Appendable} to which percent-encoded string will be appended
     * @param str The string to be encoded
     * @return This encoder
     * @throws UncheckedIOException when there is something wrong appending data to dst
     * @see #encodeUtf8(Appendable, String, boolean)
     * @see #encodeUtf8(String)
     * @see #encode(Appendable, String, Charset)
     */
    default UriEncoder encodeUtf8(Appendable dst, String str) throws UncheckedIOException {
        return encodeUtf8(dst, str, false);
    }

    /**
     * Encodes the given string to bytes using UTF-8 charset and than encodes that bytes
     * to percent-encoded string.
     *
     * <p> This method can encode str in mixed mode or not, depending on the given {@code mixed}
     * argument. With mixed mode on, the percent-encoding triplets in given {@code str} would be
     * preserved, i.e., not to be encoded. So, the meaning of mixed mode is that there are some
     * percent-encoding triplets mixed here already. For example, path segment "a%20b" would be
     * encoded to "a%2520b" with charset utf-8 and mixed mode off, but would be encoded to "a%20b"
     * with same charset and mixed mode on.
     *
     * @param str   The string to be encoded
     * @param mixed Whether to encode given str in mixed mode or not
     * @return The percent-encoded string
     * @see #encodeUtf8(String)
     * @see #encodeUtf8(Appendable, String)
     * @see #encode(String, Charset)
     */
    default String encodeUtf8(String str, boolean mixed) {
        StringBuilder sb = new StringBuilder(str.length() << 1);
        encodeUtf8(sb, str, mixed);
        return sb.toString();
    }

    /**
     * Encodes the given string to bytes using UTF-8 charset and than encodes that bytes
     * to percent-encoded string and then appends it to {@code dst}.
     *
     * <p> This method can encode str in mixed mode or not, depending on the given {@code mixed}
     * argument. With mixed mode on, the percent-encoding triplets in given {@code str} would be
     * preserved, i.e., not to be encoded. So, the meaning of mixed mode is that there are some
     * percent-encoding triplets mixed here already. For example, path segment "a%20b" would be
     * encoded to "a%2520b" with charset utf-8 and mixed mode off, but would be encoded to "a%20b"
     * with same charset and mixed mode on.
     *
     * @param dst   The {@link Appendable} to which percent-encoded string will be appended
     * @param str   The string to be encoded
     * @param mixed Whether to encode given str in mixed mode or not
     * @return This encoder
     * @throws UncheckedIOException when there is something wrong appending data to dst
     * @see #encodeUtf8(Appendable, String)
     * @see #encodeUtf8(String)
     * @see #encode(Appendable, String, Charset)
     */
    UriEncoder encodeUtf8(Appendable dst, String str, boolean mixed) throws UncheckedIOException;

}
