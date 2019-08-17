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

package org.embraceos.uri4j;

import org.apiguardian.api.API;
import org.embraceos.uri4j.internal.lang.Nullable;

/**
 * Thrown to indicate an exception has occurred with the syntax of URI-reference.
 *
 * @author Carrick Hong (洪灿昆)
 */
@API(status = API.Status.STABLE)
public class UriSyntaxException extends UriException {

    /**
     * Constructs an {@code UriSyntaxException} with {@code null} as its error detail message.
     */
    public UriSyntaxException() {
        super();
    }

    /**
     * Constructs an {@code UriSyntaxException} with the specified detail message.
     *
     * @param message The detail message (which is saved for later retrieval
     *                by the {@link #getMessage()} method)
     */
    public UriSyntaxException(@Nullable String message) {
        super(message);
    }

    /**
     * Constructs an {@code UriSyntaxException} with the specified detail message and cause.
     *
     * <p> Note that the detail message associated with {@code cause} is
     * <i>not</i> automatically incorporated into this exception's detail
     * message.
     *
     * @param message The detail message (which is saved for later retrieval
     *                by the {@link #getMessage()} method)
     * @param cause   The cause (which is saved for later retrieval by the
     *                {@link #getCause()} method).  (A null value is permitted,
     *                and indicates that the cause is nonexistent or unknown.)
     */
    public UriSyntaxException(@Nullable String message, @Nullable Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs an {@code UriSyntaxException} with the specified cause and a
     * detail message of {@code (cause==null ? null : cause.toString())}
     * (which typically contains the class and detail message of {@code cause}).
     *
     * @param cause The cause (which is saved for later retrieval by the
     *              {@link #getCause()} method).  (A null value is permitted,
     *              and indicates that the cause is nonexistent or unknown.)
     */
    public UriSyntaxException(@Nullable Throwable cause) {
        super(cause);
    }

}
