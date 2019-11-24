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
import org.embraceos.uri4j.internal.impl.PathImpl;

import javax.annotation.concurrent.Immutable;
import java.util.Iterator;
import java.util.List;
import java.util.Spliterator;
import java.util.function.Consumer;

/**
 * The path component contains data, usually organized in hierarchical
 * form, that, along with data in the non-hierarchical
 * {@link UriRef#query() query component} , serves to identify a resource
 * within the scope of the URI's scheme and naming authority (if any).
 *
 * <p> The path is terminated by the first question mark ("?") or number
 * sign ("#") character, or by the end of the URI.
 *
 * <pre>    path = path-abempty    ; begins with "/" or is empty
 *   / path-absolute   ; begins with "/" but not "//"
 *   / path-noscheme   ; begins with a non-colon segment
 *   / path-rootless   ; begins with a segment
 *   / path-empty      ; zero characters</pre>
 *
 * <p> A path consists of a sequence of path segments separated by a slash
 * ("/") character. A path is always defined for a URI, though the
 * defined path may be empty (zero length). Use of the slash character
 * to indicate hierarchy is only required when a URI will be used as the
 * context for relative references. For example, the URI
 * {@code <mailto:fred@example.com>} has a path of "fred@example.com", whereas
 * the URI {@code <foo://info.example.com?fred>} has an empty path.
 *
 * <p> The path segments "." and "..", also known as dot-segments, are
 * defined for relative reference within the path name hierarchy.  They
 * are intended for use at the beginning of a relative-path reference
 * to indicate relative position within the hierarchical
 * tree of names.
 *
 * @author Carrick Hong (洪灿昆)
 */
@API(status = API.Status.STABLE)
@Immutable
public interface Path extends Iterable<String>, Comparable<Path> {

    String SINGLE_DOT_SEGMENT = ".";
    String DOUBLE_DOTS_SEGMENT = "..";

    /**
     * Constructs a Path by parsing the given string.
     *
     * @param path The string to be parsed into a Path
     * @throws UriSyntaxException when there is something wrong with the syntax of path
     */
    static Path parse(String path) throws UriSyntaxException {
        return PathImpl.parse(path);
    }

    /**
     * Returns the path component as a whole string, which will not be null,
     * but may be empty (zero length).
     *
     * @return The path component as a whole string
     */
    String value();

    /**
     * Returns read-only segments of this path component, which will neither be null,
     * nor be empty, but in which any segment may be empty.
     *
     * @return All segments of this path component.
     */
    List<String> segments();

    /**
     * Tells whether or not this path component is empty.
     * <p> This path is empty if, and only if, {@link #value()} is empty. At this
     * situation, {@link #isAbsolute()} will return {@code false} and {@link #segments()}
     * will return a list contains only one empty segment.
     *
     * @return {@code true} if, and only if, this path component is empty
     */
    default boolean isEmpty() {
        return value().isEmpty();
    }

    /**
     * Tells whether or not this path component is absolute.
     * <p> This path is absolute if, and only if, {@link #value()} starts with slash character.
     *
     * @return {@code true} if, and only if, this path component is absolute
     */
    default boolean isAbsolute() {
        return value().startsWith("/");
    }

    /**
     * Resolves the given path component against this path component to obtain
     * target path component as per the resolution mechanism defined by
     * <a href="https://tools.ietf.org/html/rfc3986#section-5.2.2">RFC 3986</a>.
     *
     * <p> For each given path component (R), the following pseudocode describes an
     * algorithm for transforming R into its target path component (T):
     * <pre>
     * if (R starts-with "/") then
     *     T = remove_dot_segments(R);
     * else
     *     T = merge(this, R);
     *     T = remove_dot_segments(T);</pre>
     *
     * @param that The path component to be resolved against this path component
     * @return The resolved path component
     * @throws UriException if there is problem resolving path component
     */
    default Path resolve(Path that) throws UriException {
        if (that.isAbsolute()) {
            return that.normalize();
        } else {
            return mutate().tear(1)
                .segments(that.segments().toArray(new String[0]))
                .build().normalize();
        }
    }

