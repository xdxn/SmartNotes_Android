package com.example.smartnotes.network;

import android.util.Log;
import com.example.smartnotes.config.AppConfig;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * HTTP请求工具类
 * 封装OkHttp，提供基础的网络请求方法
 */
public class HttpUtil {
    private static final String TAG = "HttpUtil";
    private static final String BASE_URL = AppConfig.getBaseUrl();
    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private static final OkHttpClient client = new OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .writeTimeout(15, TimeUnit.SECONDS)
        .build();

    /**
     * 发送POST请求
     * @param url 接口路径
     * @param json 请求体JSON字符串
     * @param callback 回调接口
     */
    public static void post(String url, String json, Callback callback) {
        RequestBody body = RequestBody.create(json, JSON);
        String fullUrl = BASE_URL + url;
        
        // 添加日志
        Log.d(TAG, "POST请求: " + fullUrl);
        Log.d(TAG, "请求体: " + json);

        Request request = new Request.Builder()
                .url(fullUrl)
                .post(body)
                .build();
                
        client.newCall(request).enqueue(callback);
    }

    /**
     * 发送GET请求
     */
    public static void get(String url, Callback callback) {
        String fullUrl = BASE_URL + "/" + url;
        Request request = new Request.Builder()
                .url(fullUrl)
                .build();
        client.newCall(request).enqueue(callback);
    }

    /**
     * 发送POST请求（表单格式）
     */
    public static void postForm(String url, String key, String value, Callback callback) {
        FormBody body = new FormBody.Builder()
                .add(key, value)
                .build();
                
        String fullUrl = BASE_URL + url;
        Log.d(TAG, "POST请求: " + fullUrl);
        Log.d(TAG, "参数: " + key + "=" + value);

        Request request = new Request.Builder()
                .url(fullUrl)
                .post(body)
                .build();
                
        client.newCall(request).enqueue(callback);
    }

    /**
     * 发送POST请求（FormBody格式）
     */
    public static void post(String url, FormBody formBody, Callback callback) {
        String fullUrl = BASE_URL + url;
        
        // 添加日志
        Log.d(TAG, "POST请求: " + fullUrl);
        StringBuilder params = new StringBuilder();
        for (int i = 0; i < formBody.size(); i++) {
            params.append(formBody.name(i)).append("=").append(formBody.value(i)).append("&");
        }
        Log.d(TAG, "请求参数: " + params.toString());

        Request request = new Request.Builder()
                .url(fullUrl)
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .post(formBody)
                .build();
                
        client.newCall(request).enqueue(callback);
    }
} 