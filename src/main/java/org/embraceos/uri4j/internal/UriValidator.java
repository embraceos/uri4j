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

package org.embraceos.uri4j.internal;

import org.embraceos.uri4j.UriSyntaxException;
import org.embraceos.uri4j.internal.lang.Nullable;

import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.function.IntConsumer;

/**
 * @author Carrick Hong (洪灿昆)
 */
public class UriValidator {

    public static final UriValidator INSTANCE = new UriValidator();

    private static final AsciiMask IPV_FUTURE_MASK = AsciiMask.combine(UriMasks.UNRESERVED, UriMasks.SUB_DELIMS, AsciiMask.allow(":"));
    private static final AsciiMask IPV6_ADDRESS_MASK = AsciiMask.combine(UriMasks.HEXDIG, UriMasks.DIGIT, AsciiMask.allow(":."));

    public InternalUri validate(InternalUri uri) throws UriSyntaxException {
        // validate components individually
        validateScheme(uri.scheme());
        validateUserInfo(uri.userInfo());
        validateHost(uri.host());
        validatePort(uri.port());
        validatePath(uri.path());
        validateQuery(uri.query());
        validateFragment(uri.fragment());

        // validate correlations between components
        validateCorrelations(uri);
        return uri;
    }

    public void validateScheme(@Nullable String scheme) throws UriSyntaxException {
        if (scheme == null) return;

        if (scheme.isEmpty()) throw new UriSyntaxException("empty scheme");
        check(UriMasks.SCHEME_FC, scheme, 0, 1, false, index -> {
            throw new UriSyntaxException("invalid char in scheme at index 0: " + scheme);
        });
        check(UriMasks.SCHEME, scheme, 1, false, index -> {
            throw new UriSyntaxException("invalid char in scheme at index " + (index + 1) + ": " + scheme);
        });
    }

    public void validateUserInfo(@Nullable String userInfo) throws UriSyntaxException {
        if (userInfo == null) return;

        check(UriMasks.USER_INFO, userInfo, true, index -> {
            throw new UriSyntaxException("invalid char in userinfo at index " + index + ": " + userInfo);
        });
    }

    public void validateHost(@Nullable String host) throws UriSyntaxException {
        if (host == null) return;

        if (host.length() > 2 && host.charAt(0) == '[' && host.charAt(host.length() - 1) == ']') { // IP-literal: IpvFuture or IPv6address
            if (host.charAt(1) == 'v') { // try IPvFuture
                int vlen = check(UriMasks.HEXDIG, host, 2, false); // version length
                int dotIndex = 2 + vlen;

                if (vlen > 0 && host.charAt(dotIndex) == '.') { // IPvFuture
                    int plen = vlen + 3; // preceding length
                    int elen = 1; // ending length
                    int slen = plen + elen; // surrounding length

                    int dlen = check(IPV_FUTURE_MASK, host, dotIndex + 1, false); // data length
                    if (dlen == 0 || dlen != host.length() - slen) {
                        throw new UriSyntaxException("invalid char in host at index " + (plen + dlen) + ": " + host);
                    }

                    return;
                }
            }

            // try IPv6address
            try {
                check(IPV6_ADDRESS_MASK, host, 1, host.length() - 2, false, index -> {
                    throw new UriSyntaxException("invalid char in host at index " + (index + 1) + ": " + host);
                });

                InetAddress address = InetAddress.getByName(host);
                if (!(address instanceof Inet6Address)) {
                    throw new UriSyntaxException("invalid IPv6 address: " + host);
                }
            } catch (UnknownHostException e) {
                throw new UriSyntaxException("invalid IPv6 address: " + host);
            }

        } else { // IPv4address or reg-name. Note: IPv4address is compliant with reg-name
            check(UriMasks.REG_NAME, host, true, index -> {
                throw new UriSyntaxException("invalid char in host at index " + index + ": " + host);
            });
        }
    }

    public void validatePort(@Nullable String port) throws UriSyntaxException {
        if (port == null) return;

        check(UriMasks.PORT, port, false, index -> {
            throw new UriSyntaxException("invalid char in port at index " + index + ": " + port);
        });
    }

