package com.example.smartnotes.utils;

import java.util.regex.Pattern;

public class ValidationUtil {
    public static boolean isValidPhone(String phone) {
        return Pattern.matches("^1[3-9]\\d{9}$", phone);
    }

    public static boolean isValidPassword(String password) {
        // 密码至少6位，包含字母和数字
        return password.length() >= 6 && 
               password.matches(".*[A-Za-z].*") && 
               password.matches(".*[0-9].*");
    }
} 