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
 * This interface represents the URI as defined by
 * <a href="https://tools.ietf.org/html/rfc3986">RFC3986</a>.
 *
 * <p> A Uniform Resource Identifier (URI) provides a simple and extensible
 * means for identifying a resource.
 *
 * <p></p>
 *
 * <p> The URI syntax is organized hierarchically, with components listed in
 * order of decreasing significance from left to right.  For some URI
 * schemes, the visible hierarchy is limited to the scheme itself:
 * everything after the scheme component delimiter (":") is considered
 * opaque to URI processing.  Other URI schemes make the hierarchy
 * explicit and visible to generic parsing algorithms.
 *
 * <p> The generic syntax uses the slash ("/"), question mark ("?"), and
 * number sign ("#") characters to delimit components that are
 * significant to the generic parser's hierarchical interpretation of an
 * identifier.  In addition to aiding the readability of such
 * identifiers through the consistent use of familiar syntax, this
 * uniform representation of hierarchy across naming schemes allows
 * scheme-independent references to be made relative to that hierarchy.
 *
 * <p></p>
 *
 * <p> The URI syntax provides a method of encoding data, presumably for the
 * sake of identifying a resource, as a sequence of characters.
 *
 * <p> A URI is composed from a limited set of characters consisting of
 * digits, letters, and a few graphic symbols.  A reserved subset of
 * those characters may be used to delimit syntax components within a
 * URI while the remaining characters, including both the unreserved set
 * and those reserved characters not acting as delimiters, define each
 * component's identifying data.
 *
 * <p> For more information about the limited set of characters and encoding
 * mechanism, see <a href="https://tools.ietf.org/html/rfc3986#section-2">RFC 3986</a>.
 *
 * <p></p>
 *
 * <p> The generic URI syntax consists of a hierarchical sequence of
 * components referred to as the scheme, authority, path, query, and
 * fragment.
 *
 * <pre>    URI = scheme ":" hier-part [ "?" query ] [ "#" fragment ]
 *
 *  hier-part = "//" authority path-abempty
 *            / path-absolute
 *            / path-rootless
 *            / path-empty</pre>
 *
 * <p> The scheme and path components are required, though the path may be
 * empty (no characters).  When authority is present, the path must
 * either be empty or begin with a slash ("/") character.  When
 * authority is not present, the path cannot begin with two slash
 * characters ("//").
 *
 * <p> The following are two example URIs and their component parts:
 *
 * <pre>     foo://example.com:8042/over/there?name=ferret#nose
 *   \_/   \______________/\_________/ \_________/ \__/
 *    |           |            |            |        |
 * scheme     authority       path        query   fragment
 *    |   _____________________|__
 *   / \ /                        \
 *   urn:example:animal:ferret:nose</pre>
 *
 * @author Carrick Hong (洪灿昆)
 * @see UriRef
 * @see java.net.URI
 */
@API(status = API.Status.STABLE)
@Immutable
public interface Uri extends UriRef, Comparable<Uri> {

    /**
     * Returns the <a href="https://tools.ietf.org/html/rfc3986#section-3.1">scheme </a>
     * component of this URI.
     *
     * <p> Each URI begins with a scheme name that refers to a specification for
     * assigning identifiers within that scheme.  As such, the URI syntax is
     * a federated and extensible naming system wherein each scheme's
     * specification may further restrict the syntax and semantics of
     * identifiers using that scheme.
     *
     * <p> Scheme names consist of a sequence of characters beginning with a
     * letter and followed by any combination of letters, digits, plus
     * ("+"), period ("."), or hyphen ("-").  Although schemes are case-
     * insensitive, the canonical form is lowercase and documents that
     * specify schemes must do so with lowercase letters.
     *
     * <pre>      scheme = ALPHA *( ALPHA / DIGIT / "+" / "-" / "." )</pre>
     *
     * @return The scheme component of the URI, which is always defined and not empty
     */
    String scheme();