    public void validatePath(String path) throws UriSyntaxException {
        check(UriMasks.PATH, path, true, index -> {
            throw new UriSyntaxException("invalid char in path at index " + index + ": " + path);
        });
    }

    public void validateSegment(String segment) throws UriSyntaxException {
        check(UriMasks.SEGMENT, segment, true, index -> {
            throw new UriSyntaxException("invalid char in segment at index " + index + ": " + segment);
        });
    }


    public void validateQuery(@Nullable String query) throws UriSyntaxException {
        if (query == null) return;

        check(UriMasks.QUERY, query, true, index -> {
            throw new UriSyntaxException("invalid char in query at index " + index + ": " + query);
        });
    }

    public void validateFragment(@Nullable String fragment) throws UriSyntaxException {
        if (fragment == null) return;

        check(UriMasks.FRAGMENT, fragment, true, index -> {
            throw new UriSyntaxException("invalid char in fragment at index " + index + ": " + fragment);
        });
    }

    private void validateCorrelations(InternalUri uri) {
        String scheme = uri.scheme();
        String userInfo = uri.userInfo(), host = uri.host(), port = uri.port();
        String path = uri.path();

        boolean hasAuthority = userInfo != null || host != null || port != null;

        // RRC3986#3.2: When authority is present, the host must not be null
        if (hasAuthority && host == null) {
            throw new UriSyntaxException("When authority is present, the host must not be null");
        }

        // RFC3986#3: When authority is not present, the path cannot begin with two slash characters ("//")
        if (!hasAuthority && path.startsWith("//")) {
            throw new UriSyntaxException("When authority is not present, the path cannot begin with two slash characters (\"//\")");
        }

        // RFC3986#3: When authority is present, the path must either be empty or begin with a slash ("/") character
        if (hasAuthority && !(path.isEmpty() || path.startsWith("/"))) {
            throw new UriSyntaxException("When authority is present, the path must either be empty or begin with a slash (\"/\") character: " + path);
        }

        // RFC3986#4.2: A path segment that contains a colon character cannot be used as the first segment of a relative-path reference
        int colonIndex = path.indexOf(':');
        if (scheme == null && !hasAuthority && colonIndex != -1 && path.substring(0, colonIndex).indexOf('/') == -1) {
            throw new UriSyntaxException("A path segment that contains a colon character cannot be used as the first segment of a relative-path reference");
        }
    }

    /**
     * @param allowPE allow percent-encoding
     * @return -1 if index out of bounds, or the count of leading valid characters.
     */
    private int check(AsciiMask mask, String str, int off, int len, boolean allowPE) {
        try {
            Preconditions.checkPositiveRange(str.length(), off, off + len);
        } catch (IndexOutOfBoundsException e) {
            return -1;
        }

        for (int i = 0; i < len; i++) {
            char c = str.charAt(off + i);
            if (!mask.match(c)) {
                if (c == '%' && allowPE && i + 2 < len) {
                    char h = str.charAt(i + 1), l = str.charAt(i + 2);
                    if (Hex.isHex(h) && Hex.isHex(l)) i += 2;
                    else return i;
                } else return i;
            }
        }
        return len;
    }

    private void check(AsciiMask mask, String str, int off, int len, boolean allowPE, IntConsumer handler) {
        int c = check(mask, str, off, len, allowPE);
        if (c != len) {
            handler.accept(c);
        }
    }

    private int check(AsciiMask mask, String str, int off, boolean allowPE) {
        return check(mask, str, off, str.length() - off, allowPE);
    }

    private void check(AsciiMask mask, String str, int off, boolean allowPE, IntConsumer handler) {
        check(mask, str, off, str.length() - off, allowPE, handler);
    }

    private int check(AsciiMask mask, String str, boolean allowPE) {
        return check(mask, str, 0, allowPE);
    }

    private void check(AsciiMask mask, String str, boolean allowPE, IntConsumer handler) {
        check(mask, str, 0, allowPE, handler);
    }

}
