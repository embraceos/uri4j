package org.embraceos.uri4j.internal.impl;

import org.embraceos.uri4j.*;
import org.embraceos.uri4j.internal.InternalUri;
import org.embraceos.uri4j.internal.UriValidator;
import org.embraceos.uri4j.internal.lang.Nullable;

import java.lang.ref.WeakReference;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Objects;

/**
 * @author Carrick Hong (洪灿昆)
 */
public class UriRefImpl implements UriRef {

    final InternalUri uri;
    @Nullable private final String authority;
    private final Object portAsInt;
    private final Path path;

    private final int hashCode;

    @Nullable volatile WeakReference<URI> jdkUriRef;

    /**
     * @param uri must be validated by {@link UriValidator}
     */
    UriRefImpl(InternalUri uri) {
        this.uri = uri;
        this.authority = UriRef.super.authority();
        this.portAsInt = parsePortAsInt();
        this.path = new PathImpl(uri.path());
        this.hashCode = calcHashCode(this);
    }

    static int calcHashCode(UriRef uriRef) {
        String host = uriRef.host();
        return Objects.hash(
            host == null ? null : host.toLowerCase(),
            uriRef.port(),
            uriRef.userInfo(),
            uriRef.path(),
            uriRef.query(),
            uriRef.fragment()
        );
    }

    static boolean equals(UriRef a, UriRef b) {
        return equalsIgnoreCase(a.host(), b.host())
            && Objects.equals(a.port(), b.port())
            && Objects.equals(a.userInfo(), b.userInfo())
            && Objects.equals(a.path(), b.path())
            && Objects.equals(a.query(), b.query())
            && Objects.equals(a.fragment(), b.fragment());
    }

    static boolean equalsIgnoreCase(@Nullable String a, @Nullable String b) {
        return (a == b) || (a != null && a.equalsIgnoreCase(b));
    }

    private Object parsePortAsInt() {
        Object portAsInt;
        try {
            portAsInt = UriRef.super.portAsInt();
        } catch (ArithmeticException e) {
            portAsInt = e;
        }
        return portAsInt;
    }

    @Override
    @Nullable
    public String authority() {
        return authority;
    }

    @Override
    @Nullable
    public String userInfo() {
        return uri.userInfo();
    }

    @Override
    @Nullable
    public String host() {
        return uri.host();
    }

    @Override
    @Nullable
    public String port() {
        return uri.port();
    }

    @Override
    public int portAsInt() throws ArithmeticException {
        if (portAsInt instanceof ArithmeticException) {
            throw (ArithmeticException) portAsInt;
        }
        return (int) portAsInt;
    }

    @Override
    public Path path() {
        return path;
    }

    @Override
    @Nullable
    public String query() {
        return uri.query();
    }

    @Override
    @Nullable
    public String fragment() {
        return uri.fragment();
    }

    @Override
    public boolean equals(Object that) {
        if (this == that) return true;
        else if (!(that instanceof UriRef)) return false;
        else if (that instanceof Uri) return false;

        return equals(this, (UriRef) that);
    }

    @Override
    public int hashCode() {
        return hashCode;
    }

    @Override
    public String toString() {
        return uri.toString();
    }

    @Override
    public UriBuilder mutate() {
        return UriBuilderImpl.from(uri);
    }

    @Override
    public URI toJdk() throws UriException {
        WeakReference<URI> jdkUriRef = this.jdkUriRef;
        URI jdkUri = jdkUriRef == null ? null : jdkUriRef.get();
        if (jdkUri != null) return jdkUri;

        jdkUri = toJdk0();
        jdkUriRef = new WeakReference<>(jdkUri);
        this.jdkUriRef = jdkUriRef;
        return jdkUri;
    }

    private URI toJdk0() throws UriException {
        try {
            return new URI(toString());
        } catch (URISyntaxException e) {
            throw new UriException(e);
        }
    }

}
