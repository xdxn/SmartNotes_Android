package com.example.smartnotes.model.response;

/**
 * 通用响应数据模型
 * @param <T> 具体的数据类型
 */
public class BaseResponse<T> {
    private int code;           // 状态码：0成功，非0失败
    private String message;     // 提示信息
    private T data;            // 具体数据

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public T getData() {
        return data;
    }
} 