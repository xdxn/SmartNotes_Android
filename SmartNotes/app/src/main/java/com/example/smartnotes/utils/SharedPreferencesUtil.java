package com.example.smartnotes.utils;

import android.content.Context;
import android.content.SharedPreferences;
import com.example.smartnotes.model.LoginResponse;

public class SharedPreferencesUtil {
    private static final String PREF_NAME = "SmartNotes";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_TOKEN = "token";

    public static void saveUserInfo(Context context, LoginResponse loginResponse) {
        SharedPreferences preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        
        editor.putLong(KEY_USER_ID, loginResponse.getId());
        editor.putString(KEY_USERNAME, loginResponse.getNickname());
        editor.putString(KEY_TOKEN, loginResponse.getToken());
        
        editor.apply();
    }

    public static LoginResponse getUserInfo(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        
        Long userId = preferences.getLong(KEY_USER_ID, -1);
        String username = preferences.getString(KEY_USERNAME, null);
        String token = preferences.getString(KEY_TOKEN, null);
        
        if (userId == -1 || username == null || token == null) {
            return null;
        }
        
        LoginResponse loginResponse = new LoginResponse();
        loginResponse.setId(userId);
        loginResponse.setNickname(username);
        loginResponse.setToken(token);
        
        return loginResponse;
    }

    public static void clearUserInfo(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.apply();
    }

    public static boolean isLoggedIn(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return preferences.contains(KEY_TOKEN);
    }
} 