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
import org.embraceos.uri4j.internal.lang.Nullable;

import java.util.function.Consumer;

/**
 * Builder-style methods to build URI-references.
 *
 * <p> There are two types of component setter methods: {@code string} and {@code raw}.
 * For {@code string} methods, the name will be the same as the component, and the
 * given component is of type {@link String}, which will be checked for not allowed
 * characters, {@link UriSyntaxException} will be thrown if exist, e.g., {@link #query(String)}.
 * For {@code raw} methods, the name will be the capitalized name of the component with
 * prefix {@code raw}, and the given component is of type {@code byte[]}, which will be
 * <a href="https://tools.ietf.org/html/rfc3986#section-2.1">percent-encoded</a> to a string,
 * and then set it as the component of the final URI-reference, e.g., {@link #rawQuery(byte[])}.
 * Some component is not allowed to contain percent-encoded data, such as {@link #scheme(String)},
 * so there is not a {@code raw} method for it.
 *
 * <p> Because UTF-8 encoded string is used as components of URI-references so commonly, there
 * is also another type of component setter methods for convenience, i.e., the {@code utf8} methods.
 * For {@code utf8} methods, the name will be the capitalized name of the component with prefix
 * {@code utf8}, and the given component is of type {@link String}, in which the invalid
 * characters will be converted to {@code byte[]}, and then {@code percent-encode} the {@code byte[]},
 * e.g., {@link #utf8Query(String)}.
 *
 * <p> All {@code String} methods may defer character-checking to the the final step of building
 * process, i.e., the method {@link #build(String)} or {@link #build()}. Whether a {@code string}
 * component setter method will defer character-checking or not depends on the implementation.
 * Other than this, the {@code build} methods will do the syntax-checking, to check whether there
 * is something wrong with the syntax of URI-reference, and throw {@link UriSyntaxException} if it
 * is. For example, it's invalid for an URI-reference with scheme undefined to have a path component
 * begins with a segment contains colons.
 *
 * @author Carrick Hong (洪灿昆)
 * @see UriRef
 * @see Uri
 */
@API(status = API.Status.STABLE)
public interface UriBuilder {

    /**
     * Sets the scheme component of the final URI-reference, may be null to clear the scheme.
     *
     * @param scheme The given scheme component
     * @return This builder for further processing
     * @throws UriSyntaxException when there is something wrong with the syntax of URI-reference
     */
    UriBuilder scheme(@Nullable String scheme) throws UriSyntaxException;

    /**
     * Sets the authority component of the final URI-reference, may be null to clear the authority.
     *
     * @param authority The given authority component
     * @return This builder for further processing
     * @throws UriSyntaxException when there is something wrong with the syntax of URI-reference
     */
    UriBuilder authority(@Nullable String authority) throws UriSyntaxException;

    /**
     * Sets the userinfo component of the final URI-reference, may be null to clear the userinfo.
     *
     * @param userInfo The given userinfo component
     * @return This builder for further processing
     * @throws UriSyntaxException when there is something wrong with the syntax of URI-reference
     */
    UriBuilder userInfo(@Nullable String userInfo) throws UriSyntaxException;

    /**
     * Sets the host component of the final URI-reference, may be null to clear the host.
     *
     * @param host The given host component
     * @return This builder for further processing
     * @throws UriSyntaxException when there is something wrong with the syntax of URI-reference
     */
    UriBuilder host(@Nullable String host) throws UriSyntaxException;

    /**
     * Sets the port component of the final URI-reference, may be null to clear the port.
     *
     * @param port The given port component
     * @return This builder for further processing
     * @throws UriSyntaxException when there is something wrong with the syntax of URI-reference
     * @see #port(int)
     */
    UriBuilder port(@Nullable String port) throws UriSyntaxException;

    /**
     * Sets the port component of the final URI-reference, may be {@code -1} to clear the port.
     *
     * @param port The given port component
     * @return This builder for further processing
     * @throws UriSyntaxException when there is something wrong with the syntax of URI-reference
     * @see #port(String)
     */
    default UriBuilder port(int port) throws UriSyntaxException {
        if (port == -1) {
            return port(null);
        } else {
            return port(port + "");
        }
    }

    /**
     * Sets the path component of the final URI-reference, the path can be further manipulated
     * by {@link #path(Consumer)}.
     *
     * @param path The given path component
     * @return This builder for further processing
     * @see #path(Consumer)
     */
    UriBuilder path(Path path);

    /**
     * Manipulates the path component of the final URI-reference.
     *
     * @param action The action to manipulate the path component
     * @return This builder for further processing
     * @see #path(Path)
     */
    UriBuilder path(Consumer<PathBuilder> action);

    /**
     * Sets the query component of the final URI-reference, may be null to clear the query.
     *
     * @param query The given query component
     * @return This builder for further processing
     * @throws UriSyntaxException when there is something wrong with the syntax of URI-reference
     */
    UriBuilder query(@Nullable String query) throws UriSyntaxException;

    /**
     * Sets the fragment component of the final URI-reference, may be null to clear the fragment.
     *
     * @param fragment The given fragment component
     * @return This builder for further processing
     * @throws UriSyntaxException when there is something wrong with the syntax of URI-reference
     */
    UriBuilder fragment(@Nullable String fragment) throws UriSyntaxException;

    /**
     * Sets the userinfo component of the final URI-reference, may be null to clear the userinfo.
     *
     * <p> The given userinfo component will be
     * <a href="https://tools.ietf.org/html/rfc3986#section-2.1">percent-encoded</a>
     * before setting, if it is not {@code null}.
     *
     * @param userInfo The given {@code raw} userinfo component
     * @return This builder for further processing
     * @see #userInfo(String)
     */
    UriBuilder rawUserInfo(@Nullable byte[] userInfo);

