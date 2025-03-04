package com.example.smartnotes.utils;

public class DevUtils {
    public static final String DEV_SERVER_IP = "192.168.1.13";
    public static final String DEV_SERVER_PORT = "8080";
    
    public static String getDevServerUrl() {
        return "http://" + DEV_SERVER_IP + ":" + DEV_SERVER_PORT;
    }
} 