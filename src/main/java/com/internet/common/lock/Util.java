package com.internet.common.lock;

import org.apache.commons.lang3.time.FastDateFormat;

import java.sql.Date;
import java.time.Instant;
import java.util.UUID;

/**
 * @author Kael He
 */
public class Util {
    public static final FastDateFormat FORMAT = FastDateFormat.getInstance("yyyy-MM-dd HH:mm:ss");

    public static String generateUuid() {
        return UUID.randomUUID().toString();
    }

    public static String format(Instant instant) {
        return FORMAT.format(Date.from(instant));
    }
}
