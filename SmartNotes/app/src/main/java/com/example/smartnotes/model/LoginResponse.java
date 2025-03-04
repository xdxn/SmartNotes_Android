package com.example.smartnotes.model;

import android.util.Log;
import com.google.gson.annotations.SerializedName;

/**
 * 登录响应数据模型
 */
public class LoginResponse {
    private static final String TAG = "LoginResponse";
    
    @SerializedName("userId")
    private Long id;            // 用户ID
    
    @SerializedName("phone")
    private String phone;       // 手机号
    
    @SerializedName("token")
    private String token;       // 登录令牌
    
    @SerializedName("username")
    private String nickname;    // 昵称
    
    @SerializedName("email")
    private String email;      // 邮箱
    
    @SerializedName("createTime")
    private Long createTime;    // 创建时间
    
    @SerializedName("updateTime")
    private Long updateTime;    // 更新时间

    public String getUsername() {
        Log.d(TAG, "获取用户名: " + nickname);
        return nickname;
    }

    public Long getId() {
        Log.d(TAG, "获取用户ID: " + id);
        return id;
    }

    public void setId(Long id) {
        Log.d(TAG, "设置用户ID: " + id);
        this.id = id;
    }

    public String getPhone() {
        Log.d(TAG, "获取手机号: " + phone);
        return phone;
    }

    public void setPhone(String phone) {
        Log.d(TAG, "设置手机号: " + phone);
        this.phone = phone;
    }

    public String getToken() {
        Log.d(TAG, "获取Token: " + (token != null ? token.substring(0, Math.min(20, token.length())) + "..." : "null"));
        return token;
    }

    public void setToken(String token) {
        Log.d(TAG, "设置Token: " + (token != null ? token.substring(0, Math.min(20, token.length())) + "..." : "null"));
        this.token = token;
    }

    public String getNickname() {
        Log.d(TAG, "获取昵称: " + nickname);
        return nickname;
    }

    public void setNickname(String nickname) {
        Log.d(TAG, "设置昵称: " + nickname);
        this.nickname = nickname;
    }

    public String getEmail() {
        Log.d(TAG, "获取邮箱: " + email);
        return email;
    }

    public void setEmail(String email) {
        Log.d(TAG, "设置邮箱: " + email);
        this.email = email;
    }

    public Long getCreateTime() {
        Log.d(TAG, "获取创建时间: " + createTime);
        return createTime;
    }

    public void setCreateTime(Long createTime) {
        Log.d(TAG, "设置创建时间: " + createTime);
        this.createTime = createTime;
    }

    public Long getUpdateTime() {
        Log.d(TAG, "获取更新时间: " + updateTime);
        return updateTime;
    }

    public void setUpdateTime(Long updateTime) {
        Log.d(TAG, "设置更新时间: " + updateTime);
        this.updateTime = updateTime;
    }

    @Override
    public String toString() {
        return "LoginResponse{" +
                "id=" + id +
                ", phone='" + phone + '\'' +
                ", token='" + (token != null ? token.substring(0, Math.min(20, token.length())) + "..." : "null") + '\'' +
                ", nickname='" + nickname + '\'' +
                ", email='" + email + '\'' +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                '}';
    }
} 