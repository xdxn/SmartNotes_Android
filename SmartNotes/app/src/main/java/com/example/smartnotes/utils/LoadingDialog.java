package com.example.smartnotes.utils;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.example.smartnotes.R;

public class LoadingDialog extends Dialog {
    private TextView tvMessage;

    public LoadingDialog(Context context) {
        super(context, R.style.LoadingDialog);
        init(context);
    }

    private void init(Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_loading, null);
        tvMessage = view.findViewById(R.id.tvMessage);
        setContentView(view);
        setCancelable(false);
    }

    public void setMessage(String message) {
        tvMessage.setText(message);
    }
} 