package com.hecoolweather.com.hecoolweather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Administrator on 2018/1/24.
 */

public class Suggestion {

    public  Comf comf;
    public  class Comf{
        @SerializedName("txt")
        public String comfInfo;
    }
    @SerializedName("cw")
    public CleanCar cleanCar;
    public class CleanCar{
        @SerializedName("txt")
        public String cleanCarInfo;

    }
    public Sport sport;
    public class Sport{
        @SerializedName("txt")
        public String sportInfo;
    }
}
