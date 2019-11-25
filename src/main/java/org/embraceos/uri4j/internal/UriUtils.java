package org.embraceos.uri4j.internal;

/**
 * @author Carrick Hong (洪灿昆)
 */
public abstract class UriUtils {

    public static String normalize(String value) {
        StringBuilder sb = new StringBuilder();
        boolean mutated = false;

        int len = value.length();
        for (int j = 0; j < len; j++) {
            char c = value.charAt(j);
            if (c == '%') {
                char h = value.charAt(++j), l = value.charAt(++j);
                char pec = (char) ((int) Hex.toByte(h, l) & 0xFF);

                boolean unreserved = UriMasks.UNRESERVED.match(pec);
                boolean lowercase = Character.isLowerCase(h) || Character.isLowerCase(l);
                if (!mutated && (unreserved || lowercase)) {
                    sb.append(value, 0, j - 2);
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

        return mutated ? sb.toString() : value;
    }

}
