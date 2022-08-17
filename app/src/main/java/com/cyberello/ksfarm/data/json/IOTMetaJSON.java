package com.cyberello.ksfarm.data.json;

import java.util.ArrayList;

public class IOTMetaJSON {

    public double version;

    public ArrayList<IOTDevice> device_ids;

    public class IOTDevice {

        public String name;
        public String id;
        public String description;
    }

    public IOTDevice getDevice(String deviceId) {

        return device_ids.stream().filter(device_id -> deviceId.equals(device_id.id)).findFirst().orElse(null);
    }
}
