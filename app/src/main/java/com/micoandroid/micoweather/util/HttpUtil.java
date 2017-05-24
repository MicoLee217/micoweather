package com.micoandroid.micoweather.util;

import okhttp3.OkHttpClient;
import okhttp3.Request;

/**
 * Created by lx on 17-5-24.
 */

public class HttpUtil {
    public static void sendOkHttpResquest(String address, okhttp3.Callback callback){
        OkHttpClient client = new OkHttpClient();
        Request resquest = new Request.Builder().url(address).build();
        client.newCall(resquest).enqueue(callback);
    }
}
