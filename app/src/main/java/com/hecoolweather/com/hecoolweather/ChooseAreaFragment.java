package com.hecoolweather.com.hecoolweather;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.hecoolweather.com.hecoolweather.db.City;
import com.hecoolweather.com.hecoolweather.db.County;
import com.hecoolweather.com.hecoolweather.db.Province;
import com.hecoolweather.com.hecoolweather.util.HttpUtil;
import com.hecoolweather.com.hecoolweather.util.Utility;

import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by Administrator on 2018/1/22.
 */

public class ChooseAreaFragment extends Fragment {
    public static final int LEVEL_PROVINCE=0;
    public static final int LEVEL_CITY=1;
    public static final int LEVEL_COUNTY=2;
    private ProgressDialog progressDialog;
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

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (currentLevel == LEVEL_PROVINCE) {
                    selectedProvince = listProvinces.get(i);
                    queryCities();
                }else if(currentLevel==LEVEL_CITY){
                    selectedCity=listCities.get(i);
                    queryCounties();
                }

            }

        });
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(currentLevel==LEVEL_COUNTY){
                    queryCities();

                }else if(currentLevel==LEVEL_CITY){
                    queryProvinces();
                }
            }
        });
        queryProvinces();
    }
    private void queryProvinces(){
        titleView.setText("中国");
        backButton.setVisibility(View.INVISIBLE);
        listProvinces=DataSupport.findAll(Province.class);
        if(listProvinces.size()>0){
            dataList.clear();
            for(Province province:listProvinces){
                dataList.add(province.getProvinceName());
            }
            adapter.notifyDataSetChanged();
            currentLevel=LEVEL_PROVINCE;
            listView.setSelection(0);

        }else {
            String address="www.guolin.tech/api/china";
            queryFromServer(address,"province");
        }

    }
    private void queryCounties(){
        titleView.setText(selectedCity.getCityName());
        dataList.clear();
        listCounties=DataSupport.where("cityId=?",String.valueOf(selectedCity.getId())).find(County.class);
        if(listCounties.size()>0) {

            for (County county : listCounties) {
                dataList.add(county.getCountyName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            currentLevel=LEVEL_COUNTY;
        }else{
            int province =selectedProvince.getprovincecode();
            int city=selectedCity.getCityCode();
            String address="www.guolin.tech/api/china/"+province+"/"+city;
            queryFromServer(address,"county");
        }

    }
    //优先从数据库查询选中省内的所有市,没有则从服务器中查询,并显示在ui上

    private void queryCities() {
        titleView.setText(selectedProvince.getProvinceName());
        backButton.setVisibility(View.VISIBLE);
        listCities= DataSupport.where("provinceId=?",String.valueOf(selectedProvince.getId())).find(City.class);
        if(listCities.size()>0){
            //遍历 城市添加到数据源中
            dataList.clear();
            for(City city:listCities){
                dataList.add(city.getCityName());

            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            currentLevel=LEVEL_CITY;

        }else{
            int provinceCode=selectedProvince.getprovincecode();
            String address="http://guolin.tech/api/china/"+provinceCode;
            queryFromServer(address,"city");
        }
    }
//从服务器端进行数据查询
    private void queryFromServer(String address, final String type) {
        showProgressDialog();
        HttpUtil.sendOkhttpRequest(address, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
getActivity().runOnUiThread(new Runnable() {
    @Override
    public void run() {
        closeProgressDialog();
        Toast.makeText(getContext(),"加载失败",Toast.LENGTH_SHORT).show();
    }
});
            }
//有了响应，需要执行数据解析，并进行ui操作
            @Override
            public void onResponse(Call call, Response response) throws IOException {
 String textResponse= response.body().string();
                Boolean result =false;
                if("province".equals(type)){
                    result= Utility.handleProvinceResponse(textResponse);

                }else
                if("city".equals(type)){
                    result=Utility.handleCityResponse(textResponse,selectedProvince.getId());
                }else if("county".equals(type)){
                    result=Utility.handleCountyResponse(textResponse,selectedCity.getId());
                }
                if(result){
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //
                            closeProgressDialog();
                            if("province".equals(type)){
                                queryProvinces();
                            }else if("city".equals(type)){
                                queryCities();

                            }else if("county".equals(type)){
                                queryCounties();
                            }
                        }
                    });


                }

            }
        });
    }
//显示进度对话框
    private void showProgressDialog() {
        if(progressDialog==null){
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("数据正在加载请稍等...");
            //手指离开对话框不关闭
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();
        }
    }
    //关闭进度对话框
    private void closeProgressDialog(){
        if(progressDialog!=null){
            progressDialog.dismiss();

        }
    }
}
