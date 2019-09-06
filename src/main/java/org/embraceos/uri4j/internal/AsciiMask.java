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

import javax.annotation.concurrent.ThreadSafe;
import java.util.Arrays;

/**
 * @author Carrick Hong (洪灿昆)
 */
@ThreadSafe
public class AsciiMask {

    private final long lowMask;
    private final long highMask;

    private AsciiMask(long lowMask, long highMask) {
        this.lowMask = lowMask;
        this.highMask = highMask;
    }

    public static AsciiMask allow(String str) throws IllegalArgumentException {
        int length = str.length();
        long lowMask = 0, highMask = 0;
        for (int i = 0; i < length; i++) {
            char c = str.charAt(i);
            if ((c >>> 6) == 0) {
                lowMask |= 1L << c;
            } else if ((c >>> 7) == 0) {
                highMask |= 1L << (c & 0b111111);
            } else {
                throw new IllegalArgumentException("illegal character at index " + i + ": " + str);
            }
        }
        return new AsciiMask(lowMask, highMask);
    }

    public static AsciiMask combine(AsciiMask... masks) throws IllegalArgumentException {
        Preconditions.checkArgument(masks.length >= 2);
        long lowMask = Arrays.stream(masks).map(m -> m.lowMask).reduce((m1, m2) -> m1 | m2).get();
        long highMask = Arrays.stream(masks).map(m -> m.highMask).reduce((m1, m2) -> m1 | m2).get();
        return new AsciiMask(lowMask, highMask);
    }

    public boolean match(char c) {
        if ((c >>> 6) == 0) {
            return ((1L << c) & lowMask) != 0;
        } else if ((c >>> 7) == 0) {
            return ((1L << (c & 0b111111)) & highMask) != 0;
        } else {
            return false;
        }
    }

    /**
     * @return the count of leading matched characters
     */
    public int match(String str, int off, int len) throws IllegalArgumentException, IndexOutOfBoundsException {
        Preconditions.checkPositiveRange(str.length(), off, off + len);
        for (int i = 0; i < len; i++) {
            if (!match(str.charAt(off + i))) return i;
        }
        return len;
    }

    /**
     * @return the count of leading matched characters
     */
    public int match(String str) {
        return match(str, 0, str.length());
    }

    public boolean contains(AsciiMask that) {
        return (this.highMask | that.highMask) == this.highMask
            && (this.lowMask | that.lowMask) == this.lowMask;
    }

}
