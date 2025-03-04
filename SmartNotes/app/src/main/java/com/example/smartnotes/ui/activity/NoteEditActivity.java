package com.example.smartnotes.ui.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.smartnotes.R;
import com.example.smartnotes.model.Note;
import com.example.smartnotes.model.LoginResponse;
import com.example.smartnotes.network.ApiHelper;
import com.example.smartnotes.network.response.NoteResponse;
import com.example.smartnotes.utils.UserManager;
import jp.wasabeef.richeditor.RichEditor;
import com.example.smartnotes.SmartNotesApplication;
import com.iflytek.sparkchain.core.rtasr.RTASR;
import com.iflytek.sparkchain.core.rtasr.RTASRCallbacks;

import java.util.Date;

public class NoteEditActivity extends AppCompatActivity {
    private static final String TAG = "NoteEditActivity";
    private EditText etTitle;
    private RichEditor editor;
    private Note note;
    private boolean isContentModified = false;
    private RTASR mRTASR;
    private boolean isTranscribing = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_edit);

        Log.d(TAG, "onCreate: 开始创建笔记编辑页面");

        // 设置Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("编辑笔记");

        // 初始化视图
        initViews();
        
        // 获取传入的笔记数据
        note = (Note) getIntent().getSerializableExtra("note");
        if (note != null) {
            Log.d(TAG, "onCreate: 加载现有笔记，ID: " + note.getId());
            etTitle.setText(note.getTitle());
            editor.setHtml(note.getContent());
        } else {
            Log.d(TAG, "onCreate: 创建新笔记");
        }

        // 初始化语音转写
        mRTASR = SmartNotesApplication.getRTASR();
        if (mRTASR == null) {
            Log.e(TAG, "语音转写初始化失败");
        }
    }

    private void initViews() {
        etTitle = findViewById(R.id.etTitle);
        editor = findViewById(R.id.editor);
        
        // 配置编辑器
        editor.setEditorHeight(200);
        editor.setEditorFontSize(18);
        editor.setPadding(10, 10, 10, 10);
        editor.setPlaceholder("请输入内容...");

        // 编辑器工具栏
        findViewById(R.id.action_bold).setOnClickListener(v -> editor.setBold());
        findViewById(R.id.action_italic).setOnClickListener(v -> editor.setItalic());
        findViewById(R.id.action_underline).setOnClickListener(v -> editor.setUnderline());
        findViewById(R.id.action_strikethrough).setOnClickListener(v -> editor.setStrikeThrough());
        findViewById(R.id.action_bullet).setOnClickListener(v -> editor.setBullets());
        findViewById(R.id.action_quote).setOnClickListener(v -> editor.setBlockquote());
        findViewById(R.id.action_undo).setOnClickListener(v -> editor.undo());
        findViewById(R.id.action_redo).setOnClickListener(v -> editor.redo());

        // 添加语音转写按钮
//        findViewById(R.id.action_voice).setOnClickListener(v -> {
//            if (mRTASR == null) {
//                Toast.makeText(this, "语音转写功能未初始化", Toast.LENGTH_SHORT).show();
//                return;
//            }
//
//            if (!isTranscribing) {
//                startTranscribe();
//            } else {
//                stopTranscribe();
//            }
//        });

        // 监听内容变化
        editor.setOnTextChangeListener(text -> isContentModified = true);
        etTitle.setOnKeyListener((v, keyCode, event) -> {
            isContentModified = true;
            return false;
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_note_edit, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        } else if (id == R.id.action_save) {
            saveNote();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (isContentModified) {
            showSaveDialog();
        } else {
            super.onBackPressed();
        }
    }

    private void showSaveDialog() {
        new AlertDialog.Builder(this)
                .setTitle("保存提醒")
                .setMessage("是否保存当前修改？")
                .setPositiveButton("保存", (dialog, which) -> {
                    saveNote();
                })
                .setNegativeButton("不保存", (dialog, which) -> {
                    finish();
                })
                .setNeutralButton("取消", null)
                .show();
    }

    private void saveNote() {
        String title = etTitle.getText().toString().trim();
        String content = editor.getHtml();

        Log.d(TAG, "saveNote: 开始保存笔记");
        Log.d(TAG, "saveNote: 标题长度: " + title.length());
        Log.d(TAG, "saveNote: 内容长度: " + (content != null ? content.length() : 0));

        if (title.isEmpty()) {
            Toast.makeText(this, "请输入标题", Toast.LENGTH_SHORT).show();
            Log.w(TAG, "saveNote: 标题为空");
            return;
        }

        if (content == null || content.isEmpty()) {
            Toast.makeText(this, "请输入内容", Toast.LENGTH_SHORT).show();
            Log.w(TAG, "saveNote: 内容为空");
            return;
        }

        // 获取当前用户ID
        LoginResponse currentUser = UserManager.getInstance().getCurrentUser();
        if (currentUser == null || currentUser.getId() == null) {
            Toast.makeText(this, "用户未登录，请先登录", Toast.LENGTH_SHORT).show();
            Log.e(TAG, "saveNote: 用户未登录或用户ID为空");
            finish();
            return;
        }

        // 准备笔记数据
        NoteResponse noteResponse = new NoteResponse();
        if (note != null) {
            noteResponse.setId(note.getId());
        }
        noteResponse.setTitle(title);
        noteResponse.setContent(content);
        noteResponse.setUserId(currentUser.getId());
        
        // 设置用户名，如果为空则使用默认值
        String username = currentUser.getUsername();
        noteResponse.setUsername(username != null ? username : "用户" + currentUser.getId());
        
        // 记录请求数据
        Log.d(TAG, "saveNote: 准备发送的笔记数据: " + noteResponse.toString());

        // 显示加载对话框
        AlertDialog loadingDialog = new AlertDialog.Builder(this)
            .setMessage("正在保存...")
            .setCancelable(false)
            .show();

        // 调用API保存笔记
        if (note == null || note.getId() == null) {
            // 创建新笔记
            Log.d(TAG, "saveNote: 创建新笔记，用户ID: " + currentUser.getId());
            ApiHelper.createNote(noteResponse, this, new ApiHelper.ApiCallback<NoteResponse>() {
                @Override
                public void onSuccess(NoteResponse result) {
                    Log.d(TAG, "saveNote: 新笔记创建成功，ID: " + result.getId());
                    loadingDialog.dismiss();
                    Toast.makeText(NoteEditActivity.this, "保存成功", Toast.LENGTH_SHORT).show();
                    
                    // 转换为Note对象
                    note = new Note();
                    note.setId(result.getId());
                    note.setTitle(result.getTitle());
                    note.setContent(result.getContent());
                    note.setUserId(result.getUserId());
                    note.setCreateTime(result.getCreatedAt());
                    note.setUpdateTime(result.getUpdatedAt());

                    // 返回结果
                    Intent intent = new Intent();
                    intent.putExtra("note", note);
                    setResult(RESULT_OK, intent);
                    finish();
                }

                @Override
                public void onError(String message) {
                    Log.e(TAG, "saveNote: 创建笔记失败: " + message);
                    loadingDialog.dismiss();
                    Toast.makeText(NoteEditActivity.this, "保存失败: " + message, Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            // 更新现有笔记
            Log.d(TAG, "saveNote: 更新笔记，笔记ID: " + note.getId());
            ApiHelper.updateNote(note.getId(), noteResponse, this, new ApiHelper.ApiCallback<NoteResponse>() {
                @Override
                public void onSuccess(NoteResponse result) {
                    Log.d(TAG, "saveNote: 笔记更新成功");
                    loadingDialog.dismiss();
                    Toast.makeText(NoteEditActivity.this, "保存成功", Toast.LENGTH_SHORT).show();
                    
                    // 更新Note对象
                    note.setTitle(result.getTitle());
                    note.setContent(result.getContent());
                    note.setUpdateTime(result.getUpdatedAt());

                    // 返回结果
                    Intent intent = new Intent();
                    intent.putExtra("note", note);
                    setResult(RESULT_OK, intent);
                    finish();
                }

                @Override
                public void onError(String message) {
                    Log.e(TAG, "saveNote: 更新笔记失败: " + message);
                    loadingDialog.dismiss();
                    Toast.makeText(NoteEditActivity.this, "保存失败: " + message, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

//    private void startTranscribe() {
//        if (mRTASR == null) return;
//
//        try {
//            mRTASR.setRtasrListener(new RTASRListener() {
//                @Override
//                public void onStart() {
//                    runOnUiThread(() -> {
//                        isTranscribing = true;
//                        Toast.makeText(NoteEditActivity.this, "开始录音", Toast.LENGTH_SHORT).show();
//                        findViewById(R.id.action_voice).setSelected(true);
//                    });
//                }
//
//                @Override
//                public void onStop() {
//                    runOnUiThread(() -> {
//                        isTranscribing = false;
//                        Toast.makeText(NoteEditActivity.this, "停止录音", Toast.LENGTH_SHORT).show();
//                        findViewById(R.id.action_voice).setSelected(false);
//                    });
//                }
//
//                @Override
//                public void onResult(String result, boolean isLast) {
//                    runOnUiThread(() -> {
//                        if (result != null && !result.isEmpty()) {
//                            editor.insertText(result);
//                        }
//                    });
//                }
//
//                @Override
//                public void onError(int errorCode, String errorMessage) {
//                    runOnUiThread(() -> {
//                        isTranscribing = false;
//                        Toast.makeText(NoteEditActivity.this,
//                            "语音转写错误: " + errorMessage, Toast.LENGTH_SHORT).show();
//                        findViewById(R.id.action_voice).setSelected(false);
//                    });
//                }
//            });
//            mRTASR.start();
//        } catch (Exception e) {
//            Log.e(TAG, "启动语音转写失败", e);
//            Toast.makeText(this, "启动语音转写失败: " + e.getMessage(), Toast.LENGTH_SHORT).show();
//        }
//    }
//
//    private void stopTranscribe() {
//        if (mRTASR != null && isTranscribing) {
//            try {
//                mRTASR.stop();
//            } catch (Exception e) {
//                Log.e(TAG, "停止语音转写失败", e);
//                Toast.makeText(this, "停止语音转写失败: " + e.getMessage(), Toast.LENGTH_SHORT).show();
//            }
//        }
//    }

    @Override
    protected void onDestroy() {
//        stopTranscribe();
        super.onDestroy();
    }
} 