package com.cyberello.ksfarm.data.json;

import com.google.gson.annotations.SerializedName;

public class JSONDataWrapper {

    @SerializedName(value = "jsonData")
    public String jsonData;

    @SerializedName(value = "type")
    public String type;

    public String getJsonData() {

        return jsonData;
    }

    public void setJsonData(String jsonData) {

        this.jsonData = jsonData;
    }

    public String getType() {

        return type;
    }

    public void setType(String type) {

        this.type = type;
    }

    public boolean type(String type) {

        return this.type.equals(type);
    }
}
