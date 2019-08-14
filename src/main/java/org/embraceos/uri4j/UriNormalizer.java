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

/**
 * This interface is used to normalize URIs.
 *
 * <p> One of the most common operations on URIs is simple comparison:
 * determining whether two URIs are equivalent without using the URIs to
 * access their respective resource(s).
 *
 * <p> Proper normalization can reduce the probability of false negatives when compares
 * two URIs. <a href="https://tools.ietf.org/html/rfc3986#section-6">RFC 3986</a> defines
 * three levels of normalization, with the amount of processing required increases but
 * the probability of false negatives decreases:
 *
 * <ol>
 *   <li><a href="https://tools.ietf.org/html/rfc3986#section-6.2.2">Syntax-Based Normalization</a></li>
 *   <li><a href="https://tools.ietf.org/html/rfc3986#section-6.2.3">Scheme-Based Normalization</a></li>
 *   <li><a href="https://tools.ietf.org/html/rfc3986#section-6.2.4">Protocol-Based Normalization</a></li>
 * </ol>
 *
 * @author Carrick Hong (洪灿昆)
 * @see Uri#normalize()
 */
@API(status = API.Status.STABLE)
public interface UriNormalizer {

    /**
     * <a href="https://tools.ietf.org/html/rfc3986#section-6">Normalizes</a> the given URI,
     * and returns the normalized URI or just the given URI if it is already in normalized form.
     *
     * @param uri The URI to be normalized
     * @return The normalized URI or just the given URI if it is already in normalized form
     * @throws UriException if there is problem normalizing the URI
     */
    Uri normalize(Uri uri) throws UriException;

}
