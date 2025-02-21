package com.example.smartnotes.utils;

public class DevUtils {
    // 在这里修改IP地址，只需要改一个地方
    public static final String DEV_SERVER_IP = "192.168.31.123";
    public static final String DEV_SERVER_PORT = "8080";
    
    public static String getDevServerUrl() {
        return "http://" + DEV_SERVER_IP + ":" + DEV_SERVER_PORT;
    }
} 