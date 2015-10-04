package org.lanternpowered.server.util;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkArgument;

import java.util.UUID;

public final class UUIDHelper {

    private UUIDHelper() {
    }

    /**
     * Parses the uuid instance from a flat string (without dashes).
     * 
     * @param string the flat string
     * @return the uuid
     */
    public static UUID fromFlatString(String string) {
        checkNotNull(string, "string");
        checkArgument(string.length() == 32, "length must be 32");
        return UUID.fromString(string.substring(0, 8) + "-" + string.substring(8, 12) + "-" + string.substring(12, 16) +
                "-" + string.substring(16, 20) + "-" + string.substring(20, 32));
    }

    /**
     * Converts the uuid to a flat string (without dashes).
     * 
     * @param uuid the uuid
     * @return the flat string
     */
    public static String toFlatString(UUID uuid) {
        return checkNotNull(uuid, "uuid").toString().replace("-", "");
    }
}