package com.line4thon.fin4u.global.constant;

/* Http 응답 코드 */
public class StaticValue {

    /* 2xx response */
    public static final int OK = 200;
    public static final int CREATED = 201;

    /* 4xx response */
    public static final int BAD_REQUEST = 400;
    public static final int UNAUTHORIZED = 401;
    public static final int FORBIDDEN = 403;
    public static final int NOT_FOUND = 404;
    public static final int METHOD_NOT_ALLOWED = 405;
    public static final int CONFLICT = 409;

    /* 5xx response */
    public static final int INTERNAL_SERVER_ERROR = 500;
}
