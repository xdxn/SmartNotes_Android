package com.example.smartnotes.network;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.example.smartnotes.model.LoginResponse;
import com.example.smartnotes.network.response.BaseResponse;
import com.example.smartnotes.network.response.NoteResponse;

import java.io.EOFException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ApiHelper {
    private static final String TAG = "ApiHelper";

    public interface ApiCallback<T> {
        void onSuccess(T result);
        void onError(String message);
    }

    private static ApiService getApiService() {
        return RetrofitClient.getInstance().getApiService();
    }

    /**
     * 发送验证码
     */
    public static void sendVerificationCode(String phone, Context context, ApiCallback<Void> callback) {
        RetrofitClient.getInstance().getApiService().sendVerificationCode(phone)
                .enqueue(new Callback<BaseResponse<Void>>() {
                    @Override
                    public void onResponse(Call<BaseResponse<Void>> call, Response<BaseResponse<Void>> response) {
                        handleResponse(response, context, callback);
                    }

                    @Override
                    public void onFailure(Call<BaseResponse<Void>> call, Throwable t) {
                        handleError(t.getMessage(), context, callback);
                    }
                });
    }

    /**
     * 用户注册
     */
    public static void register(String phone, String code, String password, Context context, ApiCallback<LoginResponse> callback) {
        getApiService().register(phone, code, password)
                .enqueue(new Callback<BaseResponse<LoginResponse>>() {
                    @Override
                    public void onResponse(Call<BaseResponse<LoginResponse>> call, Response<BaseResponse<LoginResponse>> response) {
                        handleResponse(response, context, callback);
                    }

                    @Override
                    public void onFailure(Call<BaseResponse<LoginResponse>> call, Throwable t) {
                        handleError(t.getMessage(), context, callback);
                    }
                });
    }

    /**
     * 用户登录
     */
    public static void login(String phone, String password, Context context, ApiCallback<LoginResponse> callback) {
        getApiService().login(phone, password)
                .enqueue(new Callback<BaseResponse<LoginResponse>>() {
                    @Override
                    public void onResponse(Call<BaseResponse<LoginResponse>> call, Response<BaseResponse<LoginResponse>> response) {
                        handleResponse(response, context, callback);
                    }

                    @Override
                    public void onFailure(Call<BaseResponse<LoginResponse>> call, Throwable t) {
                        handleError(t.getMessage(), context, callback);
                    }
                });
    }

    /**
     * 获取用户的笔记列表
     */
    public static void getNotes(Long userId, Context context, final ApiCallback<List<NoteResponse>> callback) {
        Log.d(TAG, "getNotes: 开始获取用户笔记列表，用户ID: " + userId);
        
        getApiService().getNotes(userId).enqueue(new Callback<List<NoteResponse>>() {
            @Override
            public void onResponse(Call<List<NoteResponse>> call, Response<List<NoteResponse>> response) {
                Log.d(TAG, "getNotes onResponse: " + response.code());
                
                if (response.isSuccessful() && response.body() != null) {
                    List<NoteResponse> notes = response.body();
                    Log.d(TAG, "getNotes: 成功获取笔记列表，数量: " + (notes != null ? notes.size() : 0));
                    callback.onSuccess(notes);
                } else {
                    String errorMsg = "获取笔记列表失败，服务器响应错误: " + response.code();
                    Log.e(TAG, "getNotes: " + errorMsg);
                    callback.onError(errorMsg);
                }
            }

            @Override
            public void onFailure(Call<List<NoteResponse>> call, Throwable t) {
                Log.e(TAG, "getNotes onFailure: " + t.getMessage());
                callback.onError("网络请求失败: " + t.getMessage());
            }
        });
    }

    /**
     * 创建笔记
     */
    public static void createNote(NoteResponse note, Context context, ApiCallback<NoteResponse> callback) {
        Log.d(TAG, "开始创建笔记: " + note.toString());
        Log.d(TAG, "请求参数 - userId: " + note.getUserId());
        Log.d(TAG, "请求参数 - username: " + note.getUsername());
        Log.d(TAG, "请求参数 - title: " + note.getTitle());
        Log.d(TAG, "请求参数 - content: " + note.getContent());
        
        getApiService().createNote(note)
            .enqueue(new Callback<NoteResponse>() {
                @Override
                public void onResponse(Call<NoteResponse> call, Response<NoteResponse> response) {
                    Log.d(TAG, "创建笔记响应码: " + response.code());
                    if (!response.isSuccessful()) {
                        try {
                            String errorBody = response.errorBody().string();
                            Log.e(TAG, "创建笔记失败，错误信息: " + errorBody);
                            handleError("创建笔记失败: " + errorBody, context, callback);
                        } catch (Exception e) {
                            Log.e(TAG, "读取错误信息失败", e);
                            handleError("创建笔记失败，请稍后重试", context, callback);
                        }
                        return;
                    }
                    
                    NoteResponse result = response.body();
                    if (result != null) {
                        Log.d(TAG, "创建笔记成功: " + result.toString());
                        callback.onSuccess(result);
                    } else {
                        handleError("创建笔记失败: 返回数据为空", context, callback);
                    }
                }

                @Override
                public void onFailure(Call<NoteResponse> call, Throwable t) {
                    Log.e(TAG, "创建笔记网络请求失败", t);
                    handleError(t.getMessage(), context, callback);
                }
            });
    }

    /**
     * 更新笔记
     */
    public static void updateNote(Long id, NoteResponse note, Context context, ApiCallback<NoteResponse> callback) {
        Log.d(TAG, "开始更新笔记，ID: " + id + ", 内容: " + note.toString());
        RetrofitClient.getInstance()
            .getApiService()
            .updateNote(id, note)
            .enqueue(new Callback<NoteResponse>() {
                @Override
                public void onResponse(Call<NoteResponse> call, Response<NoteResponse> response) {
                    Log.d(TAG, "更新笔记响应码: " + response.code());
                    if (!response.isSuccessful()) {
                        try {
                            String errorBody = response.errorBody().string();
                            Log.e(TAG, "更新笔记失败，错误信息: " + errorBody);
                            handleError("更新笔记失败: " + errorBody, context, callback);
                        } catch (Exception e) {
                            Log.e(TAG, "读取错误信息失败", e);
                            handleError("更新笔记失败，请稍后重试", context, callback);
                        }
                        return;
                    }
                    
                    NoteResponse result = response.body();
                    if (result != null) {
                        Log.d(TAG, "更新笔记成功: " + result.toString());
                        callback.onSuccess(result);
                    } else {
                        handleError("更新笔记失败: 返回数据为空", context, callback);
                    }
                }

                @Override
                public void onFailure(Call<NoteResponse> call, Throwable t) {
                    Log.e(TAG, "更新笔记网络请求失败", t);
                    handleError("更新笔记失败: " + t.getMessage(), context, callback);
                }
            });
    }

    /**
     * 删除笔记
     */
    public static void deleteNote(Long id, Long userId, Context context, ApiCallback<Void> callback) {
        Log.d(TAG, "开始删除笔记，ID：" + id + ", 用户ID: " + userId);
        
        getApiService().deleteNote(id, userId)
            .enqueue(new Callback<BaseResponse<Void>>() {
                @Override
                public void onResponse(Call<BaseResponse<Void>> call, Response<BaseResponse<Void>> response) {
                    Log.d(TAG, "删除笔记响应码: " + response.code());
                    
                    if (response.code() == 200 || response.code() == 204) {
                        // 200或204都表示删除成功
                        Log.d(TAG, "删除笔记成功");
                        callback.onSuccess(null);
                        return;
                    }
                    
                    try {
                        String errorBody = response.errorBody() != null ? response.errorBody().string() : "未知错误";
                        Log.e(TAG, "删除笔记失败，错误信息: " + errorBody);
                        callback.onError("删除失败: " + errorBody);
                    } catch (Exception e) {
                        Log.e(TAG, "读取错误信息失败", e);
                        callback.onError("删除失败，请稍后重试");
                    }
                }

                @Override
                public void onFailure(Call<BaseResponse<Void>> call, Throwable t) {
                    Log.e(TAG, "删除笔记网络请求失败", t);
                    if (t instanceof EOFException) {
                        // 处理空响应的情况
                        Log.d(TAG, "服务器返回空响应，可能删除成功");
                        callback.onSuccess(null);
                    } else {
                        callback.onError("删除失败: " + t.getMessage());
                    }
                }
            });
    }

    private static <T> void handleResponse(Response<BaseResponse<T>> response, Context context, ApiCallback<T> callback) {
        if (response.isSuccessful() && response.body() != null) {
            BaseResponse<T> baseResponse = response.body();
            if (baseResponse.getCode() == 200) {
                callback.onSuccess(baseResponse.getData());
            } else {
                handleError(baseResponse.getMessage(), context, callback);
            }
        } else {
            handleError("请求失败，请检查网络连接", context, callback);
        }
    }

    private static void handleError(String message, Context context, ApiCallback<?> callback) {
        if (context != null) {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
        }
        if (callback != null) {
            callback.onError(message);
        }
    }
} 