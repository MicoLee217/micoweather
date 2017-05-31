package com.micoandroid.micoweather;


import android.app.ProgressDialog;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.micoandroid.micoweather.db.City;
import com.micoandroid.micoweather.db.County;
import com.micoandroid.micoweather.db.Provience;
import com.micoandroid.micoweather.util.HttpUtil;
import com.micoandroid.micoweather.util.JsonUtil;

import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.nio.channels.Channels;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;


/**
 * A simple {@link Fragment} subclass.
 */
public class ChooseAreaFragment extends Fragment {
    public static final int LEVEL_PROVINCE = 0;
    public static final int LEVEL_CITY = 1;
    public static final int LEVEL_COUNTY = 2;
    private ProgressDialog progressDialog;
    private Button backButton;
    private TextView titleText;
    private ListView listView;
    private ArrayAdapter<String> adapter;
    private List<String> dataList = new ArrayList<>();

    private List<Provience>  provienceList;      /*省列表*/
    private List<City>       cityList;           /*市列表*/
    private List<County>     countyList;         /*县列表*/

    private Provience  selectedProvience;  /*选中的省份*/
    private City       selectedCity;       /*选中的市*/
    private int currentlevel;  /*当前选中的级别*/

    public ChooseAreaFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.choose_area,container,false);
        titleText = (TextView) view.findViewById(R.id.tittle_text);
        backButton = (Button) view.findViewById(R.id.back_button);
        listView = (ListView) view.findViewById(R.id.list_view);
        adapter = new ArrayAdapter<>(getContext(),android.R.layout.simple_list_item_1,dataList);
        listView.setAdapter(adapter);
        return view;
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view,int position,long id){
                if (currentlevel == LEVEL_PROVINCE){
                    selectedProvience = provienceList.get(position);
                    queryCities();
                }else if (currentlevel == LEVEL_CITY){
                    selectedCity = cityList.get(position);
                    queryCouties();
                }
            }
        });
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentlevel == LEVEL_COUNTY){
                    queryCities();
                }else if (currentlevel == LEVEL_CITY){
                    queryProvinces();
                }
            }
        });
        queryProvinces();
    }

    /*
    * 查询全国所有的省，优先从数据库查询，如果没有就去服务器上查询
    * */
    private void queryProvinces() {
        titleText.setText("中国");
        backButton.setVisibility(View.GONE);
        provienceList = DataSupport.findAll(Provience.class);
        if (provienceList.size() > 0){
            dataList.clear();
            for (Provience province : provienceList){
                dataList.add(province.getProvinceName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            currentlevel = LEVEL_PROVINCE;
        }else {
            String address = "http://guolin.tech/api/china";
            queryFromServer(address,"province");
        }
    }

    /*
    * 查询选中省内所有的市，优先从数据库查询，如果没有就去服务器上查询
    * */
    private void queryCities() {
        titleText.setText(selectedProvience.getProvinceName());
        backButton.setVisibility(View.VISIBLE);
        cityList = DataSupport.where("provinceid = ?",String.valueOf(selectedProvience.getId())).find(City.class);
        if (cityList.size() > 0){
            dataList.clear();
            for (City city : cityList){
                dataList.add(city.getCityName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            currentlevel = LEVEL_CITY;
        }else {
            int provinceNum = selectedProvience.getProvinceNum();
            String address = "http://guolin.tech/api/china/"+provinceNum;
            queryFromServer(address,"city");
        }
    }

    /*
    * 查询选中省内所有的县，优先从数据库查询，如果没有就去服务器上查询
    * */
    private void queryCouties() {
        titleText.setText(selectedCity.getCityName());
        backButton.setVisibility(View.VISIBLE);
        countyList = DataSupport.where("cityid = ?",String.valueOf(selectedCity.getId())).find(County.class);
        if (countyList.size() > 0){
            dataList.clear();
            for (County county : countyList){
                dataList.add(county.getCountyName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            currentlevel = LEVEL_COUNTY;
        }else {
            int provinceNum = selectedProvience.getProvinceNum();
            int cityNum = selectedCity.getCityNum();
            String address = "http://guolin.tech/api/china/"+provinceNum+"/"+cityNum;
            queryFromServer(address,"county");
        }
    }

    /*
    * 根据传入的地址和类型从服务器上查询省市县数据
    * */
    private void queryFromServer(String address, final String type) {
        showProgessDialog();
        HttpUtil.sendOkHttpResquest(address, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseText = response.body().string();
                boolean result = false;
                if ("province".equals(type)){
                    result = JsonUtil.handleProvinceResponse(responseText);
                }else if ("city".equals(type)){
                    result = JsonUtil.handleCityResponse(responseText,selectedProvience.getId());
                }else if ("county".equals(type)){
                    result = JsonUtil.handleCountyResponse(responseText,selectedCity.getId());
                }
                if (result){
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            closeProgressDialog();
                            if ("province".equals(type)){
                                queryProvinces();
                            }else if ("city".equals(type)){
                                queryCities();
                            }else if ("county".equals(type)){
                                queryCouties();
                            }
                        }
                    });
                }
            }
        });
    }

    /*
    * 关闭进度对话框
    * */
    private void closeProgressDialog() {
        if (progressDialog != null){
            progressDialog.dismiss();
        }
    }

    /*
    * 显示进度对话框
    * */
    private void showProgessDialog() {
        if (progressDialog == null){
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("正在加载，嘻嘻嘻.....");
            progressDialog.setCanceledOnTouchOutside(false);
        }
        progressDialog.show();
    }

}
