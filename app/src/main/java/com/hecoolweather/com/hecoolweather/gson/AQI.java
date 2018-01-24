package com.hecoolweather.com.hecoolweather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Administrator on 2018/1/24.
 */

public class AQI {
    @SerializedName("city")
    public AQICity city;
    public class AQICity{
        public String aqi;
        public String pm25;
    }
}
