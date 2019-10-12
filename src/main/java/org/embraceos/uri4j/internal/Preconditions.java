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

import java.util.function.Supplier;

/**
 * @author Carrick Hong (洪灿昆)
 */
public abstract class Preconditions {

    public static <T> T checkNotNull(@Nullable T reference) throws NullPointerException {
        if (reference == null) {
            throw new NullPointerException();
        }
        return reference;
    }

    public static <T> T checkNotNull(@Nullable T reference, String message) throws NullPointerException {
        if (reference == null) {
            throw new NullPointerException(message);
        }
        return reference;
    }

    public static <T> T checkNotNull(@Nullable T reference, Supplier<String> supplier) throws NullPointerException {
        if (reference == null) {
            throw new NullPointerException(supplier.get());
        }
        return reference;
    }

    public static void checkArgument(boolean expression) throws IllegalArgumentException {
        if (!expression) {
            throw new IllegalArgumentException();
        }
    }

    public static void checkArgument(boolean expression, String message) throws IllegalArgumentException {
        if (!expression) {
            throw new IllegalArgumentException(message);
        }
    }

    public static void checkArgument(boolean expression, Supplier<String> supplier) throws IllegalArgumentException {
        if (!expression) {
            throw new IllegalArgumentException(supplier.get());
        }
    }

    public static void checkState(boolean expression) throws IllegalStateException {
        if (!expression) {
            throw new IllegalStateException();
        }
    }

    public static void checkState(boolean expression, String message) throws IllegalStateException {
        if (!expression) {
            throw new IllegalStateException(message);
        }
    }

    public static void checkState(boolean expression, Supplier<String> supplier) throws IllegalStateException {
        if (!expression) {
            throw new IllegalStateException(supplier.get());
        }
    }

    public static int checkIndex(int index, int size) throws IndexOutOfBoundsException, IllegalArgumentException {
        return checkIndex(index, size, "index");
    }

    public static int checkIndex(int index, int size, String desc) throws IndexOutOfBoundsException, IllegalArgumentException {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException(badIndex(index, size, desc));
        }
        return index;
    }

    /**
     * @param start inclusive
     * @param end   exclusive
     * @see #checkPositiveRange(int, int, int)
     */
    public static void checkRange(int size, int start, int end) throws IndexOutOfBoundsException, IllegalArgumentException {
        if (start == end && start == size) return;
        checkIndex(start, size, "start index");
        checkIndex(end, size + 1, "end index");
    }

    /**
     * @param start inclusive
     * @param end   exclusive
     * @see #checkRange(int, int, int)
     */
    public static void checkPositiveRange(int size, int start, int end) throws IndexOutOfBoundsException, IllegalArgumentException {
        checkArgument(start <= end, () -> "end index (" + end + ") must not be less than start index (" + start + ")");
        checkRange(size, start, end);
    }

    private static String badIndex(int index, int size, String desc) throws IllegalArgumentException {
        if (index < 0) {
            return desc + " (" + index + ") must not be negative";
        } else if (size < 0) {
            throw new IllegalArgumentException("negative size: " + size);
        } else {
            return desc + " (" + index + ") must be less than size (" + size + ")";
        }
    }

}
