package com.example.smartnotes.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.smartnotes.R;
import com.example.smartnotes.utils.LoadingDialog;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import com.example.smartnotes.network.ApiService;
import com.example.smartnotes.model.LoginResponse;
import com.example.smartnotes.utils.UserManager;
import com.example.smartnotes.ui.activity.MainActivity;

import com.example.smartnotes.utils.ValidationUtil;

public class LoginFragment extends Fragment {
    private TextInputEditText etPhone;
    private TextInputEditText etPassword;
    private MaterialButton btnLogin;
    private LoadingDialog loadingDialog;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        etPhone = view.findViewById(R.id.etPhone);
        etPassword = view.findViewById(R.id.etPassword);
        btnLogin = view.findViewById(R.id.btnLogin);
        loadingDialog = new LoadingDialog(requireContext());

        btnLogin.setOnClickListener(v -> login());
    }

    private void login() {
        String phone = etPhone.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        // 添加输入验证
        if (phone.isEmpty()) {
            Toast.makeText(getContext(), "请输入手机号", Toast.LENGTH_SHORT).show();
            return;
        }
        if (password.isEmpty()) {
            Toast.makeText(getContext(), "请输入密码", Toast.LENGTH_SHORT).show();
            return;
        }
        
        // 验证手机号格式
        if (!ValidationUtil.isValidPhone(phone)) {
            Toast.makeText(getContext(), "请输入正确的手机号", Toast.LENGTH_SHORT).show();
            return;
        }

        // 显示加载对话框
        loadingDialog.setMessage("登录中...");
        loadingDialog.show();
        
        // 禁用登录按钮
        btnLogin.setEnabled(false);

        ApiService.login(phone, password, getContext(), new ApiService.ApiCallback<LoginResponse>() {
            @Override
            public void onSuccess(LoginResponse result) {
                requireActivity().runOnUiThread(() -> {
                    // 隐藏加载对话框
                    loadingDialog.dismiss();
                    // 显示成功提示
                    Toast.makeText(getContext(), "登录成功", Toast.LENGTH_SHORT).show();
                    // 保存登录信息
                    UserManager.saveLoginInfo(requireContext(), result);
                    // 跳转到主页
                    startMainActivity();
                });
            }

            @Override
            public void onError(String message) {
                requireActivity().runOnUiThread(() -> {
                    // 隐藏加载对话框
                    loadingDialog.dismiss();
                    // 显示错误信息
                    Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
                    // 恢复登录按钮
                    btnLogin.setEnabled(true);
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
        if (loadingDialog != null && loadingDialog.isShowing()) {
            loadingDialog.dismiss();
        }
    }
} 