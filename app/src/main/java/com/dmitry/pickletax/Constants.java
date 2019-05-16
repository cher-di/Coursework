package com.dmitry.pickletax;

import okhttp3.MediaType;

public interface Constants {
    public static final Integer AUTH_REQUEST = 0;
    public static final Integer AUTH_RESULT_ACK = 1;
    public static final Integer AUTH_RESULT_FAIL = 2;
    public static final Integer REAUTH_REQUEST = 3;
    public static final Integer REAUTH_REQUEST_ACK = 4;
    public static final Integer REAUTH_REQUEST_FAIL = 5;

    public static final Integer AUTH_VALIDATION_ACK = 250;
    public static final Integer AUTH_VALIDATION_FAIL = 450;
    public static final Integer AUTH_CODE_ACK = 251;
    public static final Integer AUTH_CODE_FAIL = 451;

    public static final String EMAIL_IDENTIFIER = "com.dmitry.pickletax.email_edintifier";
    public static final String CITY_IDENTIFIER = "com.dmitry.pickletax.city_identifier";

    public static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
}
