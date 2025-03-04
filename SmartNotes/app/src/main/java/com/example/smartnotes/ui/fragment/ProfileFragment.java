package com.example.smartnotes.ui.fragment;

import androidx.fragment.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.example.smartnotes.R;
import com.example.smartnotes.ui.activity.LoginActivity;
import com.example.smartnotes.utils.UserManager;

import android.widget.Button;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.button.MaterialButton;

public class ProfileFragment extends Fragment {
    private MaterialButton btnLogout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        // 初始化退出登录按钮
        btnLogout = view.findViewById(R.id.btnLogout);
        
        // 设置退出登录按钮点击事件
        btnLogout.setOnClickListener(v -> logout());

        return view;
    }

    private void logout() {
        // 清除用户信息并跳转到登录页面
        UserManager.logout(requireContext());
        
        // 确保当前Activity被销毁
        requireActivity().finish();
    }
} 