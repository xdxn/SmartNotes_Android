package com.example.smartnotes.ui.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import com.example.smartnotes.ui.fragment.TranscribeFragment;
import com.example.smartnotes.ui.fragment.NotesFragment;
import com.example.smartnotes.ui.fragment.TasksFragment;
import com.example.smartnotes.ui.fragment.ProfileFragment;

public class ViewPagerAdapter extends FragmentStateAdapter {
    public ViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new TranscribeFragment(); // 转写Fragment
            case 1:
                return new NotesFragment(); // 笔记Fragment
            case 2:
                return new TasksFragment(); // 任务清单Fragment
            case 3:
                return new ProfileFragment(); // 我的Fragment
            default:
                return new TranscribeFragment(); // 默认返回转写Fragment
        }
    }

    @Override
    public int getItemCount() {
        return 4; // 四个Fragment
    }
} 