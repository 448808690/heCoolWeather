package com.hecoolweather.com.hecoolweather.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.SystemClock;
import android.preference.PreferenceManager;

import com.bumptech.glide.util.Util;
import com.hecoolweather.com.hecoolweather.gson.Weather;
import com.hecoolweather.com.hecoolweather.util.HttpUtil;
import com.hecoolweather.com.hecoolweather.util.Utility;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class AutoUpdateService extends Service {
    public AutoUpdateService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        updateWeather();
        updateBingPic();
        //运用系统闹钟进行定时运行
        AlarmManager alarmManager= (AlarmManager) getSystemService(ALARM_SERVICE);
         int anHour=60*60*1000;//毫秒数
        long triggerAtTime= SystemClock.elapsedRealtime()+anHour;
        Intent i=new Intent(this,AutoUpdateService.class);
        PendingIntent pi=PendingIntent.getService(this,0,i,0);
        alarmManager.cancel(pi);
        alarmManager.set(AlarmManager.ELAPSED_REALTIME,triggerAtTime,pi);


        return super.onStartCommand(intent, flags, startId);
    }

    private void updateBingPic() {
        String picURL="http://guolin.tech/api/bing_pic";
        HttpUtil.sendOkhttpRequest(picURL, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
String image_url=response.body().string();
                SharedPreferences.Editor editor= PreferenceManager.getDefaultSharedPreferences(AutoUpdateService.this).edit();
                editor.putString("image_url",image_url);
                editor.apply();
            }
        });
    }

    private void updateWeather() {
        SharedPreferences prefs= PreferenceManager.getDefaultSharedPreferences(this);
        String weatherString= prefs.getString("weather",null);
        if(weatherString!=null){
            //有缓存就需要更新这个天气缓存
            Weather weather= Utility.handleWeatherResponse(weatherString);
            String weatherId=weather.basic.weatherId;
            String weatherURL="http://guolin.tech/api/weather?city="+weatherId+"&key=124203c132bc4afc95eefc81623bd752";
            HttpUtil.sendOkhttpRequest(weatherURL, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {

                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
String weatherText=response.body().string();
                    Weather weather= Utility.handleWeatherResponse(weatherText);
                    if(weatherText!=null&&"ok".equals(weather.status)) {
                        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(AutoUpdateService.this).edit();

                        editor.putString("weather", weatherText);
                        editor.apply();
                    }
                }
            });
        }
    }
}
