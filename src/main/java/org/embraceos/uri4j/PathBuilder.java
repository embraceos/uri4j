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
import org.embraceos.uri4j.internal.impl.PathBuilderImpl;

import javax.annotation.concurrent.NotThreadSafe;
import java.util.List;

/**
 * Builder-style methods to build path components of URI-reference.
 *
 * <p> There are two types of segments manipulation methods: {@code string} and {@code raw}.
 * For {@code string} methods, the given segments is of type {@link String}, which will be
 * checked for not allowed characters, {@link UriSyntaxException} will be thrown if exist,
 * e.g., {@link #segments(String...)}.
 * For {@code raw} methods, the name will include {@code raw}, and the given segments is of
 * type {@code byte[]}, which will be
 * <a href="https://tools.ietf.org/html/rfc3986#section-2.1">percent-encoded</a> to a string,
 * e.g., {@link #rawSegments(byte[]...)}.
 *
 * <p> Because UTF-8 encoded string is used as path segments so commonly, there
 * is also another type of segments manipulation methods for convenience, i.e., the {@code utf8} methods.
 * For {@code utf8} methods, the name will include {@code utf8}, and the given segments is
 * of type {@link String}, in which the invalid characters will be converted to {@code byte[]},
 * and then {@code percent-encode}, e.g., {@link #utf8Segments(String...)}.
 *
 * <p> There are also some methods to append given paths to this builder for adding existing
 * paths conveniently, e.g., {@link #paths(String...)} and {@link #rawPaths(byte[]...)}.
 * The path p is appended as follows:
 * <ul>
 *   <li> p is split to segments despite whether it's absolute or not ("/a/b/c" and "a/b/c" are all split
 *   to segments "a", "b" and "c").
 *   <li> If the tail segment of builder is empty, remove it.
 *   <li> Append these segments to builder.
 * </ul>
 *
 * <p> There are also some methods to remove segments conveniently, such as {@link #strip(int)}
 * to remove topping segments, {@link #tear(int)} to remove trailing segments and {@link #trim()}
 * to remove trailing empty segments.
 *
 * <p> All {@code String} methods may defer character-checking to the the final step of building
 * process, i.e., the method {@link #build()}. Whether a {@code string} method will defer
 * character-checking or not depends on the implementation.
 *
 * @author Carrick Hong (洪灿昆)
 * @see Path
 */
@API(status = API.Status.STABLE)
@NotThreadSafe
public interface PathBuilder {

    /**
     * Creates a PathBuilder with absolute set to true and without any segment.
     */
    static PathBuilder create() {
        return new PathBuilderImpl();
    }

    /**
     * Creates a PathBuilder initialized with the given path.
     *
     * @param path The path used to initialize builder
     * @throws UriSyntaxException when there is something wrong with the syntax of the given path
     */
    static PathBuilder from(String path) throws UriSyntaxException {
        return Path.parse(path).mutate();
    }

    /**
     * Appends the given segments to the end of this path, of which {@code null} values will be ignored.
     *
     * @param segments The segments to be appended
     * @return This path builder for further processing
     * @throws UriSyntaxException when there is something wrong with the syntax of segments
     */
    default PathBuilder segments(String... segments) throws UriSyntaxException {
        return segments(size(), segments);
    }

    /**
     * Appends the given segments to the end of this path, of which {@code null} values will be ignored.
     *
     * <p> The given segments will be
     * <a href="https://tools.ietf.org/html/rfc3986#section-2.1">percent-encoded</a>.
     *
     * @param segments The segments to be appended
     * @return This path builder for further processing
     */
    default PathBuilder rawSegments(byte[]... segments) {
        return rawSegments(size(), segments);
    }

    /**
     * Appends the given segments to the end of this path, of which {@code null} values will be ignored.
     *
     * <p> The invalid characters in the given segments will be converted to
     * {@code byte[]} with {@code UTF-8} charset, and then
     * <a href="https://tools.ietf.org/html/rfc3986#section-2.1">percent-encoded</a>.
     *
     * @param segments The segments to be appended
     * @return This path builder for further processing
     */
    default PathBuilder utf8Segments(String... segments) {
        return utf8Segments(size(), segments);
    }

    /**
     * Adds the given segments to the specified position of this path, of which {@code null} values
     * will be ignored.
     *
     * @param index    The index at which to insert the first segment
     * @param segments The segments to be added
     * @return This path builder for further processing
     * @throws UriSyntaxException        when there is something wrong with the syntax of segments
     * @throws IndexOutOfBoundsException if the index is out of range (index < 0 || index > size())
     */
    PathBuilder segments(int index, String... segments) throws UriSyntaxException, IndexOutOfBoundsException;

