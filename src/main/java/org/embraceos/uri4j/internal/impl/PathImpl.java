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

package org.embraceos.uri4j.internal.impl;

import jdk.nashorn.internal.ir.annotations.Immutable;
import org.embraceos.uri4j.Path;
import org.embraceos.uri4j.UriException;
import org.embraceos.uri4j.UriSyntaxException;
import org.embraceos.uri4j.internal.Hex;
import org.embraceos.uri4j.internal.UriMasks;
import org.embraceos.uri4j.internal.UriValidator;

import java.util.*;

/**
 * @author Carrick Hong (洪灿昆)
 */
@Immutable
public class PathImpl implements Path {

    private final String value;
    private final List<String> segments;

    private volatile boolean normalized;

    private PathImpl(String value) {
        this.value = value;
        this.segments = parseSegments(value);
    }

    PathImpl(boolean absolute, List<String> segments) {
        this.value = joinSegments(absolute, segments);
        this.segments = Collections.unmodifiableList(segments);
    }

    public static PathImpl parse(String value) throws UriSyntaxException {
        UriValidator.INSTANCE.validatePath(value);
        return new PathImpl(value);
    }

    private static List<String> parseSegments(String value) {
        boolean absolute = value.startsWith("/");
        List<String> segments = Arrays.asList(value.split("/", -1));
        return Collections.unmodifiableList(absolute ? segments.subList(1, segments.size()) : segments);
    }

    private static String joinSegments(boolean absolute, List<String> segments) {
        StringBuilder sb = new StringBuilder();
        if (absolute) sb.append("/");
        else if (segments.size() >= 2 && segments.get(0).isEmpty()) {
            segments.add(0, SINGLE_DOT_SEGMENT);
        }

        int size = segments.size();
        for (int i = 0; i < size; ) {
            sb.append(segments.get(i++));
            if (i != size) {
                sb.append("/");
            }
        }
        return sb.toString();
    }

    @Override
    public String value() {
        return value;
    }

    @Override
    public List<String> segments() {
        return segments;
    }

    @Override
    public Path resolve(Path that) throws UriException {
        return Path.super.resolve(that);
    }

    @Override
    public Path normalize() throws UriException {
        if (normalized) return this;

        PathImpl normalizedPath = normalize0();
        normalizedPath.normalized = true;
        return normalizedPath;
    }

    private PathImpl normalize0() {
        List<String> segments = new LinkedList<>();
        for (String seg : segments()) {
            if (SINGLE_DOT_SEGMENT.equals(seg)) {
                // just drop it
                continue;
            } else if (DOUBLE_DOTS_SEGMENT.equals(seg)) {
                if (segments.isEmpty()) {
                    segments.add(seg);
                } else {
                    String last = segments.get(segments.size() - 1);
                    if (DOUBLE_DOTS_SEGMENT.equals(last)) {
                        segments.add(seg);
                    } else {
                        segments.remove(segments.size() - 1);
                    }
                }
            } else {
                segments.add(seg);
            }
        }

        if (isAbsolute()) {
            Iterator<String> iterator = segments.iterator();
            while (iterator.hasNext()) {
                String seg = iterator.next();
                if (DOUBLE_DOTS_SEGMENT.equals(seg)) {
                    iterator.remove();
                } else {
                    break;
                }
            }
        }

        if (segments.size() >= 2 && isAbsolute() && segments.get(0).isEmpty()) { // start with "//"
            segments.add(0, SINGLE_DOT_SEGMENT);
        } else if (!segments.isEmpty() && !isAbsolute() && segments.get(0).indexOf(':') != -1) { // first segment contains colon
            segments.add(0, SINGLE_DOT_SEGMENT);
        }

        normalizeSegments(segments);

        if (segments.equals(segments())) {
            return this;
        } else {
            return new PathImpl(isAbsolute(), segments);
        }
    }

    private void normalizeSegments(List<String> segments) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < segments.size(); i++, sb.setLength(0)) {
            String segment = segments.get(i);
            boolean mutated = false;

            int len = segment.length();
            for (int j = 0; j < len; j++) {
                char c = segment.charAt(j);
                if (c == '%') {
                    char h = segment.charAt(++j), l = segment.charAt(++j);
                    char pec = (char) ((int) Hex.toByte(h, l) & 0xFF);

                    boolean unreserved = UriMasks.UNRESERVED.match(pec);
                    boolean lowercase = Character.isLowerCase(h) || Character.isLowerCase(l);
                    if (!mutated && (unreserved || lowercase)) {
                        sb.append(segment, 0, j - 2);
                        mutated = true;
                    }

                    if (mutated) {
                        if (unreserved) {
                            sb.append(pec);
                        } else {
                            sb.append(c).append(Character.toUpperCase(h)).append(Character.toUpperCase(l));
                        }
                    }
                } else {
                    if (mutated) {
                        sb.append(c);
                    }
                }
            }

            if (mutated) {
                segments.set(i, sb.toString());
            }
        }
    }

    @Override
    public boolean equals(Object that) {
        if (this == that) return true;
        if (!(that instanceof Path)) return false;
        return Objects.equals(this.value(), ((Path) that).value());
    }

    @Override
    public int hashCode() {
        return value().hashCode();
    }

    @Override
    public String toString() {
        return value();
    }

}