    /**
     * Returns the <a href="https://tools.ietf.org/html/rfc3986#section-3.2">authority</a>
     * component of this URI.
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
     * sign ("#") character, or by the end of the URI.
     *
     * <pre>   authority = [ userinfo "@" ] host [ ":" port ]</pre>
     *
     * @return The authority component of this URI, or {@code null} if the authority is undefined
     * @see #userInfo()
     * @see #host()
     * @see #port()
     */
    @Override
    @Nullable
    default String authority() {
        return UriRef.super.authority();
    }

    /**
     * Returns the <a href="https://tools.ietf.org/html/rfc3986#section-3.2.1">user information</a>
     * component of this URI, this is a subcomponent of the authority component.
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
     * @return The user information component of this URI,
     * or {@code null} if the user information component is undefined
     * @see #authority()
     */
    @Override
    @Nullable
    String userInfo();

    /**
     * Returns the <a href="https://tools.ietf.org/html/rfc3986#section-3.2.2">host</a>
     * component of this URI, this is a subcomponent of the authority component.
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
     * @return The host component of this URI,
     * or {@code null} if the host component is undefined
     * @see #authority()
     */
    @Override
    @Nullable
    String host();

    /**
     * Returns the <a href="https://tools.ietf.org/html/rfc3986#section-3.2.3">port</a>
     * component of this URI, this is a subcomponent of the authority component.
     *
     * <p> The port subcomponent of authority is designated by an optional port
     * number in decimal following the host and delimited from it by a
     * single colon (":") character.
     *
     * <pre>   port = *DIGIT</pre>
     *
     * @return The port component of this URI,
     * or {@code null} if the port component is undefined
     * @see #authority()
     * @see #portAsInt()
     */
    @Override
    @Nullable
    String port();

    /**
     * Returns the <a href="https://tools.ietf.org/html/rfc3986#section-3.2.3">port</a>
     * component of this URI as an int value. If the port component is undefined
     * or empty, the returned value would be {@code -1}.
     *
     * @return The port component of this URI as an int value,
     * or {@code -1} if the port component is undefined or empty
     * @throws ArithmeticException if the {@code port} overflows an int
     */
    @Override
    default int portAsInt() throws ArithmeticException {
        return UriRef.super.portAsInt();
    }

    /**
     * Returns the <a href="https://tools.ietf.org/html/rfc3986#section-3.3">path</a>
     * component of this URI.
     *
     * <p> The path component contains data, usually organized in hierarchical
     * form, that, along with data in the non-hierarchical {@link #query() query component},
     * serves to identify a resource within the scope of the URI's scheme
     * and naming authority (if any).
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
     * to indicate relative position within the hierarchical tree of names.
     *
     * @return The path component ot this URI
     */
    @Override
    Path path();

    /**
     * Returns the <a href="https://tools.ietf.org/html/rfc3986#section-3.4">query</a>
     * component of this URI.
     *
     * <p> The query component contains non-hierarchical data that, along with
     * data in the {@link #path() path component}, serves to identify a
     * resource within the scope of the URI's scheme and naming authority (if any).
     *
     * <p> The query component is indicated by the first question
     * mark ("?") character and terminated by a number sign ("#") character
     * or by the end of the URI.
     *
     * <pre>   query = *( pchar / "/" / "?" )</pre>
     *
     * @return The query component of this URI,
     * or {@code null} is the query component is undefined
     */
    @Override
    @Nullable
    String query();

    /**
     * Returns the <a href="https://tools.ietf.org/html/rfc3986#section-3.5">fragment</a>
     * component of this URI.
     *
     * <p> The fragment identifier component of a URI allows indirect
     * identification of a secondary resource by reference to a primary
     * resource and additional identifying information.  The identified
     * secondary resource may be some portion or subset of the primary
     * resource, some view on representations of the primary resource, or
     * some other resource defined or described by those representations.
     *
     * <p> A fragment identifier component is indicated by the presence of a
     * number sign ("#") character and terminated by the end of the URI.
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
     * @return The fragment component of this URI,
     * or {@code null} if the fragment component is undefined
     */
    @Override
    @Nullable
    String fragment();

