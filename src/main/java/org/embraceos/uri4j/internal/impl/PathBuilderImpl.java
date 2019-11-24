package org.embraceos.uri4j.internal.impl;

import org.embraceos.uri4j.Path;
import org.embraceos.uri4j.PathBuilder;
import org.embraceos.uri4j.UriSyntaxException;
import org.embraceos.uri4j.internal.Preconditions;
import org.embraceos.uri4j.internal.UriValidator;

import javax.annotation.concurrent.NotThreadSafe;
import java.util.*;

/**
 * @author Carrick Hong (洪灿昆)
 */
@NotThreadSafe
public class PathBuilderImpl implements PathBuilder {

    private boolean absolute = true;
    private List<String> segments = new LinkedList<>();

    public PathBuilderImpl() {
    }

    private PathBuilderImpl(boolean absolute, List<String> segments) {
        this.absolute = absolute;
        this.segments.addAll(segments);
    }

    public static PathBuilderImpl from(Path path) {
        return new PathBuilderImpl(path.isAbsolute(), path.segments());
    }

    @Override
    public PathBuilder segments(int index, String... segments) throws UriSyntaxException, IndexOutOfBoundsException {
        Preconditions.checkIndex(index, size() + 1);

        for (String segment : segments) {
            if (segment != null) {
                this.segments.add(index++, segment);
            }
        }
        return this;
    }

    @Override
    public PathBuilder setSegments(int index, String... segments) throws UriSyntaxException, IndexOutOfBoundsException {
        Preconditions.checkIndex(index, size());

        for (String segment : segments) {
            if (segment != null) {
                if (index < size()) {
                    this.segments.set(index++, segment);
                } else {
                    this.segments.add(index++, segment);
                }
            }
        }
        return this;
    }

    @Override
    public PathBuilder paths(String... paths) throws UriSyntaxException {
        for (String path : paths) {
            if (path == null || path.isEmpty()) continue;

            if (segments.isEmpty()) segments.add("");

            String[] toAppended = path.split("/", -1);
            if (!segments.isEmpty() && segments.get(segments.size() - 1).isEmpty()) {
                segments.remove(segments.size() - 1);
            }

            segments.addAll(Arrays.asList(toAppended).subList(toAppended[0].isEmpty() ? 1 : 0, toAppended.length));
        }
        return this;
    }

    @Override
    public PathBuilder strip(int size) {
        size = Math.min(Math.max(size, 0), size());
        segments.subList(0, size).clear();
        return this;
    }

    @Override
    public PathBuilder tear(int size) {
        size = Math.min(Math.max(size, 0), size());
        segments.subList(size() - size, size()).clear();
        return this;
    }

    @Override
    public PathBuilder truncate(int size) {
        size = Math.min(Math.max(size, 0), size());
        segments = new LinkedList<>(segments.subList(0, size));
        return this;
    }

    @Override
    public PathBuilder trim() {
        for (int i = segments.size() - 1; i >= 0; i--) {
            String segment = segments.get(i);
            if (segment.isEmpty()) {
                segments.remove(i);
            } else {
                break;
            }
        }
        return this;
    }

    @Override
    public PathBuilder prune() {
        ListIterator<String> iterator = segments.listIterator();
        while (iterator.hasNext()) {
            String segment = iterator.next();
            if (segment.isEmpty() && iterator.nextIndex() != size()) {
                iterator.remove();
            }
        }
        return this;
    }

    @Override
    public PathBuilder remove(int index) throws IndexOutOfBoundsException {
        Preconditions.checkIndex(index, size());
        segments.remove(index);
        return this;
    }

    @Override
    public PathBuilder clear() {
        segments.clear();
        return this;
    }

    @Override
    public PathBuilder absolute(boolean absolute) {
        this.absolute = absolute;
        return this;
    }

    @Override
    public Path build() throws UriSyntaxException {
        if (segments.isEmpty()) segments.add("");

        for (String segment : segments) {
            UriValidator.INSTANCE.validateSegment(segment);
        }

        return new PathImpl(absolute, segments);
    }

    @Override
    public List<String> segments() {
        return Collections.unmodifiableList(segments);
    }

    @Override
    public int size() {
        return segments.size();
    }

}
