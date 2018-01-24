package com.hecoolweather.com.hecoolweather.gson;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Administrator on 2018/1/24.
 */

public class Weather {
    public String status;
    public Basic basic;
    public AQI aqi;
    public Now now;
    public Suggestion suggestion;
     @SerializedName("daily_forecast")//为了把json数据的字段跟java的命名对应起来的注解映射一下
    public List<Daily_forecast> daily_forecastList;
}
