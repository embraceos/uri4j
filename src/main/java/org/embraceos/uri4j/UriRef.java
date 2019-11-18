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

import javax.annotation.concurrent.Immutable;
import java.net.URI;

/**
 * This interface represents the URI reference as defined by
 * <a href="https://tools.ietf.org/html/rfc3986#section-4.1">RFC3986</a>.
 *
 * <p> URI-reference is used to denote the most common usage of a resource
 * identifier.
 *
 * <p> A URI-reference is either a URI or a relative reference.  If the
 * URI-reference's prefix does not match the syntax of a scheme followed
 * by its colon separator, then the URI-reference is a relative
 * reference.
 *
 * <pre>   URI-reference = URI / relative-ref</pre>
 *
 * <p> A relative reference takes advantage of the hierarchical syntax to
 * express a URI reference relative to the name space of another hierarchical URI.
 *
 * @author Carrick Hong (洪灿昆)
 * @see Uri
 * @see java.net.URI
 */
@API(status = API.Status.STABLE)
@Immutable
public interface UriRef {

    /**
     * Returns the <a href="https://tools.ietf.org/html/rfc3986#section-3.2">authority</a>
     * component of this URI-reference.
     *
     * <p> Many URI schemes include a hierarchical element for a naming
     * authority so that governance of the name space defined by the
     * remainder of the URI is delegated to that authority (which may, in
     * turn, delegate it further).  The generic syntax provides a common
     * means for distinguishing an authority based on a registered name or
     * server address, along with optional port and user information.
     *
     * <p> The authority component is preceded by a double slash ("//") and is
     * terminated by the next slash ("/"), question mark ("?"), or number
     * sign ("#") character, or by the end of the URI-reference.
     *
     * <pre>   authority = [ userinfo "@" ] host [ ":" port ]</pre>
     *
     * @return The authority component of this URI-reference,
     * or {@code null} if the authority is undefined
     * @see #userInfo()
     * @see #host()
     * @see #port()
     */
    @Nullable
    String authority();

    /**
     * Returns the <a href="https://tools.ietf.org/html/rfc3986#section-3.2.1">user information</a>
     * component of this URI-reference, this is a subcomponent of the authority component.
     *
     * <p> The userinfo subcomponent may consist of a user name and, optionally,
     * scheme-specific information about how to gain authorization to access
     * the resource.
     *
     * <p>The user information, if present, is followed by a
     * commercial at-sign ("@") that delimits it from the host.
     *
     * <pre>    userinfo = *( unreserved / pct-encoded / sub-delims / ":" )</pre>
     *
     * @return The user information component of this URI-reference,
     * or {@code null} if the user information component is undefined
     * @see #authority()
     */
    @Nullable
    String userInfo();

    /**
     * Returns the <a href="https://tools.ietf.org/html/rfc3986#section-3.2.2">host</a>
     * component of this URI-reference, this is a subcomponent of the authority component.
     *
     * <p> The host subcomponent is always defined if the authority component is defined,
     * but may be empty (zero length).
     *
     * <p> The host subcomponent of authority is identified by an IP literal
     * encapsulated within square brackets, an IPv4 address in dotted-decimal
     * form, or a registered name. The host subcomponent is case-insensitive.
     *
     * <pre>   host = IP-literal / IPv4address / reg-name</pre>
     *
     * @return The host component of this URI-reference,
     * or {@code null} if the host component is undefined
     * @see #authority()
     */
    @Nullable
    String host();

    /**
     * Returns the <a href="https://tools.ietf.org/html/rfc3986#section-3.2.3">port</a>
     * component of this URI-reference, this is a subcomponent of the authority component.
     *
     * <p> The port subcomponent of authority is designated by an optional port
     * number in decimal following the host and delimited from it by a
     * single colon (":") character.
     *
     * <pre>   port = *DIGIT</pre>
     *
     * @return The port component of this URI-reference,
     * or {@code null} if the port component is undefined
     * @see #authority()
     * @see #portAsInt()
     */
    @Nullable
    String port();

    /**
     * Returns the <a href="https://tools.ietf.org/html/rfc3986#section-3.2.3">port</a>
     * component of this URI-reference as an int value. If the port component is undefined
     * or empty, the returned value would be {@code -1}.
     *
     * @return The port component of this URI-reference as an int value,
     * or {@code -1} if the port component is undefined or empty
     * @throws ArithmeticException if the {@code port} overflows an int
     */
    int portAsInt() throws ArithmeticException;

    /**
     * Returns the <a href="https://tools.ietf.org/html/rfc3986#section-3.3">path</a>
     * component of this URI-reference.
     *
     * <p> The path component contains data, usually organized in hierarchical
     * form, that, along with data in the non-hierarchical {@link #query() query component},
     * serves to identify a resource within the scope of the URI's scheme
     * and naming authority (if any).
     *
     * <p> The path is terminated by the first question mark ("?") or number
     * sign ("#") character, or by the end of the URI-reference.
     *
     * <pre>    path = path-abempty    ; begins with "/" or is empty
     *   / path-absolute   ; begins with "/" but not "//"
     *   / path-noscheme   ; begins with a non-colon segment
     *   / path-rootless   ; begins with a segment
     *   / path-empty      ; zero characters</pre>
     *
     * <p> A path consists of a sequence of path segments separated by a slash
     * ("/") character. A path is always defined for a URI-reference, though the
     * defined path may be empty (zero length). Use of the slash character
     * to indicate hierarchy is only required when a URI will be used as the
     * context for relative references. For example, the URI
     * {@code <mailto:fred@example.com>} has a path of "fred@example.com", whereas
     * the URI {@code <foo://info.example.com?fred>} has an empty path.
     *
     * <p> The path segments "." and "..", also known as dot-segments, are
     * defined for relative reference within the path name hierarchy.  They
     * are intended for use at the beginning of a relative-path reference
     * to indicate relative position within the hierarchical tree of names.
     *
     * @return The path component ot this URI-reference
     */
    Path path();

