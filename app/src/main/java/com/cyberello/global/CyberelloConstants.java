package com.cyberello.global;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class CyberelloConstants {

    public static final String DATE_TIME_FORMAT_STRING = "dd/MM/yyyy HH:mm:ss";
    public static final SimpleDateFormat DATE_TIME_FORMAT = new SimpleDateFormat(DATE_TIME_FORMAT_STRING, Locale.ENGLISH);

    public static final String SERVER_DATE_TIME_FORMAT_STRING = "yyyy-MM-dd'[T]'HH:mm:ss.SSSZ";
    public static final SimpleDateFormat SERVER_DATE_TIME_FORMAT = new SimpleDateFormat(SERVER_DATE_TIME_FORMAT_STRING, Locale.US);

    private static final String DATE_FORMAT = "dd/MM/yyyy";
    private static final String TIME_FORMAT = "HH:mm:ss";
    public static final String TIME_FORMAT_SHORT = "HH:mm";
    public static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat(DATE_FORMAT, Locale.US);
    public static final SimpleDateFormat SIMPLE_TIME_FORMAT = new SimpleDateFormat(TIME_FORMAT, Locale.US);

    public static final String STATUS_CODE_NEW = "new";

    public static final String STATUS_CODE_SUCCESS = "success";
    public static final String STATUS_CODE_ERROR = "error";

    public static final String STATUS_CODE_UPDATE = "update";
    public static final String STATUS_CODE_ACTIVE = "active";
    public static final String STATUS_CODE_DISABLED = "disabled";
    public static final String STATUS_CODE_DELETE = "delete";
    public static final String STATUS_CODE_DELETED = "deleted";
    public static final String STATUS_CODE_EMPTY = "empty";
    public static final String STATUS_CODE_NOT_AUTHORIZED = "not_authorized";
    public static final String STATUS_CODE_QUERY = "query";
    public static final String STATUS_CODE_LIST_NOT_FOUND = "List not found!";

    public static final String JSON_DATA_TYPE_TEMP = "temp";
    public static final String JSON_DATA_TYPE_AIR_CON = "air-con";
    public static final String JSON_DATA_TYPE_RELAY = "relay";

    public static final String ROLE_ADMIN = "admin";

    public static final String TRUE_STRING = "true";
    public static final String FALSE_STRING = "false";

    public static final int LIST_QUERY_LIMIT = 10;
    public static final String PROD_STRING = "prod";
}
