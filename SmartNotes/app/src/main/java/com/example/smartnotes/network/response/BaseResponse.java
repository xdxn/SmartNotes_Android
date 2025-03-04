package com.example.smartnotes.network.response;

import android.util.Log;

/**
 * 通用响应数据模型
 * @param <T> 具体的数据类型
 */
public class BaseResponse<T> {
    private static final String TAG = "BaseResponse";
    private int code;           // 状态码：0成功，非0失败
    private String message;     // 提示信息
    private T data;            // 具体数据

    public int getCode() {
        Log.d(TAG, "获取状态码: " + code);
        return code;
    }

    public void setCode(int code) {
        Log.d(TAG, "设置状态码: " + code);
        this.code = code;
    }

    public String getMessage() {
        Log.d(TAG, "获取提示信息: " + message);
        return message;
    }

    public void setMessage(String message) {
        Log.d(TAG, "设置提示信息: " + message);
        this.message = message;
    }

    public T getData() {
        Log.d(TAG, "获取数据: " + (data != null ? data.toString() : "null"));
        return data;
    }

    public void setData(T data) {
        Log.d(TAG, "设置数据: " + (data != null ? data.toString() : "null"));
        this.data = data;
    }

    public boolean isSuccess() {
        boolean success = code == 200;
        Log.d(TAG, "检查是否成功: " + success);
        return success;
    }

    @Override
    public String toString() {
        return "BaseResponse{" +
                "code=" + code +
                ", message='" + message + '\'' +
                ", data=" + (data != null ? data.toString() : "null") +
                '}';
    }
} 