    /**
     * Adds the given segments to the specified position of this path, of which {@code null} values
     * will be ignored.
     *
     * <p> The given segments will be
     * <a href="https://tools.ietf.org/html/rfc3986#section-2.1">percent-encoded</a>.
     *
     * @param index    The index at which to insert the first segment
     * @param segments The segments to be added
     * @return This path builder for further processing
     * @throws IndexOutOfBoundsException if the index is out of range (index < 0 || index > size())
     */
    default PathBuilder rawSegments(int index, byte[]... segments) throws IndexOutOfBoundsException {
        String[] encoded = new String[segments.length];
        for (int i = 0; i < segments.length; i++) {
            byte[] segment = segments[i];
            encoded[i] = segment == null ? null : UriEncoder.forSegment().encode(segment);
        }
        return segments(index, encoded);
    }

    /**
     * Adds the given segments to the specified position of this path, of which {@code null} values
     * will be ignored.
     *
     * <p> The invalid characters in the given segments will be converted to
     * {@code byte[]} with {@code UTF-8} charset, and then
     * <a href="https://tools.ietf.org/html/rfc3986#section-2.1">percent-encoded</a>.
     *
     * @param index    The index at which to insert the first segment
     * @param segments The segments to be added
     * @return This path builder for further processing
     * @throws IndexOutOfBoundsException if the index is out of range (index < 0 || index > size())
     */
    default PathBuilder utf8Segments(int index, String... segments) throws IndexOutOfBoundsException {
        String[] encoded = new String[segments.length];
        for (int i = 0; i < segments.length; i++) {
            String segment = segments[i];
            encoded[i] = segment == null ? null : UriEncoder.forSegment().encodeUtf8(segment);
        }
        return segments(index, encoded);
    }

    /**
     * Overwrites the given segments to the specified position of this path, of which {@code null} values
     * will be ignored.
     *
     * <p> If the size of the given segments (excluding {@code null} values) is larger
     * than {@code size() - index}, then the extra segments will be appended to this path. For example,
     * setting segments {@code {"s1", null, "s2"}} to a path builder that contains segments
     * {@code {"s3","s4"}} at index {@code 1} will result to {@code {"s3", "s1", "s2"}}.
     *
     * @param index    The index at which to set the first segment
     * @param segments The segments to be set
     * @return This path builder for further processing
     * @throws UriSyntaxException        when there is something wrong with the syntax of segments
     * @throws IndexOutOfBoundsException if the index is out of range (index < 0 || index >= size())
     */
    PathBuilder setSegments(int index, String... segments) throws UriSyntaxException, IndexOutOfBoundsException;

    /**
     * Overwrites the given segments to the specified position of this path, of which {@code null} values
     * will be ignored.
     *
     * <p> The given segments will be
     * <a href="https://tools.ietf.org/html/rfc3986#section-2.1">percent-encoded</a>.
     *
     * <p> If the size of the given segments (excluding {@code null} values) is larger
     * than {@code size() - index}, then the extra segments will be appended to this path. For example,
     * setting segments {@code {"s1", null, "s2"}} to a path builder that contains segments
     * {@code {"s3","s4"}} at index {@code 1} will result to {@code {"s3", "s1", "s2"}}.
     *
     * @param segments The segments to be set
     * @param index    The index at which to set the first segment
     * @return This path builder for further processing
     * @throws IndexOutOfBoundsException if the index is out of range (index < 0 || index >= size())
     */
    default PathBuilder setRawSegments(int index, byte[]... segments) throws IndexOutOfBoundsException {
        String[] encoded = new String[segments.length];
        for (int i = 0; i < segments.length; i++) {
            byte[] segment = segments[i];
            encoded[i] = segment == null ? null : UriEncoder.forSegment().encode(segment);
        }
        return setSegments(index, encoded);
    }

    /**
     * Overwrites the given segments to the specified position of this path, of which {@code null} values
     * will be ignored.
     *
     * <p> The invalid characters in the given segments will be converted to
     * {@code byte[]} with {@code UTF-8} charset, and then
     * <a href="https://tools.ietf.org/html/rfc3986#section-2.1">percent-encoded</a>.
     *
     * <p> If the size of the given segments (excluding {@code null} values) is larger
     * than {@code size() - index}, then the extra segments will be appended to this path. For example,
     * setting segments {@code {"s1", null, "s2"}} to a path builder that contains segments
     * {@code {"s3","s4"}} at index {@code 1} will result to {@code {"s3", "s1", "s2"}}.
     *
     * @param index    The index at which to set the first segment
     * @param segments The segments to be set
     * @return This path builder for further processing
     * @throws IndexOutOfBoundsException if the index is out of range (index < 0 || index >= size())
     */
    default PathBuilder setUtf8Segments(int index, String... segments) throws IndexOutOfBoundsException {
        String[] encoded = new String[segments.length];
        for (int i = 0; i < segments.length; i++) {
            String segment = segments[i];
            encoded[i] = segment == null ? null : UriEncoder.forSegment().encodeUtf8(segment);
        }
        return setSegments(index, encoded);
    }

