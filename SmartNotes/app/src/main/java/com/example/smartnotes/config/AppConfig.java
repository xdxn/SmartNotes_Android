package com.example.smartnotes.config;

/**
 * 应用程序配置类
 * 存储全局配置信息，如服务器地址、API路径等
 */
public class AppConfig {
    // 确保这是您后端服务器的正确地址和端口
    private static final String BASE_URL = "http://192.168.1.101:8080";
    
    // API接口路径（需要在开头加/）
    public static final String API_SEND_CODE = "/api/user/code";      // 发送验证码
    public static final String API_REGISTER = "/api/user/register";    // 注册
    public static final String API_LOGIN = "/api/user/login";          // 登录
    
    // SharedPreferences配置，用于本地存储用户信息
    public static final String PREF_NAME = "SmartNotes";     // SharedPreferences文件名
    public static final String PREF_TOKEN = "token";         // 存储token的key
    public static final String PREF_USER_INFO = "user_info"; // 存储用户信息的key
    
    public static String getBaseUrl() {
        return BASE_URL;
    }
} 