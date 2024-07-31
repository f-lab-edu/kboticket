package com.kboticket.common.constants;

import lombok.experimental.UtilityClass;

@UtilityClass
public class KboConstant {
    public static final String SEAT_LOCK = "seatLock:";

    public static final String BASIC_DLIIMITER = ":";
    public static final String AUTH_HEADER_PREFIX = "Basic ";

    public static final long CONNECT_TIMEOUT = 1 * 1000;
    public static final long READ_TIMEOUT = 60 * 1000;
    public static final long WAIT_TIME = 3 * 1000;
    public static final long EXPIRED_TIME = 8 * 60 * 1000;
}