    /**
     * Concatenates the given paths to this path builder, of which {@code null} values and empty paths will
     * be ignored.
     *
     * @param paths The paths to be concatenated
     * @return This path builder for further processing
     * @throws UriSyntaxException when there is something wrong with the syntax of paths
     */
    PathBuilder paths(String... paths) throws UriSyntaxException;

    /**
     * Concatenates the given paths to this path builder, of which {@code null} values and empty paths will
     * be ignored.
     *
     * <p> The given paths will be
     * <a href="https://tools.ietf.org/html/rfc3986#section-2.1">percent-encoded</a>.
     *
     * @param paths The paths to be concatenated
     * @return This path builder for further processing
     */
    default PathBuilder rawPaths(byte[]... paths) {
        String[] encoded = new String[paths.length];
        for (int i = 0; i < paths.length; i++) {
            byte[] path = paths[i];
            encoded[i] = path == null ? null : UriEncoder.forPath().encode(path);
        }
        return paths(encoded);
    }

    /**
     * Concatenates the given paths to this path builder, of which {@code null} values and empty paths will
     * be ignored.
     *
     * <p> The invalid characters in the given paths will be converted to
     * {@code byte[]} with {@code UTF-8} charset, and then
     * <a href="https://tools.ietf.org/html/rfc3986#section-2.1">percent-encoded</a>.
     *
     * @param paths The paths to be concatenated
     * @return This path builder for further processing
     */
    default PathBuilder utf8Paths(String... paths) {
        String[] encoded = new String[paths.length];
        for (int i = 0; i < paths.length; i++) {
            String path = paths[i];
            encoded[i] = path == null ? null : UriEncoder.forPath().encodeUtf8(path);
        }
        return paths(encoded);
    }

    /**
     * Removes the first given size segments from this path builder.
     * If given size is larger than or equal to the current size of this builder, then all
     * segments will be removed.
     * If given size is less than or equal to {@code 0}, then nothing will happen.
     *
     * @param size The size of segments to be removed
     * @return This path builder for further processing
     * @see #tear(int)
     * @see #truncate(int)
     */
    PathBuilder strip(int size);

    /**
     * Removes the last given size segments from this path builder.
     * If given size is larger than or equal to the current size of this builder, then all
     * segments will be removed.
     * If given size is less than or equal to {@code 0}, then nothing will happen.
     *
     * @param size The size of segments to be removed
     * @return This path builder for further processing
     * @see #strip(int)
     * @see #truncate(int)
     */
    PathBuilder tear(int size);

    /**
     * Truncates this path builder to the given size.
     *
     * <p> If the given size is less than the current size of this builder, then this builder
     * is truncated, discarding any segments beyond the new size of this builder.
     * If the given size is greater than or equal to the current size of this builder then
     * this builder is not modified.
     * If the given size is less than or equal to {@code 0}, then all segments will be removed.
     *
     * @param size The new size of this builder
     * @return This path builder for further processing
     * @see #strip(int)
     * @see #tear(int)
     */
    PathBuilder truncate(int size);

    /**
     * Removes all trailing empty segments of this builder.
     *
     * @return This path builder for further processing
     * @see #prune()
     */
    PathBuilder trim();

    /**
     * Removes all empty segments except the last trailing one.
     *
     * @return This path builder for further processing
     * @see #trim()
     */
    PathBuilder prune();

    /**
     * Removes the segment at the specified position in this builder.
     *
     * @param index The index of the segment to be removed
     * @return This path builder for further processing
     * @throws IndexOutOfBoundsException if the index is out of range ({@code index < 0 || index >= size()})
     */
    PathBuilder remove(int index) throws IndexOutOfBoundsException;

    /**
     * Removes all of the segments from this builder. The builder will be empty after this call returns.
     *
     * @return This path builder for further processing
     */
    PathBuilder clear();

    /**
     * Sets whether the final path component is absolute or not. If unset, the default is true.
     *
     * @param absolute Whether the final path is absolute or not
     * @return This path builder for further processing
     * @see Path#isAbsolute()
     */
    PathBuilder absolute(boolean absolute);

    /**
     * Builds and returns the final path component which in {@link Path#normalize() normalized form}.
     *
     * <p> If none segment is added to this builder, i.e., the size of this builder is 0,
     * an empty segment is added.
     *
     * @return The path component
     * @throws UriSyntaxException when there is something wrong with the syntax of path component
     */
    Path build() throws UriSyntaxException;

    /**
     * Returns the segments currently in this builder which is unmodifiable.
     *
     * @return the segments currently in this builder
     */
    List<String> segments();

    /**
     * Returns the number of segments in this builder.
     *
     * @return The number of segments
     */
    default int size() {
        return segments().size();
    }

}