    /**
     * Tells whether or not this URI is
     * <a href="https://tools.ietf.org/html/rfc3986#section-4.3">absolute-URI</a>.
     *
     * <p> Some protocol elements allow only the absolute form of a URI without
     * a fragment identifier. For example, defining a base URI for later
     * use by relative references calls for an absolute-URI syntax rule that
     * does not allow a fragment.
     *
     * <pre>    absolute-URI = scheme ":" hier-part [ "?" query ]</pre>
     *
     * @return {@code true} if, and only if, this URI is absolute-URI.
     */
    default boolean isAbsolute() {
        return fragment() == null;
    }

    /**
     * Resolves the given URI-reference against this URI to obtain target URI
     * as per the resolution mechanism defined by
     * <a href="https://tools.ietf.org/html/rfc3986#section-5">RFC 3986</a>.
     *
     * <p> If the fragment component of this URI is defined, then it would be stripped
     * within the process of reference resolution, i.e., it will not be used to compose
     * the target URI, the fragment component of the target URI is completely depends
     * on the given URI-reference, as the semantics of fragment are defined by the type
     * of resource specified by the target URI.
     *
     * <p> The resolution process may be non-strict, so that ignores the scheme component
     * of the URI-reference, if it is identical to the scheme component of this URI.
     *
     * <p> For more information about reference resolution, see
     * <a href="https://tools.ietf.org/html/rfc3986#section-5">RFC 3986 (section 5.2 and 5.3)</a>.
     *
     * @param uriRef The URI-reference to be resolved against this URI
     * @param strict Whether or not the resolution process is strict
     * @return The resolved target URI
     * @throws UriException if there is problem resolving URI-reference
     * @see #resolve(UriRef)
     */
    Uri resolve(UriRef uriRef, boolean strict) throws UriException;

    /**
     * Resolves the given URI-reference against this URI strictly to obtain target URI
     * as per the resolution mechanism defined by
     * <a href="https://tools.ietf.org/html/rfc3986#section-5">RFC 3986</a>.
     *
     * <p> Same as invoke method: <pre>    resolve(uriRef, true)</pre>
     *
     * <p> If the fragment component of this URI is defined, then it would be stripped
     * within the process of reference resolution, i.e., it will not be used to compose
     * the target URI, the fragment component of the target URI is completely depends
     * on the given URI-reference, as the semantics of fragment are defined by the type
     * of resource specified by the target URI.
     *
     * <p> For more information about reference resolution, see
     * <a href="https://tools.ietf.org/html/rfc3986#section-5">RFC 3986 (section 5.2 and 5.3)</a>.
     *
     * @param uriRef The URI-reference to be resolved against this URI
     * @return The resolved target URI
     * @throws UriException if there is problem resolving URI-reference
     * @see #resolve(UriRef, boolean)
     */
    default Uri resolve(UriRef uriRef) throws UriException {
        return resolve(uriRef, true);
    }

    /**
     * <a href="https://tools.ietf.org/html/rfc3986#section-6">Normalizes</a> this URI,
     * and returns the normalized URI or just this URI if it is already in normalized form.
     *
     * <p> One of the most common operations on URIs is simple comparison:
     * determining whether two URIs are equivalent without using the URIs to
     * access their respective resource(s).
     *
     * <p> Proper normalization can reduce the probability of false negatives when compares
     * two URIs. <a href="https://tools.ietf.org/html/rfc3986#section-6">RFC 3986</a> defines
     * three levels of normalization, with the amount of processing required increases but
     * the probability of false negatives decreases:
     *
     * <ol>
     *   <li><a href="https://tools.ietf.org/html/rfc3986#section-6.2.2">Syntax-Based Normalization</a></li>
     *   <li><a href="https://tools.ietf.org/html/rfc3986#section-6.2.3">Scheme-Based Normalization</a></li>
     *   <li><a href="https://tools.ietf.org/html/rfc3986#section-6.2.4">Protocol-Based Normalization</a></li>
     * </ol>
     *
     * <p> This method employs the {@code Syntax-Based normalization} to normalize this URI.
     *
     * @return The normalized URI or just this URI if it is already in normalized form
     * @throws UriException if there is problem normalizing this URI
     */
    Uri normalize() throws UriException;

