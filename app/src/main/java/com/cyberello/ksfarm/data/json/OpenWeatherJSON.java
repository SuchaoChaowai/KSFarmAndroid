package com.cyberello.ksfarm.data.json;

import com.google.gson.Gson;

import java.util.List;

public class OpenWeatherJSON {

    class WeatherJSON {

        List<Weather> weather;
        Main main;
        long dt;
    }

    class Weather {

        int id;
        String main;
        String description;
        String icon;
    }

    class Main {

        float temp;
        int pressure;
        int humidity;
        int sea_level;
        int grnd_level;
    }

    private WeatherJSON weatherJSON;

    public void setJsonString(String jsonString){

        Gson G = new Gson();
        weatherJSON = G.fromJson(jsonString, WeatherJSON.class);
    }

    public float temperature() {
        return weatherJSON.main.temp;
    }

    public int humidity() {
        return weatherJSON.main.humidity;
    }

    public int pressure() {
        return weatherJSON.main.pressure;
    }

    public long dt() {
        return weatherJSON.dt;
    }
}