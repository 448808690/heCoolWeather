package com.hecoolweather.com.hecoolweather.util;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.util.Util;
import com.hecoolweather.com.hecoolweather.R;
import com.hecoolweather.com.hecoolweather.gson.Daily_forecast;
import com.hecoolweather.com.hecoolweather.gson.Weather;
import com.hecoolweather.com.hecoolweather.service.AutoUpdateService;

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
    private TextView aqiText;
    private TextView pm25Text;
    private TextView comfortText;
    private TextView cleanCarText;
    private TextView sportText;
    private ImageView image_background;
    public SwipeRefreshLayout swipeRefreshLayout;
    private String mWeatherId;
    private Button openDrawerLayoutBT;
    public DrawerLayout drawerLayout;
    private AutoCompleteTextView autoCompleteTextViewSeclectCity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= 21) {
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
        setContentView(R.layout.activity_weather);

        //初始化控件
        weatherLayout = (ScrollView) findViewById(R.id.weather_layout);
        titleCity = (TextView) findViewById(R.id.title_city_text);
        titleUpdateTime = (TextView) findViewById(R.id.title_update_time);
        degreeText = (TextView) findViewById(R.id.degree_text);
        weatherInfoText = (TextView) findViewById(R.id.weather_info_text);
        forecastLayout = (LinearLayout) findViewById(R.id.forecast_layout);
        aqiText = (TextView) findViewById(R.id.aqi_text);
        pm25Text = (TextView) findViewById(R.id.pm2_5_text);
        comfortText = (TextView) findViewById(R.id.comfort_text);
        cleanCarText = (TextView) findViewById(R.id.clean_car_text);
        sportText = (TextView) findViewById(R.id.sport_text);
        image_background = (ImageView) findViewById(R.id.image_background);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        openDrawerLayoutBT = (Button) findViewById(R.id.button_open_drawerlayout);
        openDrawerLayoutBT.setAlpha(0.8f);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawerlayout_id);
        openDrawerLayoutBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String weatherString = prefs.getString("weather", null);
        String imageContent = prefs.getString("image_url", null);
        if (imageContent != null) {
            //
            Glide.with(this).load(imageContent).into(image_background);
        } else {
            //没有缓存从网络上请求并显添加到控件上
            showImage();
        }
        if (weatherString != null) {
            //有缓存的时候解析天气并显示到界面上
            Weather weather = Utility.handleWeatherResponse(weatherString);

            showWeatherInfo(weather);
        } else {
            //没有缓存则去服务器获取并显示到界面上
            mWeatherId = getIntent().getStringExtra("weather_id");
            weatherLayout.setVisibility(View.INVISIBLE);
            requestWeather(mWeatherId);

        }
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                SharedPreferences prefs=PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this);
               String weatherString= prefs.getString("weather",null);

                Weather weather=Utility.handleWeatherResponse(weatherString);
                mWeatherId = weather.basic.weatherId;
                requestWeather(mWeatherId);
            }
        });

    }


    private void showImage() {
        String imageURL = "http://guolin.tech/api/bing_pic";
        HttpUtil.sendOkhttpRequest(imageURL, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String imageInfo = response.body().string();
                SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this).edit();
                editor.putString("image_url", imageInfo);
                editor.apply();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Glide.with(WeatherActivity.this).load(imageInfo).into(image_background);
                    }
                });
            }
        });
    }

    public void requestWeather(String weatherId) {
        String weatherURL = "http://guolin.tech/api/weather?cityid=" + weatherId + "&key=124203c132bc4afc95eefc81623bd752";
        HttpUtil.sendOkhttpRequest(weatherURL, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(WeatherActivity.this, "获取天气失败", Toast.LENGTH_SHORT).show();
                        swipeRefreshLayout.setRefreshing(false);
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseText = response.body().string();
                final Weather weather = Utility.handleWeatherResponse(responseText);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (weather != null && "ok".equals(weather.status)) {
                            //有网络数据添加缓存并显示到界面上
                            SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this).edit();
                            editor.putString("weather", responseText);
                            editor.apply();
                            showWeatherInfo(weather);

                        } else {
                            Toast.makeText(WeatherActivity.this, "获取天气失败", Toast.LENGTH_SHORT).show();
                        }
                        swipeRefreshLayout.setRefreshing(false);
                    }
                });
            }
        });
    }

    private void showWeatherInfo(Weather weather) {
        if(weather!=null&&"ok".equals(weather.status)){
        String cityName = weather.basic.cityName;
        String updateTime = weather.basic.update.updateTime.split(" ")[1];
        String degree = weather.now.temperature;
        String weatherInfo = weather.now.more.moreInfo;
        titleCity.setText(cityName);
        titleUpdateTime.setText(updateTime);
        degreeText.setText(degree);
        weatherInfoText.setText(weatherInfo);
        forecastLayout.removeAllViews();
            Intent intent=new Intent(this, AutoUpdateService.class);
            startService(intent);
        for (Daily_forecast forecast : weather.daily_forecastList) {
            View view = LayoutInflater.from(this).inflate(R.layout.forecast_item, forecastLayout, false);
            TextView dateText = (TextView) view.findViewById(R.id.date_text);
            TextView infoText = (TextView) view.findViewById(R.id.info_text);
            TextView maxText = (TextView) view.findViewById(R.id.max_text);
            TextView minText = (TextView) view.findViewById(R.id.min_text);
            dateText.setText(forecast.date);
            infoText.setText(forecast.more.moreInfo);
            maxText.setText(forecast.temperature.maxTemperature);
            minText.setText(forecast.temperature.minTemperature);
            forecastLayout.addView(view);
        }
        if (weather.aqi != null) {
            aqiText.setText(weather.aqi.city.aqi);
            pm25Text.setText(weather.aqi.city.pm25);

        }
        String comfortTt = "舒适度:\n" + weather.suggestion.comf.comfInfo;
        String cleanCar = "洗车指数:\n" + weather.suggestion.cleanCar.cleanCarInfo;
        String sport = "运动指数:\n"  + weather.suggestion.sport.sportInfo;
        comfortText.setText(comfortTt);
        cleanCarText.setText(cleanCar);
        sportText.setText(sport);
        weatherLayout.setVisibility(View.VISIBLE);

    }
        else{
            Toast.makeText(this,"获取天气失败",Toast.LENGTH_SHORT).show();
        }
    }

}