    /**
     * Returns the <a href="https://tools.ietf.org/html/rfc3986#section-3.4">query</a>
     * component of this URI-reference.
     *
     * <p> The query component contains non-hierarchical data that, along with
     * data in the {@link #path() path component}, serves to identify a
     * resource within the scope of the URI's scheme and naming authority (if any).
     *
     * <p> The query component is indicated by the first question
     * mark ("?") character and terminated by a number sign ("#") character
     * or by the end of the URI-reference.
     *
     * <pre>   query = *( pchar / "/" / "?" )</pre>
     *
     * @return The query component of this URI-reference,
     * or {@code null} is the query component is undefined
     */
    @Nullable
    String query();

    /**
     * Returns the <a href="https://tools.ietf.org/html/rfc3986#section-3.5">fragment</a>
     * component of this URI-reference.
     *
     * <p> The fragment identifier component of a URI-reference allows indirect
     * identification of a secondary resource by reference to a primary
     * resource and additional identifying information.  The identified
     * secondary resource may be some portion or subset of the primary
     * resource, some view on representations of the primary resource, or
     * some other resource defined or described by those representations.
     *
     * <p> A fragment identifier component is indicated by the presence of a
     * number sign ("#") character and terminated by the end of the URI-reference.
     *
     * <pre>   fragment = *( pchar / "/" / "?" )</pre>
     *
     * <p> The semantics of a fragment identifier are defined by the set of
     * representations that might result from a retrieval action on the
     * primary resource.  The fragment's format and resolution is therefore
     * dependent on the media type [RFC2046] of a potentially retrieved
     * representation, even though such a retrieval is only performed if the
     * URI is dereferenced.  If no such representation exists, then the
     * semantics of the fragment are considered unknown and are effectively
     * unconstrained.  Fragment identifier semantics are independent of the
     * URI scheme and thus cannot be redefined by scheme specifications.
     *
     * <p> Fragment identifiers have a special role in information retrieval
     * systems as the primary form of client-side indirect referencing,
     * allowing an author to specifically identify aspects of an existing
     * resource that are only indirectly provided by the resource owner.  As
     * such, the fragment identifier is not used in the scheme-specific
     * processing of a URI; instead, the fragment identifier is separated
     * from the rest of the URI prior to a dereference, and thus the
     * identifying information within the fragment itself is dereferenced
     * solely by the user agent, regardless of the URI scheme.  Although
     * this separate handling is often perceived to be a loss of
     * information, particularly for accurate redirection of references as
     * resources move over time, it also serves to prevent information
     * providers from denying reference authors the right to refer to
     * information within a resource selectively.  Indirect referencing also
     * provides additional flexibility and extensibility to systems that use
     * URIs, as new media types are easier to define and deploy than new
     * schemes of identification.
     *
     * @return The fragment component of this URI-reference,
     * or {@code null} if the fragment component is undefined
     */
    @Nullable
    String fragment();

    /**
     * Tests this URI reference for equality with another object.
     *
     * <p> If the given object is not a URI reference then this method immediately returns {@code false}.
     * If the given object is a URI then this method immediately returns {@code false}
     *
     * <p> The two URI references are considered to be equal if, and only if, following
     * conditions are all met:
     *
     * <ul>
     *   <li> Their host components are either both be undefined or else be equal without regard to case.
     *   <li> Their port components are either both be undefined or else be equal.
     *   <li> Their userinfo components are either both be undefined or else be equal.
     *   <li> Their path components are equal.
     *   <li> Their query components are either both be undefined or else be equal.
     *   <li> Their fragment components are either both be undefined or else be equal.
     * </ul>
     *
     * <p> This method satisfies the general contract of the {@link Object#equals(Object) Object.equals} method.
     *
     * @param that The object to which this URI reference is to be compared
     * @return {@code true} if, and only if, the given object is a URI reference that is
     * identical to this URI reference
     * @see #hashCode()
     */
    @Override
    boolean equals(@Nullable Object that);

    /**
     * Returns a hash-code value for this URI reference. The hash code is based upon all
     * of the URI reference's components, and satisfies the general contract of the
     * {@link Object#hashCode() Object.hashCode} method.
     *
     * @return A hash-code value for this URI reference
     * @see #equals(Object)
     */
    @Override
    int hashCode();

    /**
     * Returns the content of this URI-reference as a string.
     *
     * @return The string form of this URI-reference
     */
    @Override
    String toString();

    /**
     * Returns a builder to mutate components of this URI-reference.
     *
     * @return The builder
     */
    UriBuilder mutate();

    /**
     * Returns the {@link URI} converted from this {@link UriRef}.
     *
     * @return The converted {@link URI}
     * @throws UriException when there is something wrong with converting
     */
    URI toJdk() throws UriException;

}
