package com.example.smartnotes.network;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.example.smartnotes.config.AppConfig;
import com.example.smartnotes.model.LoginResponse;
import com.example.smartnotes.model.request.LoginRequest;
import com.example.smartnotes.model.request.RegisterRequest;
import com.example.smartnotes.model.response.BaseResponse;
import com.example.smartnotes.ui.activity.LoginActivity;
import com.example.smartnotes.utils.UserManager;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Response;
import org.json.JSONObject;

/**
 * API服务类
 * 封装所有与后端的接口调用
 */
public class ApiService {
    private static final Gson gson = new Gson();
    private static final String TAG = "ApiService";

    public interface ApiCallback<T> {
        void onSuccess(T result);
        void onError(String message);
    }

    /**
     * 发送验证码
     * @param phone 手机号
     * @param context 上下文
     * @param callback 回调接口
     */
    public static void sendVerificationCode(String phone, Context context, ApiCallback<Void> callback) {
        try {
            // 使用表单格式发送请求
            HttpUtil.postForm(AppConfig.API_SEND_CODE, "phone", phone, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    handleError(e.getMessage(), context, callback);
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    handleBaseResponse(response, context, callback, Void.class);
                }
            });
        } catch (Exception e) {
            handleError(e.getMessage(), context, callback);
        }
    }

    /**
     * 用户注册
     */
    public static void register(String phone, String code, String password, Context context, ApiCallback<LoginResponse> callback) {
        try {
            // 使用表单格式发送请求
            FormBody formBody = new FormBody.Builder()
                    .add("phone", phone)
                    .add("code", code)
                    .add("password", password)
                    .build();

            // 添加日志
            Log.d(TAG, "注册请求 - 手机号: " + phone + ", 验证码: " + code);
            
            HttpUtil.post(AppConfig.API_REGISTER, formBody, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Log.e(TAG, "注册请求失败: " + e.getMessage());
                    handleError("网络连接失败，请检查网络设置", context, callback);
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (response.isSuccessful()) {
                        handleBaseResponse(response, context, callback, LoginResponse.class);
                    } else {
                        Log.e(TAG, "注册失败: " + response.code() + " " + response.message());
                        handleError("注册失败，请稍后重试", context, callback);
                    }
                }
            });
        } catch (Exception e) {
            Log.e(TAG, "注册异常: " + e.getMessage());
            handleError("注册失败，请稍后重试", context, callback);
        }
    }

    /**
     * 用户登录
     */
    public static void login(String phone, String password, Context context, ApiCallback<LoginResponse> callback) {
        try {
            // 添加日志输出，检查参数
            Log.d(TAG, "开始登录请求 - 手机号: " + phone + ", 密码长度: " + password.length());

            // 使用表单格式发送请求
            FormBody formBody = new FormBody.Builder()
                    .add("phone", phone)
                    .add("password", password)
                    .build();

            HttpUtil.post(AppConfig.API_LOGIN, formBody, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Log.e(TAG, "登录请求失败: " + e.getMessage());
                    handleError("网络连接失败，请检查网络设置", context, callback);
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    try {
                        String responseBody = response.body().string();
                        Log.d(TAG, "登录响应: " + responseBody);
                        
                        Type type = TypeToken.getParameterized(BaseResponse.class, LoginResponse.class).getType();
                        BaseResponse<LoginResponse> baseResponse = gson.fromJson(responseBody, type);

                        if (baseResponse.getCode() == 0) {
                            callback.onSuccess(baseResponse.getData());
                        } else {
                            // 根据不同的错误码显示不同的提示
                            String errorMsg;
                            switch (baseResponse.getCode()) {
                                case 1001:
                                    errorMsg = "账号不存在";
                                    break;
                                case 1002:
                                    errorMsg = "密码错误";
                                    break;
                                case 1003:
                                    errorMsg = "账号已被禁用";
                                    break;
                                default:
                                    errorMsg = baseResponse.getMessage();
                                    break;
                            }
                            handleError(errorMsg, context, callback);
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "解析登录响应失败: " + e.getMessage());
                        handleError("服务器响应解析失败", context, callback);
                    } finally {
                        response.close(); // 确保响应被关闭
                    }
                }
            });
        } catch (Exception e) {
            Log.e(TAG, "登录过程异常: " + e.getMessage());
            handleError("登录失败，请稍后重试", context, callback);
        }
    }

    private static <T> void handleBaseResponse(Response response, Context context, ApiCallback<T> callback, Class<T> classOfT) {
        try {
            // 先读取响应体
            String responseBody = response.body().string();
            Log.d(TAG, "响应体: " + responseBody);
            
            try {
                Type type = TypeToken.getParameterized(BaseResponse.class, classOfT).getType();
                BaseResponse<T> baseResponse = gson.fromJson(responseBody, type);

                if (baseResponse.getCode() == 0) {
                    callback.onSuccess(baseResponse.getData());
                } else if (baseResponse.getCode() == 401) { // Token过期
                    // 清除本地登录信息
                    UserManager.logout(context);
                    // 跳转到登录页面
                    Intent intent = new Intent(context, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    context.startActivity(intent);
                } else {
                    handleError(baseResponse.getMessage(), context, callback);
                }
            } catch (Exception e) {
                Log.e(TAG, "JSON解析失败: " + e.getMessage());
                handleError("服务器响应解析失败", context, callback);
            }
        } catch (IOException e) {
            Log.e(TAG, "读取响应体失败: " + e.getMessage());
            handleError("网络请求失败", context, callback);
        } finally {
            response.close();
        }
    }

    private static void handleError(String message, Context context, ApiCallback<?> callback) {
        if (context != null) {
            ((android.app.Activity) context).runOnUiThread(() -> {
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                callback.onError(message);
            });
        }
    }
} 