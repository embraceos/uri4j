package org.embraceos.uri4j.internal.impl;

import org.embraceos.uri4j.*;
import org.embraceos.uri4j.internal.InternalUri;
import org.embraceos.uri4j.internal.UriValidator;
import org.embraceos.uri4j.internal.lang.Nullable;

import java.net.URI;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Carrick Hong (洪灿昆)
 */
public class UriBuilderImpl implements UriBuilder {

    private static final Pattern AUTHORITY_PATTERN = Pattern.compile("(?:([^@/?#]*)@)?(\\[[^]/?#]*\\]|[^:/?#]*)(?::([^/?#]*))?");

    private final InternalUri.Builder uriBuilder = InternalUri.create();
    private PathBuilder pathBuilder = PathBuilder.create().absolute(false);

    public static UriBuilderImpl from(String uriRef) throws UriSyntaxException {
        InternalUri uri = InternalUri.parse(uriRef);
        UriValidator.INSTANCE.validate(uri);
        return from(uri);
    }

    public static UriBuilderImpl from(URI jdkUri) throws UriSyntaxException {
        return from(jdkUri.toASCIIString());
    }

    /**
     * @param uri must be validated by {@link UriValidator}
     */
    static UriBuilderImpl from(InternalUri uri) {
        UriBuilderImpl builder = new UriBuilderImpl();
        builder.scheme(uri.scheme());
        builder.userInfo(uri.userInfo());
        builder.host(uri.host());
        builder.port(uri.port());
        builder.path(new PathImpl(uri.path()));
        builder.query(uri.query());
        builder.fragment(uri.fragment());
        return builder;
    }

    @Override
    public UriBuilder scheme(@Nullable String scheme) throws UriSyntaxException {
        uriBuilder.scheme(scheme);
        return this;
    }

    @Override
    public UriBuilder authority(@Nullable String authority) throws UriSyntaxException {
        if (authority == null) {
            uriBuilder.userInfo(null).host(null).port(null);
        } else {
            Matcher matcher = AUTHORITY_PATTERN.matcher(authority);
            if (!matcher.matches()) {
                throw new UriSyntaxException("invalid authority: " + authority);
            }
            uriBuilder.userInfo(matcher.group(1))
                .host(matcher.group(2))
                .port(matcher.group(3));
        }
        return this;
    }

    @Override
    public UriBuilder userInfo(@Nullable String userInfo) throws UriSyntaxException {
        uriBuilder.userInfo(userInfo);
        return this;
    }

    @Override
    public UriBuilder host(@Nullable String host) throws UriSyntaxException {
        uriBuilder.host(host);
        return this;
    }

    @Override
    public UriBuilder port(@Nullable String port) throws UriSyntaxException {
        uriBuilder.port(port);
        return this;
    }

    @Override
    public UriBuilder path(Path path) {
        pathBuilder = path.mutate();
        return this;
    }

    @Override
    public UriBuilder path(Consumer<PathBuilder> action) {
        action.accept(pathBuilder);
        return this;
    }

    @Override
    public UriBuilder query(@Nullable String query) throws UriSyntaxException {
        uriBuilder.query(query);
        return this;
    }

    @Override
    public UriBuilder fragment(@Nullable String fragment) throws UriSyntaxException {
        uriBuilder.fragment(fragment);
        return this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends UriRef> T build() throws UriSyntaxException, ClassCastException {
        InternalUri internalUri = uriBuilder.path(pathBuilder.build().toString()).build();
        UriValidator.INSTANCE.validate(internalUri);
        UriRef uriRef = internalUri.scheme() == null ? new UriRefImpl(internalUri) : new UriImpl(internalUri);
        return (T) uriRef;
    }

}
