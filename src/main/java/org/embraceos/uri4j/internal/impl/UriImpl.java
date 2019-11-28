package org.embraceos.uri4j.internal.impl;

import org.embraceos.uri4j.*;
import org.embraceos.uri4j.internal.InternalUri;
import org.embraceos.uri4j.internal.UriUtils;
import org.embraceos.uri4j.internal.UriValidator;

import java.math.BigInteger;
import java.net.URI;
import java.util.Comparator;
import java.util.Objects;
import java.util.function.Function;

/**
 * @author Carrick Hong (洪灿昆)
 */
public class UriImpl extends UriRefImpl implements Uri {

    private static final Comparator<Uri> COMPARATOR;

    static {
        Function<Uri, Integer> getPortAsInt = uri -> {
            try {
                return uri.portAsInt();
            } catch (ArithmeticException e) {
                return null;
            }
        };
        COMPARATOR = Comparator.comparing(Uri::scheme, String::compareToIgnoreCase)
            .thenComparing(Uri::host, Comparator.nullsFirst(String::compareToIgnoreCase))
            .thenComparing((a, b) -> {
                String pa = a.port(), pb = b.port();
                if (pa == null) return (pb == null) ? 0 : -1;
                else if (pb == null) return 1;
                else {
                    Integer pia = getPortAsInt.apply(a), pib = getPortAsInt.apply(b);
                    if (pia != null && pib != null) return Integer.compare(pia, pib);
                    else if (pia != null) return -1;
                    else if (pib != null) return 1;
                    else return new BigInteger(pa).compareTo(new BigInteger(pb));
                }
            })
            .thenComparing(Uri::userInfo, Comparator.nullsFirst(String::compareTo))
            .thenComparing(Uri::path)
            .thenComparing(Uri::query, Comparator.nullsFirst(String::compareTo))
            .thenComparing(Uri::fragment, Comparator.nullsFirst(String::compareTo));
    }

    private final int hashCode;

    private volatile boolean normalized;

    /**
     * @param uri must be validated by {@link UriValidator} and {@link InternalUri#scheme() scheme} must not be null
     */
    UriImpl(InternalUri uri) {
        super(uri);
        this.hashCode = UriImpl.calcHashCode(this);
    }

    static int calcHashCode(Uri uri) {
        return Objects.hash(UriRefImpl.calcHashCode(uri), uri.scheme().toLowerCase());
    }

    static boolean equals(Uri a, Uri b) {
        return equalsIgnoreCase(a.scheme(), b.scheme()) && UriRefImpl.equals(a, b);
    }

    @Override
    public String scheme() {
        return uri.scheme();
    }

    @Override
    public Uri resolve(UriRef uriRef, boolean strict) throws UriException {
        if (uriRef instanceof Uri && (strict || !this.scheme().equalsIgnoreCase(((Uri) uriRef).scheme()))) {
            return ((Uri) uriRef).normalize();
        }

        UriBuilder builder = this.mutate();

        if (uriRef.authority() != null) {
            builder.authority(uriRef.authority())
                .path(uriRef.path())
                .query(uriRef.query());
        } else {
            if (uriRef.path().isEmpty()) {
                if (uriRef.query() != null) {
                    builder.query(uriRef.query());
                }
            } else {
                if (uriRef.path().isAbsolute()) {
                    builder.path(uriRef.path());
                } else {
                    if (authority() != null && path().isEmpty()) {
                        // for better compatibility
                        builder.path(uriRef.path().mutate().absolute(true).build());
                    } else {
                        builder.path(this.path().resolve(uriRef.path()));
                    }
                }
                builder.query(uriRef.query());
            }
        }

        builder.fragment(uriRef.fragment());
        return ((Uri) builder.build()).normalize();
    }

    @Override
    public Uri normalize() throws UriException {
        if (normalized) return this;

        UriImpl normalizedUri = normalize0();
        normalizedUri.normalized = true;
        return normalizedUri;
    }

    private UriImpl normalize0() {
        UriBuilderImpl builder = UriBuilderImpl.from(uri);

        builder.scheme(scheme().toLowerCase());

        String useInfo = userInfo();
        if (useInfo != null) {
            builder.userInfo(UriUtils.normalize(useInfo));
        }

        String host = host();
        if (host != null) {
            builder.host(UriUtils.normalize(host.toLowerCase()));
        }

        String port = port();
        if (port != null && port.isEmpty()) {
            builder.port(null);
        }

        Path path = path().normalize();
        if ((path.value().startsWith("/.//") && authority() != null) || path.value().startsWith("./")) {
            path = path.mutate().strip(1).build();
        }
        builder.path(path);

        String query = query();
        if (query != null) {
            builder.query(UriUtils.normalize(query));
        }

        String fragment = fragment();
        if (fragment != null) {
            builder.fragment(UriUtils.normalize(fragment));
        }

        return builder.build();
    }

    @Override
    public int compareTo(Uri that) {
        return COMPARATOR.compare(this, that);
    }

    @Override
    public boolean equals(Object that) {
        if (this == that) return true;
        else if (!(that instanceof Uri)) return false;

        return UriImpl.equals(this, (Uri) that);
    }

    @Override
    public int hashCode() {
        return hashCode;
    }

    @Override
    public String toString() {
        return super.toString();
    }

    @Override
    public UriBuilder mutate() {
        return super.mutate();
    }

    @Override
    public URI toJdk() throws UriException {
        return super.toJdk();
    }

}
