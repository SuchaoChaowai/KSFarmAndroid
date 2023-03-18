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

        IOTDevice iotDevice = null;

        try {

            iotDevice = device_ids.stream().filter(device_id -> deviceId.equals(device_id.id)).findFirst().orElse(null);
        } catch (NullPointerException nex) {

            return null;
        }

        return iotDevice;
    }
}
