package com.micoandroid.micoweather.util;

import android.text.TextUtils;

import com.micoandroid.micoweather.db.City;
import com.micoandroid.micoweather.db.County;
import com.micoandroid.micoweather.db.Provience;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by lx on 17-5-24.
 */

public class JsonUtil {
    /*
    * 解析和处理服务器返回的省级数据
    * */
    public static boolean handleProvinceResponse(String response){
        if(!TextUtils.isEmpty(response)){
            try {
                JSONArray allProvinces = new JSONArray(response);
                for(int i = 0; i < allProvinces.length(); i++){
                    JSONObject provinceObject = allProvinces.getJSONObject(i);
                    Provience provience = new Provience();
                    provience.setProvinceName(provinceObject.getString("name"));
                    provience.setProvinceNum(provinceObject.getInt("id"));
                    provience.save();
                }
                return true;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    /*
    * 解析和处理服务器返回的市级数据
    * */
    public static boolean handleCityResponse(String response,int provinceId){
        if(!TextUtils.isEmpty(response)){
            try {
                JSONArray allCities = new JSONArray(response);
                for(int i = 0; i < allCities.length(); i++){
                    JSONObject cityObject = allCities.getJSONObject(i);
                    City cityies = new City();
                    cityies.setCityName(cityObject.getString("name"));
                    cityies.setCityNum(cityObject.getInt("id"));
                    cityies.setProvinceId(provinceId);
                    cityies.save();
                }
                return true;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    /*
    * 解析和处理服务器返回的县级数据
    * */
    public static boolean handleCountyResponse(String response,int cityId){
        if(!TextUtils.isEmpty(response)){
            try {
                JSONArray allCounties = new JSONArray(response);
                for (int i = 0; i < allCounties.length(); i++){
                    JSONObject countyObject = allCounties.getJSONObject(i);
                    County counties = new County();
                    counties.setCountyName(countyObject.getString("name"));
                    counties.setWeatherId(countyObject.getString("weather_id"));
                    counties.setCityId(cityId);
                    counties.save();
                }
                return true;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return false;
    }
}
