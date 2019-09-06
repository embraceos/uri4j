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

/**
 * @author Carrick Hong (洪灿昆)
 */
public abstract class UriMasks {

    public static final AsciiMask LOWALPHA = AsciiMask.allow("abcdefghijklmnopqrstuvwxyz");
    public static final AsciiMask UPALPHA = AsciiMask.allow("ABCDEFGHIJKLMNOPQRSTUVWXYZ");
    public static final AsciiMask ALPHA = AsciiMask.combine(LOWALPHA, UPALPHA);
    public static final AsciiMask DIGIT = AsciiMask.allow("0123456789");
    public static final AsciiMask ALPHANUM = AsciiMask.combine(ALPHA, DIGIT);
    public static final AsciiMask HEXDIG = AsciiMask.combine(DIGIT, AsciiMask.allow("abcdefABCDEF"));

    public static final AsciiMask UNRESERVED = AsciiMask.combine(ALPHA, DIGIT, AsciiMask.allow("-._~"));
    public static final AsciiMask GEN_DELIMS = AsciiMask.allow(":/?#[]@");
    public static final AsciiMask SUB_DELIMS = AsciiMask.allow("!$&'()*+,;=");
    public static final AsciiMask RESERVED = AsciiMask.combine(GEN_DELIMS, SUB_DELIMS);
    public static final AsciiMask URIC = AsciiMask.combine(UNRESERVED, RESERVED);

    public static final AsciiMask PCHAR = AsciiMask.combine(UNRESERVED, SUB_DELIMS, AsciiMask.allow(":@"));
    public static final AsciiMask SEGMENT = PCHAR;
    public static final AsciiMask SEGMENT_NC = AsciiMask.combine(UNRESERVED, SUB_DELIMS, AsciiMask.allow("@"));

    public static final AsciiMask SCHEME_FC = ALPHA; // first char of scheme component
    public static final AsciiMask SCHEME = AsciiMask.combine(ALPHA, DIGIT, AsciiMask.allow("+-."));
    public static final AsciiMask USER_INFO = AsciiMask.combine(UNRESERVED, SUB_DELIMS, AsciiMask.allow(":"));
    public static final AsciiMask REG_NAME = AsciiMask.combine(UNRESERVED, SUB_DELIMS);
    public static final AsciiMask PORT = DIGIT;
    public static final AsciiMask PATH = AsciiMask.combine(PCHAR, AsciiMask.allow("/"));
    public static final AsciiMask QUERY = AsciiMask.combine(PCHAR, AsciiMask.allow("/?"));
    public static final AsciiMask FRAGMENT = AsciiMask.combine(PCHAR, AsciiMask.allow("/?"));

    private UriMasks() {
    }

}
