// 删除这个类，因为我们改用表单格式发送请求 
package com.example.smartnotes.model.request;

public class RegisterRequest {
    private String phone;
    private String verificationCode;
    private String password;

    public RegisterRequest(String phone, String verificationCode, String password) {
        this.phone = phone;
        this.verificationCode = verificationCode;
        this.password = password;
    }
} 