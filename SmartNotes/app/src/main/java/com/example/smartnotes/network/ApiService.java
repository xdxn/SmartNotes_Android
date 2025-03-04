package com.example.smartnotes.network;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

import com.example.smartnotes.model.LoginResponse;
import com.example.smartnotes.network.response.BaseResponse;
import com.example.smartnotes.network.response.NoteResponse;

import java.util.List;

/**
 * API服务接口
 * 封装所有与后端的接口调用
 */
public interface ApiService {
    // 用户相关接口
    @FormUrlEncoded
    @POST("/api/user/code")
    Call<BaseResponse<Void>> sendVerificationCode(@Field("phone") String phone);

    @FormUrlEncoded
    @POST("/api/user/register")
    Call<BaseResponse<LoginResponse>> register(
        @Field("phone") String phone,
        @Field("code") String code,
        @Field("password") String password
    );

    @FormUrlEncoded
    @POST("/api/user/login")
    Call<BaseResponse<LoginResponse>> login(
        @Field("phone") String phone,
        @Field("password") String password
    );

    // 笔记相关接口
    @GET("/api/notes/user/{userId}")
    Call<List<NoteResponse>> getNotes(
        @Path("userId") Long userId
    );

    @GET("/api/notes/{id}")
    Call<BaseResponse<NoteResponse>> getNoteById(@Path("id") Long id);

    @POST("/api/notes")
    Call<NoteResponse> createNote(@Body NoteResponse note);

    @PUT("/api/notes/{id}")
    Call<NoteResponse> updateNote(
        @Path("id") Long id,
        @Body NoteResponse note
    );

    @DELETE("/api/notes/{id}")
    Call<BaseResponse<Void>> deleteNote(
        @Path("id") Long id,
        @Query("userId") Long userId
    );
}
