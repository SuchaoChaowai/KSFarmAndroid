package com.cyberello.ksfarm.util;

import com.cyberello.CyberelloContants;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class KSFarmUtil {

    private static Gson gson;

    public static Gson gson() {

        if (null == gson) {
            gson = new GsonBuilder().setDateFormat(CyberelloContants.SERVER_DATE_TIME_FORMAT_STRING)
                    .create();
        }

        return gson;
    }
}
