/*
Copyright 2025 Adobe
All Rights Reserved.

NOTICE: Adobe permits you to use, modify, and distribute this file in
accordance with the terms of the Adobe license agreement accompanying
it.
*/
package com.adobe.mkto.assetcopy.util;

import java.sql.Timestamp;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public final class AssetCopyUtils {

    private AssetCopyUtils() {}

    public static Timestamp parseTimeString(String timeString){
        timeString = timeString.replace("Z+0000", "+00:00");
        ZonedDateTime zonedDateTime = ZonedDateTime.parse(timeString,
                DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX"));

        return Timestamp.from(zonedDateTime.toInstant());
    }
}
