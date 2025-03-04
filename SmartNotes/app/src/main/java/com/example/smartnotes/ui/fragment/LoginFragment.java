package com.example.smartnotes.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;


import com.example.smartnotes.databinding.FragmentLoginBinding;
import com.example.smartnotes.model.LoginResponse;
import com.example.smartnotes.network.ApiHelper;
import com.example.smartnotes.ui.activity.MainActivity;
import com.example.smartnotes.utils.UserManager;


public class LoginFragment extends Fragment {
    private static final String TAG = "LoginFragment";
    private FragmentLoginBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentLoginBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews();
    }

    private void initViews() {
        binding.btnLogin.setOnClickListener(v -> attemptLogin());
    }

    private void attemptLogin() {
        String phone = binding.etPhone.getText().toString().trim();
        String password = binding.etPassword.getText().toString().trim();

        // 输入验证
        if (TextUtils.isEmpty(phone)) {
            binding.etPhone.setError("请输入手机号码");
            return;
        }

        if (TextUtils.isEmpty(password)) {
            binding.etPassword.setError("请输入密码");
            return;
        }

        // 执行登录
        ApiHelper.login(phone, password, requireContext(), new ApiHelper.ApiCallback<LoginResponse>() {
            @Override
            public void onSuccess(LoginResponse result) {
                Log.d(TAG, "登录成功：" + result.toString());
                // 保存用户信息
                UserManager.saveLoginInfo(requireContext(), result);
                // 跳转到主页
                startActivity(new Intent(requireContext(), MainActivity.class));
                requireActivity().finish();
            }

            @Override
            public void onError(String message) {
                Log.e(TAG, "登录失败：" + message);
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
} 