    /**
     * <a href="https://tools.ietf.org/html/rfc3986#section-6.2.2.3">Normalizes</a> this path
     * component, and returns the normalized path component or just this path component if it
     * is already in normalized form.
     *
     * <p> The normalizing process is as follows:
     * <ol>
     *   <li> All {@code "."} segments are removed.
     *   <li> If a {@code ".."} segment is preceded by a non-{@code ".."} segment then both of
     *   these segments are removed.  This step is repeated until it is no longer applicable.
     *   <li> If this path is absolute, all leading double-dots segments is removed.
     *   <li> All percent-encoding octets will be capitalized.
     *   <li> All percent-encoding octets that corresponds to an unreserved character will be decoded.
     * </ol>
     *
     * <p> <b>Note:</b> Because some paths is invalid in some URIs according to
     * <a href="https://tools.ietf.org/html/rfc3986">RFC3986</a>, which includes:
     * <ul>
     *   <li>RFC3986#3: When authority is not present, the path cannot begin with two slash characters ("//").
     *   <li>RFC3986#4.2: A path segment that contains a colon character cannot be used as the first
     *   segment of a relative-path reference.
     * </ul>
     * <p> So in these two situations, a single-dot segment will be prepended to prevent ambiguous and conflict.
     *
     * <p> A normalized path will begin with one or more {@code ".."} segments if it's relative and
     * there were insufficient non-{@code ".."} segments preceding them to allow their removal.
     * A normalized path will begin with a {@code "."} segment if one was inserted as above.
     * Otherwise, a normalized path will not contain any {@code "."} or {@code ".."} segments.
     *
     * @return The normalized path component or just this path component if it is already in normalized form
     * @throws UriException if there is problem normalizing this path component
     */
    Path normalize() throws UriException;

    /**
     * Returns an iterator over segments of this path component.
     *
     * @return an Iterator
     */
    @Override
    default Iterator<String> iterator() {
        return segments().iterator();
    }

    /**
     * Performs the given action for each segments of this path component
     * until all segments have been processed or the action throws an
     * exception.  Actions are performed in the order of iteration, if that
     * order is specified.  Exceptions thrown by the action are relayed to the
     * caller.
     *
     * @param action The action to be performed for each segment
     * @throws NullPointerException if the specified action is null
     */
    @Override
    default void forEach(Consumer<? super String> action) {
        segments().forEach(action);
    }

    /**
     * Creates a {@link Spliterator} over the segments of this path component.
     *
     * @return a {@code Spliterator}
     */
    @Override
    default Spliterator<String> spliterator() {
        return segments().spliterator();
    }

    /**
     * Compares this Path with the given Path.
     *
     * <p> The ordering of Paths is determined by comparing their {@link #value()}.
     *
     * <p> Recommends to normalize this Path and the given Path before comparison to reduce the
     * probability of false negatives.
     *
     * <p> This method satisfies the general contract of the {@link Comparable#compareTo(Object)} method,
     * and is consistent with the {@link #equals(Object)} method.
     *
     * @param that The Path to be compared with this Path
     * @return A negative integer, zero, or a positive integer as this Path is
     * less than, equal to, or greater than the given Path
     * @see #equals(Object)
     * @see #normalize()
     */
    @Override
    default int compareTo(Path that) {
        return this.value().compareTo(that.value());
    }

    /**
     * Tests this Path for equality with another object.
     *
     * <p> If the given object is not a Path then this method immediately returns {@code false}.
     *
     * <p> The two Paths are considered to be equal if, and only if, their {@link #value()} are equal.
     *
     * <p> This method satisfies the general contract of the {@link Object#equals(Object) Object.equals} method,
     * and is consistent with the {@link #compareTo(Path)} method.
     *
     * @param that The object to which this Path is to be compared
     * @return {@code true} if, and only if, the given object is a Path that is identical to this Path
     * @see #hashCode()
     * @see #compareTo(Path)
     */
    @Override
    boolean equals(Object that);

    /**
     * Returns a hash-code value for this Path, which is same as the hash-code of {@link #value()}.
     * The hash code satisfies the general contract of the
     * {@link java.lang.Object#hashCode() Object.hashCode} method.
     *
     * @return A hash-code value for this Path
     * @see #equals(Object)
     */
    @Override
    int hashCode();

    /**
     * Returns the string representation of this path, i.e., {@link #value()}.
     *
     * @return The string representation of this path
     */
    @Override
    String toString();

    /**
     * Returns a builder to mutate path component.
     *
     * @return The builder
     */
    default PathBuilder mutate() {
        return PathBuilderImpl.from(this);
    }

}
