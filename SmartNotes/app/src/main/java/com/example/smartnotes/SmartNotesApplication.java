package com.example.smartnotes;

import android.app.Application;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;

import com.example.smartnotes.config.AppConfig;
import com.iflytek.sparkchain.core.SparkChain;
import com.iflytek.sparkchain.core.rtasr.RTASR;
import com.iflytek.sparkchain.core.SparkChainConfig;
import androidx.multidex.MultiDex;

import java.io.File;


public class SmartNotesApplication extends Application {
    private static final String TAG = "SmartNotes";
    private static RTASR mRTASR;  // 添加静态实例

    static {
        try {
            System.loadLibrary("spark");
            System.loadLibrary("SparkChain");
        } catch (UnsatisfiedLinkError e) {
            Log.e(TAG, "Failed to load library", e);
        }
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        
        try {
            // 1. 创建并初始化 SparkChain SDK
            Log.d(TAG, "Starting SparkChain SDK initialization...");
            Log.d(TAG, "APP_ID: " + AppConfig.IFLYTEK_APP_ID);
            Log.d(TAG, "API_KEY: " + AppConfig.RTASR_API_KEY);
            
            SparkChainConfig config = SparkChainConfig.builder()
                    .appID(AppConfig.IFLYTEK_APP_ID)
                    .apiKey(AppConfig.RTASR_API_KEY)
                    .apiSecret(AppConfig.RTASR_API_SECRET);
            
            Log.d(TAG, "SparkChainConfig created successfully");
            int ret = SparkChain.getInst().init(getApplicationContext(), config);
            Log.d(TAG, "SparkChain SDK initialization returned: " + ret);
            
            if (ret != 0) {
                Log.e(TAG, "SparkChain SDK initialization failed with code: " + ret);
                Log.e(TAG, "Please check if APP_ID and API_KEY are correct");
                Log.e(TAG, "APP_ID: " + AppConfig.IFLYTEK_APP_ID);
                Log.e(TAG, "API_KEY: " + AppConfig.RTASR_API_KEY);
                return;
            }
            Log.d(TAG, "SparkChain SDK initialized successfully");
            
            try {
                // 2. 创建 RTASR 实例
                Log.d(TAG, "Creating RTASR instance...");
                mRTASR = new RTASR(AppConfig.RTASR_rtAsrApiKey);
                Log.d(TAG, "RTASR instance created successfully");
                
                // 3. 设置基本参数
                Log.d(TAG, "Setting RTASR parameters...");
                mRTASR.lang("cn");                       // 设置语言
                mRTASR.punc("1");                        // 开启标点
                mRTASR.pd("edu");                        // 设置场景
                Log.d(TAG, "RTASR parameters set successfully");
            } catch (Exception e) {
                Log.e(TAG, "Failed to initialize RTASR", e);
                Log.e(TAG, "Error type: " + e.getClass().getName());
                Log.e(TAG, "Error message: " + e.getMessage());
                mRTASR = null;  // 如果初始化失败，将实例设为 null
            }
            
        } catch (Exception e) {
            Log.e(TAG, "Failed to initialize SparkChain SDK", e);
            e.printStackTrace();
        }
        
        // 打印应用信息
        Log.i("SmartNotes", "Package name: " + getPackageName());
        Log.i("SmartNotes", "Application class: " + getClass().getName());
        
        // 打印 native 库目录和文件
        String nativeLibDir = getApplicationInfo().nativeLibraryDir;
        Log.i("SmartNotes", "Native library dir: " + nativeLibDir);
        
        File dir = new File(nativeLibDir);
        if (dir.exists() && dir.isDirectory()) {
            String[] files = dir.list();
            if (files != null) {
                Log.i("SmartNotes", "Native libraries:");
                for (String file : files) {
                    File libFile = new File(dir, file);
                    Log.i("SmartNotes", String.format(" - %s (%d bytes)", file, libFile.length()));
                }
            }
        }
    }

    // 提供静态方法获取 RTASR 实例
    public static RTASR getRTASR() {
        return mRTASR;
    }
} 