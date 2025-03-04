package com.example.smartnotes.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.example.smartnotes.R;
import com.example.smartnotes.databinding.ActivityLoginBinding;
import com.example.smartnotes.model.LoginResponse;
import com.example.smartnotes.network.ApiHelper;
import com.example.smartnotes.ui.fragment.LoginFragment;
import com.example.smartnotes.ui.fragment.RegisterFragment;

import com.example.smartnotes.utils.UserManager;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    private ActivityLoginBinding binding;
    private TabLayout tabLayout;
    private ViewPager2 viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // 检查是否已登录
        if (UserManager.getToken(this) != null) {
            // 已登录，直接跳转到主页
            startActivity(new Intent(this, MainActivity.class));
            finish();
            return;
        }

        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initViews();
    }

    private void initViews() {
        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.viewPager);

        viewPager.setAdapter(new FragmentStateAdapter(this) {
            @NonNull
            @Override
            public Fragment createFragment(int position) {
                return position == 0 ? new LoginFragment() : new RegisterFragment();
            }

            @Override
            public int getItemCount() {
                return 2;
            }
        });

        new TabLayoutMediator(tabLayout, viewPager,
                (tab, position) -> tab.setText(position == 0 ? "登录" : "注册")
        ).attach();
    }

//    private void attemptLogin() {
//        String phone = binding.phoneEditText.getText().toString().trim();
//        String password = binding.passwordEditText.getText().toString().trim();
//
//        // 输入验证
//        if (TextUtils.isEmpty(phone)) {
//            binding.phoneInputLayout.setError("请输入手机号码");
//            return;
//        }
//        binding.phoneInputLayout.setError(null);
//
//        if (TextUtils.isEmpty(password)) {
//            binding.passwordInputLayout.setError("请输入密码");
//            return;
//        }
//        binding.passwordInputLayout.setError(null);
//
//        // 执行登录
//        ApiHelper.login(phone, password, this, new ApiHelper.ApiCallback<LoginResponse>() {
//            @Override
//            public void onSuccess(LoginResponse result) {
//                Log.d(TAG, "登录成功：" + result.toString());
//                // 保存用户信息
//                SharedPreferencesUtil.saveUserInfo(LoginActivity.this, result);
//                // 跳转到主页
//                startActivity(MainActivity.createIntent(LoginActivity.this));
//                finish();
//            }
//
//            @Override
//            public void onError(String message) {
//                Log.e(TAG, "登录失败：" + message);
//                Toast.makeText(LoginActivity.this, message, Toast.LENGTH_SHORT).show();
//            }
//        });
//    }
}