    /**
     * Sets the host component of the final URI-reference, may be null to clear the host.
     *
     * <p> The given host component will be
     * <a href="https://tools.ietf.org/html/rfc3986#section-2.1">percent-encoded</a>
     * before setting, if it is not {@code null}.
     *
     * <p> The given host component will be considered as a
     * <a href="https://tools.ietf.org/html/rfc3986#section-3.2.2">reg-name</a>
     * because only {@code reg-name} is allowed to contain percent-encoded data,
     * among all types of the host component.
     *
     * <p><b>Caution:</b> If you want to convert {@link Uri} to {@link java.net.URI} in future,
     * don't invoke this method, because {@link java.net.URI} is compliant
     * with <a href="https://tools.ietf.org/html/rfc2396">RFC 2396</a> that doesn't allow
     * host component to contain percent-encoded data.
     *
     * @param host The given {@code raw} host component
     * @return This builder for further processing
     * @see #host(String)
     */
    UriBuilder rawHost(@Nullable byte[] host);

    /**
     * Sets the query component of the final URI-reference, may be null to clear the query.
     *
     * <p> The given query component will be
     * <a href="https://tools.ietf.org/html/rfc3986#section-2.1">percent-encoded</a>
     * before setting, if it is not {@code null}.
     *
     * @param query The given {@code raw} query component
     * @return This builder for further processing
     * @see #query(String)
     */
    UriBuilder rawQuery(@Nullable byte[] query);

    /**
     * Sets the fragment component of the final URI-reference, may be null to clear the fragment.
     *
     * <p> The given fragment component will be
     * <a href="https://tools.ietf.org/html/rfc3986#section-2.1">percent-encoded</a>
     * before setting, if it is not {@code null}.
     *
     * @param fragment The given {@code raw} fragment component
     * @return This builder for further processing
     * @see #fragment(String)
     */
    UriBuilder rawFragment(@Nullable byte[] fragment);

    /**
     * Sets the userinfo component of the final URI-reference, may be null to clear the userinfo.
     *
     * <p> The invalid characters in the given userinfo component will be converted to
     * {@code byte[]} with {@code UTF-8} charset, and then
     * <a href="https://tools.ietf.org/html/rfc3986#section-2.1">percent-encoded</a>
     * the {@code byte[]} before setting, if it is not {@code null}.
     *
     * @param userInfo The given {@code raw} userinfo component
     * @return This builder for further processing
     * @see #userInfo(String)
     */
    UriBuilder utf8UserInfo(@Nullable String userInfo);

    /**
     * Sets the host component of the final URI-reference, may be null to clear the host.
     *
     * <p> The invalid characters in the given host component will be converted to
     * {@code byte[]} with {@code UTF-8} charset, and then
     * <a href="https://tools.ietf.org/html/rfc3986#section-2.1">percent-encoded</a>
     * the {@code byte[]} before setting, if it is not {@code null}.
     *
     * <p> The given host component will be considered as a
     * <a href="https://tools.ietf.org/html/rfc3986#section-3.2.2">reg-name</a>
     * because only {@code reg-name} is allowed to contain percent-encoded data,
     * among all types of the host component.
     *
     * <p><b>Caution:</b> If you want to convert {@link Uri} to {@link java.net.URI} in future,
     * don't invoke this method, because {@link java.net.URI} is compliant
     * with <a href="https://tools.ietf.org/html/rfc2396">RFC 2396</a> that doesn't allow
     * host component to contain percent-encoded data.
     *
     * @param host The given {@code raw} host component
     * @return This builder for further processing
     * @see #host(String)
     */
    UriBuilder utf8Host(@Nullable String host);

    /**
     * Sets the query component of the final URI-reference, may be null to clear the query.
     *
     * <p> The invalid characters in the given query component will be converted to
     * {@code byte[]} with {@code UTF-8} charset, and then
     * <a href="https://tools.ietf.org/html/rfc3986#section-2.1">percent-encoded</a>
     * the {@code byte[]} before setting, if it is not {@code null}.
     *
     * @param query The given {@code raw} query component
     * @return This builder for further processing
     * @see #query(String)
     */
    UriBuilder utf8Query(@Nullable String query);

    /**
     * Sets the fragment component of the final URI-reference, may be null to clear the fragment.
     *
     * <p> The invalid characters in the given fragment component will be converted to
     * {@code byte[]} with {@code UTF-8} charset, and then
     * <a href="https://tools.ietf.org/html/rfc3986#section-2.1">percent-encoded</a>
     * the {@code byte[]} before setting, if it is not {@code null}.
     *
     * @param fragment The given {@code raw} fragment component
     * @return This builder for further processing
     * @see #fragment(String)
     */
    UriBuilder utf8Fragment(@Nullable String fragment);

    /**
     * Builds and returns the final URI-reference.
     * <p> If the scheme component is defined, the returned URI-reference will be a {@link Uri},
     * otherwise it will be a {@link UriRef}.
     *
     * @param <T> The type of the URI-reference, which is either {@link Uri} or {@link UriRef}
     * @return The URI-reference
     * @throws UriSyntaxException when there is something wrong with the syntax of URI-reference
     * @throws ClassCastException if the returned URI-reference is not a instance of type {@link T}
     * @see #build(String)
     */
    <T extends UriRef> T build() throws UriSyntaxException, ClassCastException;

    /**
     * Sets the scheme component of the final URI, then builds and returns the final URI.
     *
     * @param scheme The given scheme component
     * @return The URI
     * @throws UriSyntaxException when there is something wrong with the syntax of URI-reference
     * @see #build()
     */
    default Uri build(String scheme) throws UriSyntaxException {
        return this.scheme(scheme).build();
    }

}
