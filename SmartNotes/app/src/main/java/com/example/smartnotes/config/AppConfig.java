package com.example.smartnotes.config;

import android.util.Log;
import com.example.smartnotes.utils.DevUtils;

/**
 * 应用程序配置类
 * 存储全局配置信息，如服务器地址、API路径等
 */
public class AppConfig {

    private static final String TAG = "AppConfig";

    // API接口路径（需要在开头加/）
    public static final String API_SEND_CODE = "/api/user/code";      // 发送验证码
    public static final String API_REGISTER = "/api/user/register";    // 注册
    public static final String API_LOGIN = "/api/user/login";          // 登录
    public static final String API_GENERATE_SUMMARY = "/api/generate/summary"; // 生成摘要的 API 路径
    
    // SharedPreferences配置，用于本地存储用户信息
    public static final String PREF_NAME = "SmartNotes";     // SharedPreferences文件名
    public static final String PREF_TOKEN = "token";         // 存储token的key
    public static final String PREF_USER_INFO = "user_info"; // 存储用户信息的key
    
    // 讯飞语音配置
    public static final String IFLYTEK_APP_ID = "d0616090";  // 确保这是正确的 APPID
    public static final String RTASR_API_KEY = "0e8b7746eb28312fc09efb36fa62e04d";
    // 用于实时语音转写
    public static final String RTASR_API_SECRET = "M2Q3MzRlNjg1YjVlMDY2Nzc1NGUxNDUz";
    // 文件保存路径
    public static final String TRANSCRIPTION_PATH = "/storage/emulated/0/Android/data/com.example.smartnotes/files/Transcriptions/";
    public static String RTASR_rtAsrApiKey="e38d7f8dc33f9be6a55b1c56c6f78363";

    public static String getBaseUrl() {
        // 使用开发环境的服务器地址
        String baseUrl = DevUtils.getDevServerUrl();
        Log.d(TAG, "基础 URL: " + baseUrl);
        return baseUrl;
    }
} 