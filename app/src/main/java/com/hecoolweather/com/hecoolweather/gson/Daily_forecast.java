package com.hecoolweather.com.hecoolweather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Administrator on 2018/1/24.
 */

public class Daily_forecast {
    public String date;
    @SerializedName("cond")
    public More  more;
    public class More{
        @SerializedName("txt_d")
        public String moreInfo;
    }
    @SerializedName("tmp")
    public Temperature temperature;
    public class Temperature{
        @SerializedName("max")
        public String maxTemperature;
        @SerializedName("min")
        public String minTemperature;
    }
}
