package com.example.smartnotes.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.smartnotes.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import com.example.smartnotes.network.ApiService;
import com.example.smartnotes.model.LoginResponse;
import com.example.smartnotes.utils.UserManager;
import com.example.smartnotes.ui.activity.MainActivity;

public class RegisterFragment extends Fragment {
    private TextInputEditText etPhone;
    private TextInputEditText etVerifyCode;
    private TextInputEditText etPassword;
    private MaterialButton btnSendCode;
    private MaterialButton btnRegister;
    private CountDownTimer countDownTimer;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_register, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        etPhone = view.findViewById(R.id.etPhone);
        etVerifyCode = view.findViewById(R.id.etVerifyCode);
        etPassword = view.findViewById(R.id.etPassword);
        btnSendCode = view.findViewById(R.id.btnSendCode);
        btnRegister = view.findViewById(R.id.btnRegister);

        btnSendCode.setOnClickListener(v -> sendVerificationCode());
        btnRegister.setOnClickListener(v -> register());
    }

    private void sendVerificationCode() {
        String phone = etPhone.getText().toString().trim();
        if (phone.isEmpty()) {
            Toast.makeText(getContext(), "请输入手机号", Toast.LENGTH_SHORT).show();
            return;
        }

        btnSendCode.setEnabled(false);
        btnSendCode.setText("发送中...");

        ApiService.sendVerificationCode(phone, getContext(), new ApiService.ApiCallback<Void>() {
            @Override
            public void onSuccess(Void result) {
                requireActivity().runOnUiThread(() -> {
                    Toast.makeText(getContext(), "验证码已发送", Toast.LENGTH_SHORT).show();
                    startCountDown();
                });
            }

            @Override
            public void onError(String message) {
                requireActivity().runOnUiThread(() -> {
                    btnSendCode.setEnabled(true);
                    btnSendCode.setText("发送验证码");
                });
            }
        });
    }

    private void startCountDown() {
        btnSendCode.setEnabled(false);
        countDownTimer = new CountDownTimer(60000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                btnSendCode.setText(millisUntilFinished / 1000 + "秒后重试");
            }

            @Override
            public void onFinish() {
                btnSendCode.setEnabled(true);
                btnSendCode.setText("发送验证码");
            }
        }.start();
    }

    private void register() {
        String phone = etPhone.getText().toString().trim();
        String verifyCode = etVerifyCode.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (phone.isEmpty() || verifyCode.isEmpty() || password.isEmpty()) {
            Toast.makeText(getContext(), "请填写完整信息", Toast.LENGTH_SHORT).show();
            return;
        }

        btnRegister.setEnabled(false);
        btnRegister.setText("注册中...");

        ApiService.register(phone, verifyCode, password, getContext(), new ApiService.ApiCallback<LoginResponse>() {
            @Override
            public void onSuccess(LoginResponse result) {
                // 保存登录信息
                UserManager.saveLoginInfo(requireContext(), result);
                // 跳转到主页
                startMainActivity();
            }

            @Override
            public void onError(String message) {
                requireActivity().runOnUiThread(() -> {
                    btnRegister.setEnabled(true);
                    btnRegister.setText("注册");
                });
            }
        });
    }

    private void startMainActivity() {
        Intent intent = new Intent(requireContext(), MainActivity.class);
        // 清除任务栈中的其他Activity
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }
} 