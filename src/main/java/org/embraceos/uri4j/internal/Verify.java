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

import java.util.function.Supplier;

/**
 * @author Carrick Hong (洪灿昆)
 */
public abstract class Verify {

    private Verify() {
    }

    public static void verify(boolean expression) throws VerifyException {
        if (!expression) {
            throw new VerifyException();
        }
    }

    public static void verify(boolean expression, String message) throws VerifyException {
        if (!expression) {
            throw new VerifyException(message);
        }
    }

    public static void verify(boolean expression, Supplier<String> supplier) throws VerifyException {
        if (!expression) {
            throw new VerifyException(supplier.get());
        }
    }

}
