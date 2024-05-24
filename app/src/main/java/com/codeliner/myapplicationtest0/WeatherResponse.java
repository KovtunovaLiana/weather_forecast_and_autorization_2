package com.codeliner.myapplicationtest0;

import com.google.gson.annotations.SerializedName;

public class WeatherResponse {
    @SerializedName("main")
    public Main main;

    @SerializedName("wind")
    public Wind wind;

    @SerializedName("weather")
    public Weather[] weather;

    public class Main {
        @SerializedName("temp")
        public float temp;

        @SerializedName("humidity")
        public int humidity;

        @SerializedName("sea_level")
        public int seaLevel;
    }

    public class Wind {
        @SerializedName("speed")
        public float speed;
    }

    public class Weather {
        @SerializedName("description")
        public String description;
        @SerializedName("icon")
        public String icon;
    }
}
