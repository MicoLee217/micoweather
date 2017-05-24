package com.micoandroid.micoweather.db;

import org.litepal.crud.DataSupport;

/**
 * Created by lx on 17-5-24.
 */

public class City extends DataSupport {
    private int id;
    private int cityNum;
    private int provinceId;
    private String cityName;

    public int getId(){
        return id;
    }

    public void setId(int id){
        this.id = id;
    }

    public int getCityNum(){
        return cityNum;
    }

    public void setCityNum(int cityNum){
        this.cityNum = cityNum;
    }

    public int getProvinceId(){
        return provinceId;
    }

    public void setProvinceId(int provinceId){
        this.provinceId = provinceId;
    }

    public String getCityName(){
        return cityName;
    }

    public void setCityName(String cityName){
        this.cityName = cityName;
    }
}
