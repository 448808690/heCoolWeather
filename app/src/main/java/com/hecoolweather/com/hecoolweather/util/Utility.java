package com.hecoolweather.com.hecoolweather.util;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.hecoolweather.com.hecoolweather.db.City;
import com.hecoolweather.com.hecoolweather.db.County;
import com.hecoolweather.com.hecoolweather.db.Province;
import com.hecoolweather.com.hecoolweather.gson.Weather;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Administrator on 2018/1/21.
 */


public class Utility {
    /**
     * 解析服务器返回的省级数据，并添加到数据库中
     */
    public static boolean handleProvinceResponse(String response) {
        if (!TextUtils.isEmpty(response)) {
            try {
                JSONArray allProvince = new JSONArray(response);
                for (int i = 0; i < allProvince.length(); i++) {
                    JSONObject provinceObject = allProvince.getJSONObject(i);
                    Province province = new Province();

                    province.setProvinceCode(provinceObject.getInt("id"));
                    province.setProvinceName(provinceObject.getString("name"));
                    //数据库保存数据
                    province.save();
                }
                return true;
            } catch (JSONException e) {
                e.printStackTrace();
            }


        }
        return false;

    }

    /**
     * 解析city数据并进行数据处理
     *
     * @param response
     * @param provinceId
     * @return
     */
    public static boolean handleCityResponse(String response, int provinceId) {
        if (!TextUtils.isEmpty(response)) {
            try {
                JSONArray allCities = new JSONArray(response);
                for (int i = 0; i < allCities.length(); i++) {
                    JSONObject cityObject = allCities.getJSONObject(i);
                    City city = new City();
                    city.setCityName(cityObject.getString("name"));
                    city.setCityCode(cityObject.getInt("id"));
                    city.setProvinceId(provinceId);
                    city.save();

                }
                return true;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * 解析county并保存数据于数据库中
     */
    public static boolean handleCountyResponse(String response, int cityId) {
        if (!TextUtils.isEmpty(response)) {
            try {
                JSONArray allCounties = new JSONArray(response);
                for (int i = 0; i < allCounties.length(); i++) {
                    JSONObject countyObject = allCounties.getJSONObject(i);
                    County county = new County();
                    county.setCountyName(countyObject.getString("name"));
                    county.setWeatherId(countyObject.getString("weather_id"));
                    county.setCityId(cityId);
                    county.save();

                }
                return true;
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        return false;

    }
    public static Weather handleWeatherResponse(String response){
        if(!TextUtils.isEmpty(response)){
            try {
                JSONObject jsonObject=new JSONObject(response);
                JSONArray jsonArray=jsonObject.getJSONArray("HeWeather");
                String weatherContent=jsonArray.getJSONObject(0).toString();
                return new Gson().fromJson(weatherContent,Weather.class);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return null;
    }

}