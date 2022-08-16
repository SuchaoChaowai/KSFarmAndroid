package com.cyberello.ksfarm.data.json;

import java.util.ArrayList;

public class IOTMetaJSON {

    public double version;

    public ArrayList<DeviceId> device_ids;

    public class DeviceId {
        String name;
        String id;
    }

    public String getDeviceName(String deviceId) {

        DeviceId device = device_ids.stream().filter(device_id -> deviceId.equals(device_id.id)).findFirst().orElse(null);

        if (device != null) {

            return device.name;
        }

        return null;
    }
}
