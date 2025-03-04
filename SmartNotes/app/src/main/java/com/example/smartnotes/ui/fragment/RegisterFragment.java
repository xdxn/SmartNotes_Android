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

import com.example.smartnotes.databinding.FragmentRegisterBinding;
import com.example.smartnotes.model.LoginResponse;
import com.example.smartnotes.network.ApiHelper;
import com.example.smartnotes.ui.activity.MainActivity;
import com.example.smartnotes.utils.SharedPreferencesUtil;

public class RegisterFragment extends Fragment {
    private static final String TAG = "RegisterFragment";
    private FragmentRegisterBinding binding;
    private boolean isWaitingForCode = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentRegisterBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews();
    }

    private void initViews() {
        binding.btnSendCode.setOnClickListener(v -> sendVerificationCode());
        binding.btnRegister.setOnClickListener(v -> attemptRegister());
    }

    private void sendVerificationCode() {
        String phone = binding.etPhone.getText().toString().trim();

        if (TextUtils.isEmpty(phone)) {
            binding.etPhone.setError("请输入手机号码");
            return;
        }

        if (isWaitingForCode) {
            Toast.makeText(requireContext(), "请稍后再试", Toast.LENGTH_SHORT).show();
            return;
        }

        isWaitingForCode = true;
        binding.btnSendCode.setEnabled(false);

        ApiHelper.sendVerificationCode(phone, requireContext(), new ApiHelper.ApiCallback<Void>() {
            @Override
            public void onSuccess(Void result) {
                Toast.makeText(requireContext(), "验证码已发送", Toast.LENGTH_SHORT).show();
                startCountdown();
            }

            @Override
            public void onError(String message) {
                isWaitingForCode = false;
                binding.btnSendCode.setEnabled(true);
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void startCountdown() {
        // TODO: 实现倒计时功能
        binding.btnSendCode.postDelayed(() -> {
            isWaitingForCode = false;
            binding.btnSendCode.setEnabled(true);
        }, 60000);
    }

    private void attemptRegister() {
        String phone = binding.etPhone.getText().toString().trim();
        String code = binding.etVerificationCode.getText().toString().trim();
        String password = binding.etPassword.getText().toString().trim();

        if (TextUtils.isEmpty(phone)) {
            binding.etPhone.setError("请输入手机号码");
            return;
        }

        if (TextUtils.isEmpty(code)) {
            binding.etVerificationCode.setError("请输入验证码");
            return;
        }

        if (TextUtils.isEmpty(password)) {
            binding.etPassword.setError("请输入密码");
            return;
        }

        ApiHelper.register(phone, code, password, requireContext(), new ApiHelper.ApiCallback<LoginResponse>() {
            @Override
            public void onSuccess(LoginResponse result) {
                Log.d(TAG, "注册成功：" + result.toString());
                // 保存用户信息
                SharedPreferencesUtil.saveUserInfo(requireContext(), result);
                // 跳转到主页
                startActivity(new Intent(requireContext(), MainActivity.class));
                requireActivity().finish();
            }

            @Override
            public void onError(String message) {
                Log.e(TAG, "注册失败：" + message);
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