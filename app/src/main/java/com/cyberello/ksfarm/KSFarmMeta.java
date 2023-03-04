package com.cyberello.ksfarm;

import com.cyberello.ksfarm.data.LonganQount;

public class KSFarmMeta {

    private static LonganQount longanQount;

    public static LonganQount longanQount() {

        if (longanQount == null) {
            longanQount = new LonganQount();
        }

        return longanQount;
    }
}
