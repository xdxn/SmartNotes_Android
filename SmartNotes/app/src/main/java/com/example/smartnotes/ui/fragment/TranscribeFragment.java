package com.example.smartnotes.ui.fragment;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.util.Log;
import android.app.AlertDialog;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.smartnotes.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import com.iflytek.sparkchain.core.rtasr.RTASR;
import com.iflytek.sparkchain.core.rtasr.RTASRCallbacks;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import com.example.smartnotes.utils.AudioRecorder;
import com.example.smartnotes.SmartNotesApplication;

public class TranscribeFragment extends Fragment {
    private static final String TAG = "TranscribeFragment";
    private static final int PERMISSION_REQUEST_CODE = 1;

    private FloatingActionButton recordButton;
    private TextView tvTranscript;
    private TextView tvRecordingStatus;
    private boolean isRecording = false;
    private StringBuilder transcriptionBuilder = new StringBuilder();
    
    private RTASR mRTASR;
    private String asrFinalResult = "识别结果：\n";
    private AudioRecorder audioRecorder;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initRTASR();
        initAudioRecorder();
    }

    private void initRTASR() {
        try {
            Log.d(TAG, "Starting RTASR initialization...");
            mRTASR = SmartNotesApplication.getRTASR();
            if (mRTASR == null) {
                throw new RuntimeException("RTASR not initialized");
            }
            mRTASR.registerCallbacks(mRtAsrCallbacks);
            Log.d(TAG, "RTASR callbacks registered successfully");
        } catch (Exception e) {
            Log.e(TAG, "RTASR initialization failed", e);
            showError("语音识别初始化失败：" + e.getMessage());
        }
    }

    private void initAudioRecorder() {
        audioRecorder = new AudioRecorder();
        audioRecorder.setOnAudioDataListener((data, length) -> {
            if (mRTASR != null && isRecording) {
                try {
                    mRTASR.write(data);
                    Thread.sleep(10);
                } catch (Exception e) {
                    Log.e(TAG, "Error writing audio data", e);
                }
            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_transcribe, container, false);
        recordButton = view.findViewById(R.id.recordButton);
        tvTranscript = view.findViewById(R.id.tvTranscript);
        tvRecordingStatus = view.findViewById(R.id.tvRecordingStatus);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recordButton.setOnClickListener(v -> {
            if (checkPermission()) {
                toggleRecording();
            } else {
                requestPermission();
            }
        });
    }

    private void toggleRecording() {
        isRecording = !isRecording;
        if (isRecording) {
            startRecording();
        } else {
            stopRecording();
        }
        updateUI();
    }

    private void startRecording() {
        if (mRTASR == null) {
            showError("语音转写未初始化");
            return;
        }

        // 重置结果
        transcriptionBuilder.setLength(0);
        asrFinalResult = "识别结果：\n";
        tvTranscript.setText(asrFinalResult);
        
        // 开始转写
        int ret = mRTASR.start(String.valueOf(System.currentTimeMillis()));
        if (ret != 0) {
            isRecording = false;
            showError("转写启动失败，错误码:" + ret);
            updateUI();
            return;
        }

        // 开始录音
        audioRecorder.startRecording();
    }

    private void stopRecording() {
        // 停止录音
        if (audioRecorder != null) {
            audioRecorder.stopRecording();
        }
        
        // 停止转写
        if (mRTASR != null) {
            mRTASR.stop();
        }
        
        // 保存结果
        saveTranscription();
    }

    private void saveTranscription() {
        String text = transcriptionBuilder.toString().trim();
        if (text.isEmpty()) {
            showError("没有需要保存的内容");
            return;
        }

        new AlertDialog.Builder(requireContext())
                .setTitle("保存选项")
                .setMessage("请选择操作")
                .setPositiveButton("生成摘要", (dialog, which) -> {
                    showError("摘要生成功能开发中...");
                })
                .setNeutralButton("直接编辑", (dialog, which) -> {
                    saveToFile(text);
                })
                .setNegativeButton("不保存", (dialog, which) -> {
                    clearTranscription();
                })
                .setCancelable(false)
                .show();
    }

    private void saveToFile(String text) {
        try {
            // 获取应用私有目录
            File dir = new File(requireContext().getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), "transcriptions");
            if (!dir.exists() && !dir.mkdirs()) {
                showError("创建目录失败");
                return;
            }

            // 使用时间戳作为文件名
            String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
            File file = new File(dir, "transcription_" + timestamp + ".txt");

            // 写入文件
            try (FileWriter writer = new FileWriter(file)) {
                writer.write(text);
            }

            showSuccess("转录文本已保存至: " + file.getAbsolutePath());
            clearTranscription();

        } catch (IOException e) {
            Log.e(TAG, "保存文件失败", e);
            showError("保存文件失败：" + e.getMessage());
        }
    }

    private void clearTranscription() {
        transcriptionBuilder.setLength(0);
        asrFinalResult = "识别结果：\n";
        tvTranscript.setText(asrFinalResult);
    }

    private RTASRCallbacks mRtAsrCallbacks = new RTASRCallbacks() {
        @Override
        public void onResult(RTASR.RtAsrResult result, Object usrTag) {
            String data = result.getData();
            int status = result.getStatus();

            if (!isAdded()) return;
            requireActivity().runOnUiThread(() -> {
                if (status == 1) {  // 子句流式结果
                    tvTranscript.setText(asrFinalResult + data);
                } else if (status == 2) {  // 子句plain结果
                    asrFinalResult = asrFinalResult + data;
                    transcriptionBuilder.append(data);
                } else if (status == 3) {  // end结果
                    isRecording = false;
                    tvTranscript.setText(asrFinalResult);
                    updateUI();
                }
            });
        }

        @Override
        public void onError(RTASR.RtAsrError error, Object usrTag) {
            if (!isAdded()) return;
            int code = error.getCode();
            String msg = error.getErrMsg();
            requireActivity().runOnUiThread(() -> {
                showError("转写出错，错误码:" + code + ",错误信息:" + msg);
                isRecording = false;
                updateUI();
            });
        }
    };

    private void updateUI() {
        if (!isAdded()) return;
        recordButton.setImageResource(isRecording ? 
            R.drawable.ic_baseline_stop_24 : R.drawable.ic_baseline_mic_24);
        tvRecordingStatus.setText(isRecording ? "正在录音..." : "点击开始录音");
    }

    private void showError(String message) {
        if (!isAdded()) return;
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
    }

    private void showSuccess(String message) {
        if (!isAdded()) return;
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
    }

    private boolean checkPermission() {
        return ContextCompat.checkSelfPermission(requireContext(), 
            Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(requireActivity(),
            new String[]{Manifest.permission.RECORD_AUDIO},
            PERMISSION_REQUEST_CODE);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopRecordingAndCleanup();
    }

    private void stopRecordingAndCleanup() {
        if (isRecording) {
            if (audioRecorder != null) {
                audioRecorder.stopRecording();
            }
            
            if (mRTASR != null) {
                mRTASR.stop();
            }
            isRecording = false;
        }

        audioRecorder = null;
        mRTASR = null;
    }
} 