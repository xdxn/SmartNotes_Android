package com.example.smartnotes.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.smartnotes.config.AppConfig;
import com.example.smartnotes.model.LoginResponse;
import com.google.gson.Gson;

/**
 * 用户信息管理工具类
 * 处理用户信息的本地存储和获取
 */
public class UserManager {
    private static final Gson gson = new Gson();
    
    /**
     * 保存登录信息
     */
    public static void saveLoginInfo(Context context, LoginResponse loginResponse) {
        SharedPreferences sp = context.getSharedPreferences(AppConfig.PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        
        // 保存token
        editor.putString(AppConfig.PREF_TOKEN, loginResponse.getToken());
        
        // 保存用户信息
        String userInfoJson = gson.toJson(loginResponse.getUserInfo());
        editor.putString(AppConfig.PREF_USER_INFO, userInfoJson);
        
        editor.apply();
    }
    
    /**
     * 获取用户token
     */
    public static String getToken(Context context) {
        SharedPreferences sp = context.getSharedPreferences(AppConfig.PREF_NAME, Context.MODE_PRIVATE);
        return sp.getString(AppConfig.PREF_TOKEN, null);
    }
    
    public static LoginResponse.UserInfo getUserInfo(Context context) {
        SharedPreferences sp = context.getSharedPreferences(AppConfig.PREF_NAME, Context.MODE_PRIVATE);
        String userInfoJson = sp.getString(AppConfig.PREF_USER_INFO, null);
        if (userInfoJson != null) {
            return gson.fromJson(userInfoJson, LoginResponse.UserInfo.class);
        }
        return null;
    }
    
    public static void logout(Context context) {
        SharedPreferences sp = context.getSharedPreferences(AppConfig.PREF_NAME, Context.MODE_PRIVATE);
        sp.edit().clear().apply();
    }
} 