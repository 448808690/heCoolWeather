package com.hecoolweather.com.hecoolweather.gson;

import android.text.style.UpdateAppearance;

import com.google.gson.annotations.SerializedName;

/**
 * 包含字段：
 * city:城市名
 * id:城市对应的天气id
 * update{
 *     loc:天气的更新时间
 * }
 * Created by Administrator on 2018/1/24.
 */

public class Basic {
    @SerializedName("city")
    public String cityName;
    @SerializedName("id")
    public  String  weatherId;
    public Update update;
    public  class Update{
        @SerializedName("loc")
        public String updateTime;
    }
}