    /**
     * Compares this URI with the given URI.
     *
     * <p> One of the most common operations on URIs is simple comparison:
     * determining whether two URIs are equivalent without using the URIs to
     * access their respective resource(s).
     *
     * <p> When comparing corresponding components of two URIs, if one
     * component is undefined but the other is defined then the first is
     * considered to be less than the second.  Unless otherwise noted, string
     * components are ordered according to their natural, case-sensitive
     * ordering as defined by the {@link String#compareTo(String)} method.
     *
     * <p> The ordering of URIs is defined as follows:
     *
     * <ol>
     *   <li> Compares the scheme components, <b>without regard to case</b>.
     *   <li> Compares the host components, <b>without regard to case</b>.
     *   <li> The URI with empty port component is considered to be less than the URI
     *   with non-empty port component. If both URIs contain non-empty port component,
     *   compares the port components <b>as numbers</b>.
     *   <li> Compares the userinfo components.
     *   <li> Compares the path components.
     *   <li> Compares the query components.
     *   <li> Compares the fragment components.
     * </ol>
     *
     * <p> Recommends to normalize this URI and the given URI before comparison to reduce the
     * probability of false negatives.
     *
     * <p> This method satisfies the general contract of the {@link Comparable#compareTo(Object)} method,
     * and is consistent with the {@link #equals(Object)} method.
     *
     * @param that The URI to be compared with this URI
     * @return A negative integer, zero, or a positive integer as this URI is
     * less than, equal to, or greater than the given URI
     * @see #equals(Object)
     * @see #normalize()
     */
    @Override
    int compareTo(Uri that);

    /**
     * Tests this URI for equality with another object.
     *
     * <p> If the given object is not a URI then this method immediately
     * returns {@code false}.
     *
     * <p> The two URIs are considered to be equal if, and only if, following
     * conditions are all met:
     *
     * <ul>
     *   <li> Their scheme components are equal without regard to case.
     *   <li> Their host components are either both undefined or else equal without regard to case.
     *   <li> Their port components are either both undefined or else equal.
     *   <li> Their userinfo components are either both undefined or else equal.
     *   <li> Their path components are equal.
     *   <li> Their query components are either both undefined or else equal.
     *   <li> Their fragment components are either both undefined or else equal.
     * </ul>
     *
     * <p> Recommends to normalize this URI and the given URI before comparison to reduce the
     * probability of false negatives.
     *
     * <p> This method satisfies the general contract of the {@link Object#equals(Object) Object.equals} method,
     * and is consistent with the {@link #compareTo(Uri)} method.
     *
     * @param that The object to which this URI is to be compared
     * @return {@code true} if, and only if, the given object is a URI that is identical to this URI
     * @see #compareTo(Uri)
     * @see #hashCode()
     * @see #normalize()
     */
    @Override
    boolean equals(@Nullable Object that);

    /**
     * Returns a hash-code value for this URI.  The hash code is based upon all
     * of the URI's components, and satisfies the general contract of the
     * {@link Object#hashCode() Object.hashCode} method. For {@link #scheme()}
     * or {@link #host()} component, lowercase it if defined during hash-code calculation.
     *
     * @return A hash-code value for this URI
     * @see #equals(Object)
     */
    @Override
    int hashCode();

    /**
     * Returns the content of this URI as a string.
     *
     * @return The string form of this URI
     */
    @Override
    String toString();

    /**
     * Returns a builder to mutate components of this URI.
     *
     * @return The builder
     */
    UriBuilder mutate();

    /**
     * Returns the {@link URI} converted from this {@link Uri}.
     *
     * @return The converted {@link URI}
     * @throws UriException when there is something wrong with converting
     */
    @Override
    URI toJdk() throws UriException;

}
