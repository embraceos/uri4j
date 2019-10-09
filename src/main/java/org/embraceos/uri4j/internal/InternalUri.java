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

import org.embraceos.uri4j.internal.lang.Nullable;

import javax.annotation.concurrent.Immutable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Carrick Hong (洪灿昆)
 */
@Immutable
public class InternalUri {

    private static final Pattern PATTERN = Pattern.compile("(?:([^:/?#]+):)?(?://(?:([^@/?#]*)@)?(\\[[^]/?#]*\\]|[^:/?#]*)(?::([^/?#]*))?)?([^?#]*)(?:\\?([^#]*))?(?:#(.*))?");

    @Nullable private String scheme;
    @Nullable private String userInfo;
    @Nullable private String host;
    @Nullable private String port;
    private String path;
    @Nullable private String query;
    @Nullable private String fragment;

    public InternalUri(@Nullable String scheme, @Nullable String userInfo,
                       @Nullable String host, @Nullable String port, String path,
                       @Nullable String query, @Nullable String fragment) {
        this.scheme = scheme;
        this.userInfo = userInfo;
        this.host = host;
        this.port = port;
        this.path = path;
        this.query = query;
        this.fragment = fragment;
    }

    public static InternalUri parse(String uri) {
        Matcher matcher = PATTERN.matcher(uri);
        Verify.verify(matcher.matches());
        Verify.verify(matcher.groupCount() == 7);

        String scheme = matcher.group(1);

        String userInfo = matcher.group(2);
        String host = matcher.group(3);
        String port = matcher.group(4);

        String path = matcher.group(5);
        Verify.verify(path != null);

        String query = matcher.group(6);
        String fragment = matcher.group(7);

        return new InternalUri(scheme, userInfo, host, port, path, query, fragment);
    }

    public static Builder create() {
        return new Builder();
    }

    @Nullable
    public String scheme() {
        return scheme;
    }

    @Nullable
    public String userInfo() {
        return userInfo;
    }

    @Nullable
    public String host() {
        return host;
    }

    @Nullable
    public String port() {
        return port;
    }

    public String path() {
        return path;
    }

    @Nullable
    public String query() {
        return query;
    }

    @Nullable
    public String fragment() {
        return fragment;
    }

    public static class Builder {

        @Nullable private String scheme;
        @Nullable private String userInfo;
        @Nullable private String host;
        @Nullable private String port;
        private String path = "";
        @Nullable private String query;
        @Nullable private String fragment;

        public Builder scheme(@Nullable String scheme) {
            this.scheme = scheme;
            return this;
        }

        public Builder userInfo(@Nullable String userInfo) {
            this.userInfo = userInfo;
            return this;
        }

        public Builder host(@Nullable String host) {
            this.host = host;
            return this;
        }

        public Builder port(@Nullable String port) {
            this.port = port;
            return this;
        }

        public Builder path(String path) {
            this.path = path;
            return this;
        }

        public Builder query(@Nullable String query) {
            this.query = query;
            return this;
        }

        public Builder fragment(@Nullable String fragment) {
            this.fragment = fragment;
            return this;
        }

        public InternalUri build() {
            return new InternalUri(scheme, userInfo, host, port, path, query, fragment);
        }

    }

}
