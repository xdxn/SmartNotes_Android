package com.example.smartnotes.model;

/**
 * 登录/注册成功后的响应数据模型
 */
public class LoginResponse {
    private String token;       // 用户令牌
    private UserInfo userInfo;  // 用户信息

    public String getToken() {
        return token;
    }

    public UserInfo getUserInfo() {
        return userInfo;
    }

    public static class UserInfo {
        private String userId;    // 用户ID
        private String phone;     // 手机号
        private String nickname;  // 昵称

        public String getUserId() {
            return userId;
        }

        public String getPhone() {
            return phone;
        }

        public String getNickname() {
            return nickname;
        }
    }
} 