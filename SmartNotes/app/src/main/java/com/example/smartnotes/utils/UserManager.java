package com.example.smartnotes.utils;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import com.example.smartnotes.config.AppConfig;
import com.example.smartnotes.model.LoginResponse;
import com.example.smartnotes.ui.activity.LoginActivity;
import com.google.gson.Gson;

/**
 * 用户信息管理工具类
 * 处理用户信息的本地存储和获取
 */
public class UserManager {
    private static final String TAG = "UserManager";
    private static final String PREF_NAME = "user_pref";
    private static final String KEY_USER_INFO = "user_info";
    private static UserManager instance;
    private LoginResponse currentUser;
    private static final Gson gson = new Gson();

    private UserManager() {
        // 私有构造函数
        Log.d(TAG, "UserManager 实例化");
    }

    public static synchronized UserManager getInstance() {
        if (instance == null) {
            Log.d(TAG, "创建新的 UserManager 实例");
            instance = new UserManager();
        }
        return instance;
    }

    /**
     * 保存登录信息
     */
    public static void saveLoginInfo(Context context, LoginResponse loginResponse) {
        Log.d(TAG, "开始保存登录信息");
        if (context == null) {
            Log.e(TAG, "保存失败：context 为空");
            return;
        }
        if (loginResponse == null) {
            Log.e(TAG, "保存失败：loginResponse 为空");
            return;
        }
        
        try {
            SharedPreferences pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
            String userJson = gson.toJson(loginResponse);
            Log.d(TAG, "序列化的用户信息: " + userJson);
            
            SharedPreferences.Editor editor = pref.edit();
            editor.putString(KEY_USER_INFO, userJson);
            editor.apply();
            Log.d(TAG, "用户信息已保存到 SharedPreferences");
            
            // 更新当前用户信息
            getInstance().currentUser = loginResponse;
            Log.d(TAG, "当前用户信息已更新到内存");
        } catch (Exception e) {
            Log.e(TAG, "保存登录信息时发生错误: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public LoginResponse getCurrentUser() {
        Log.d(TAG, "获取当前用户信息: " + (currentUser != null ? "用户已登录" : "用户未登录"));
        return currentUser;
    }

    /**
     * 获取用户token
     */
    public static String getToken(Context context) {
        Log.d(TAG, "开始获取用户 token");
        LoginResponse user = getInstance().getCurrentUser();
        if (user != null) {
            String token = user.getToken();
            Log.d(TAG, "获取到 token: " + (token != null ? token.substring(0, 20) + "..." : "null"));
            return token;
        }
        Log.d(TAG, "获取 token 失败：用户未登录");
        return null;
    }

    public static void loadUserInfo(Context context) {
        Log.d(TAG, "开始加载用户信息");
        if (context == null) {
            Log.e(TAG, "加载失败：context 为空");
            return;
        }
        
        try {
            SharedPreferences pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
            String userJson = pref.getString(KEY_USER_INFO, null);
            Log.d(TAG, "从 SharedPreferences 读取的用户信息: " + userJson);
            
            if (userJson != null) {
                LoginResponse user = gson.fromJson(userJson, LoginResponse.class);
                if (user != null && user.getToken() != null) {
                    getInstance().currentUser = user;
                    Log.d(TAG, "用户信息已加载到内存: " + user.toString());
                } else {
                    Log.e(TAG, "解析的用户信息无效：" + (user == null ? "user为空" : "token为空"));
                    getInstance().currentUser = null;
                }
            } else {
                Log.d(TAG, "没有找到保存的用户信息");
                getInstance().currentUser = null;
            }
        } catch (Exception e) {
            Log.e(TAG, "加载用户信息时发生错误: " + e.getMessage());
            e.printStackTrace();
            getInstance().currentUser = null;
        }
    }

    public static void logout(Context context) {
        Log.d(TAG, "开始执行登出操作");
        if (context == null) {
            Log.e(TAG, "登出失败：context 为空");
            return;
        }
        
        try {
            SharedPreferences pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
            pref.edit().clear().apply();
            Log.d(TAG, "SharedPreferences 已清除");
            
            getInstance().currentUser = null;
            Log.d(TAG, "内存中的用户信息已清除");

            // 跳转到登录页面
            Intent intent = new Intent(context, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            context.startActivity(intent);
            Log.d(TAG, "已跳转到登录页面");
        } catch (Exception e) {
            Log.e(TAG, "登出操作时发生错误: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public boolean isLoggedIn() {
        boolean logged = currentUser != null;
        Log.d(TAG, "检查登录状态: " + (logged ? "已登录" : "未登录"));
        return logged;
    }
} 