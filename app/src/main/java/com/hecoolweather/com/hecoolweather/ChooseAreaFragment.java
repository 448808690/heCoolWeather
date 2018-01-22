package com.hecoolweather.com.hecoolweather;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.hecoolweather.com.hecoolweather.db.City;
import com.hecoolweather.com.hecoolweather.db.County;
import com.hecoolweather.com.hecoolweather.db.Province;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018/1/22.
 */

public class ChooseAreaFragment extends Fragment {
    public static final int LEVEL_PROVINCE=0;
    public static final int LEVEL_CITY=1;
    public static final int LEVEL_COUNTY=2;
    private int currentLevel;
    private TextView titleView;
    private Button backButton;
    private ListView listView;
    private List<String> dataList=new ArrayList<>();
    private ArrayAdapter<String> adapter;
    //省列表
    private List<Province> listProvinces;
    //市级列表
    private List<City> listCities;
    //县级列表
    private List<County> listCounties;
    //选中的省
    private Province selectedProvince;
    //选中的市
    private City selectedCity;

    /**
     *
     */

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view=inflater.inflate(R.layout.choose_area,container,false);
        titleView= (TextView) view.findViewById(R.id.title_text);
        backButton= (Button) view.findViewById(R.id.back_button);
        listView= (ListView) view.findViewById(R.id.list_view);
        adapter=new ArrayAdapter<String>(getContext(),android.R.layout.simple_list_item_1,dataList);
        listView.setAdapter(adapter);


        return view;

    }
}
