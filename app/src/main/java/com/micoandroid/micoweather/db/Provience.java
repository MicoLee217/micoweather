package com.micoandroid.micoweather.db;

import org.litepal.crud.DataSupport;

/**
 * Created by lx on 17-5-24.
 */

public class Provience extends DataSupport {
    private int id;
    private String provinceName;
    private int provinceNum;

    public int getId(){
        return id;
    }

    public void setId(int id){
        this.id = id;
    }

    public String getProvinceName(){
        return provinceName;
    }

    public void setProvinceName(String provinceName){
        this.provinceName = provinceName;
    }

    public int getProvinceNum(){
        return provinceNum;
    }

    public void setProvinceNum(int provinceNum){
        this.provinceNum = provinceNum;
    }
}
