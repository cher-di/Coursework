package com.dmitry.pickletax;

import okhttp3.MediaType;

public interface Constants {
    public static final int AUTH_REQUEST = 0;
    public static final int AUTH_RESULT_ACK = 1;
    public static final int AUTH_RESULT_FAIL = 2;
    public static final int REAUTH_REQUEST = 3;
    public static final int REAUTH_REQUEST_ACK = 4;
    public static final int REAUTH_REQUEST_FAIL = 5;

    public static final int AUTH_VALIDATION_ACK = 250;
    public static final int AUTH_VALIDATION_FAIL = 450;
    public static final int AUTH_CODE_ACK = 251;
    public static final int AUTH_CODE_FAIL = 451;

    public static int CONNECT_TIMEOUT = 5;

    // эти константы нельзя менять, т.к. они жестко зашиты в базе данных
    public static final int CLASSROOM_FREE = 0;
    public static final int CLASSROOM_BUSY = 1;

    public static final String EMAIL_IDENTIFIER = "com.dmitry.pickletax.email_edintifier";
    public static final String CITY_IDENTIFIER = "com.dmitry.pickletax.city_identifier";

    public static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
}
