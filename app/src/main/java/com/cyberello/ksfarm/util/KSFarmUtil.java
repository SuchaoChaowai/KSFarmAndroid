package com.cyberello.ksfarm.util;

import com.cyberello.global.CyberelloConstants;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.text.ParseException;
import java.util.Objects;

public class KSFarmUtil {

    private static Gson gson;

    public static Gson gson() {

        if (null == gson) {
            gson = new GsonBuilder().setDateFormat(CyberelloConstants.SERVER_DATE_TIME_FORMAT_STRING)
                    .create();
        }

        return gson;
    }

    public static String getServerDateTimeString(String dateTimeString) throws ParseException {

        return CyberelloConstants.DATE_TIME_FORMAT.format(Objects.requireNonNull(CyberelloConstants.SERVER_DATE_TIME_FORMAT.parse(dateTimeString)));
    }
}
