package com.hecoolweather.com.hecoolweather.util;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.util.Util;
import com.hecoolweather.com.hecoolweather.R;
import com.hecoolweather.com.hecoolweather.gson.Daily_forecast;
import com.hecoolweather.com.hecoolweather.gson.Weather;

import java.io.IOException;
import java.util.zip.Inflater;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class WeatherActivity extends AppCompatActivity {
    private ScrollView weatherLayout;
    private TextView titleCity;
    private TextView titleUpdateTime;
    private TextView degreeText;
    private TextView weatherInfoText;
    private LinearLayout forecastLayout;
    private TextView   apiText;
    private TextView pm25Text;
    private TextView comfortText;
    private TextView cleanCarText;
    private TextView sportText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);
        //初始化控件
        weatherLayout= (ScrollView) findViewById(R.id.weather_layout);
        titleCity= (TextView) findViewById(R.id.title_city_text);
        titleUpdateTime= (TextView) findViewById(R.id.title_update_time);
        degreeText= (TextView) findViewById(R.id.degree_text);
        weatherInfoText= (TextView) findViewById(R.id.weather_info_text);
        forecastLayout= (LinearLayout) findViewById(R.id.forecast_layout);
        apiText= (TextView) findViewById(R.id.api_text);
        pm25Text= (TextView) findViewById(R.id.pm2_5_text);
        comfortText= (TextView) findViewById(R.id.comfort_text);
        cleanCarText= (TextView) findViewById(R.id.clean_car_text);
        sportText= (TextView) findViewById(R.id.sport_text);
        SharedPreferences prefs= PreferenceManager.getDefaultSharedPreferences(this);
        String weatherString=prefs.getString("weather",null);
        if(weatherString!=null){
            //有缓存的时候解析天气并显示到界面上
            Weather weather= Utility.handleWeatherResponse(weatherString);
            showWeatherInfo(weather);
        }else {
            //没有缓存则去服务器获取并显示到界面上
            String weatherId=getIntent().getStringExtra("weather_id");
            requestWeather(weatherId);

        }

    }

    private void requestWeather(String weatherId) {
        String weatherURL="http://guolin.tech/api/weather?cityid="+weatherId+"&key=124203c132bc4afc95eefc81623bd752";
        HttpUtil.sendOkhttpRequest(weatherURL, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(WeatherActivity.this,"获取天气失败",Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
final String responseText=response.body().string();
                final Weather weather=Utility.handleWeatherResponse(responseText);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(weather!=null&&"ok".equals(weather.status)){
                            //有网络数据添加缓存并显示到界面上
                            SharedPreferences.Editor editor=PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this).edit();
                            editor.putString("weather",responseText);
                            editor.apply();
                            showWeatherInfo(weather);

                        }else{
                            Toast.makeText(WeatherActivity.this,"获取天气失败",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }

    private void showWeatherInfo(Weather weather) {
        String cityName=weather.basic.cityName;
        String updateTime=weather.basic.update.updateTime.split(" ")[1];
        String degree=weather.now.temperature;
        String weatherInfo=weather.now.more.moreInfo;
        titleCity.setText(cityName);
        titleUpdateTime.setText(updateTime);
        degreeText.setText(degree);
        weatherInfoText.setText(weatherInfo);
        forecastLayout.removeAllViews();
        for(Daily_forecast forecast:weather.daily_forecastList){
            View view= LayoutInflater.from(this).inflate(R.layout.forecast_item,forecastLayout,false);
            TextView dateText= (TextView) view.findViewById(R.id.date_text);
            TextView infoText= (TextView) view.findViewById(R.id.info_text);
            TextView maxText= (TextView) view.findViewById(R.id.max_text);
            TextView minText= (TextView) view.findViewById(R.id.min_text);
            dateText.setText(forecast.date);
            infoText.setText(forecast.more.moreInfo);
            maxText.setText(forecast.temperature.maxTemperature);
            minText.setText(forecast.temperature.minTemperature);
            forecastLayout.addView(view);
        }
        if(weather.aqi!=null){
            apiText.setText(weather.aqi.city.aqi);
            pm25Text.setText(weather.aqi.city.pm25);

        }
        String comfortTt="舒适度:\n"+weather.suggestion.comf.comfInfo;
        String cleanCar="洗车指数:\n"+"/n"+weather.suggestion.cleanCar.cleanCarInfo;
        String sport="运动指数:\n"+"/n"+weather.suggestion.sport.sportInfo;
        comfortText.setText(comfortTt);
        cleanCarText.setText(cleanCar);
        sportText.setText(sport);
        weatherLayout.setVisibility(View.VISIBLE);

    }
}
