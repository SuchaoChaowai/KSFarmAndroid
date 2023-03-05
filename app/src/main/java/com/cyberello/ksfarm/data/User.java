package com.cyberello.ksfarm.data;

import com.google.gson.annotations.SerializedName;

public class User {

    @SerializedName(value = "id")
    public String id;

    @SerializedName(value = "email")
    public String email;

    @SerializedName(value = "accountType")
    public String accountType;

    @SerializedName(value = "picture")
    public String picture;

    @SerializedName(value = "token")
    public String token;

    public User(String email, String accountType, String picture, String token) {

        this.email = email;
        this.accountType = accountType;
        this.picture = picture;
        this.token = token;
    }
}