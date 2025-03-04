package com.example.smartnotes.utils;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.util.Log;

public class AudioRecorder {
    private static final String TAG = "AudioRecorder";
    private static final int SAMPLE_RATE = 16000;
    private static final int CHANNEL_CONFIG = AudioFormat.CHANNEL_IN_MONO;
    private static final int AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT;
    
    private AudioRecord audioRecord;
    private boolean isRecording = false;
    private OnAudioDataListener listener;
    
    public interface OnAudioDataListener {
        void onAudioData(byte[] data, int length);
    }
    
    public void setOnAudioDataListener(OnAudioDataListener listener) {
        this.listener = listener;
    }
    
    public void startRecording() {
        if (isRecording) return;
        
        int minBufferSize = AudioRecord.getMinBufferSize(
            SAMPLE_RATE, CHANNEL_CONFIG, AUDIO_FORMAT);
            
        audioRecord = new AudioRecord(
            MediaRecorder.AudioSource.MIC,
            SAMPLE_RATE,
            CHANNEL_CONFIG,
            AUDIO_FORMAT,
            minBufferSize);
            
        if (audioRecord.getState() != AudioRecord.STATE_INITIALIZED) {
            Log.e(TAG, "AudioRecord initialization failed");
            return;
        }
        
        isRecording = true;
        audioRecord.startRecording();
        
        // 开始读取音频数据
        new Thread(() -> {
            byte[] buffer = new byte[320];  // 每次读取320字节
            while (isRecording) {
                int len = audioRecord.read(buffer, 0, buffer.length);
                if (len > 0 && listener != null) {
                    listener.onAudioData(buffer.clone(), len);
                }
            }
        }).start();
    }
    
    public void stopRecording() {
        isRecording = false;
        if (audioRecord != null) {
            try {
                audioRecord.stop();
                audioRecord.release();
            } catch (Exception e) {
                Log.e(TAG, "Error stopping AudioRecord", e);
            }
            audioRecord = null;
        }
    }
} 