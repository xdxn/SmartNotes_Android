package com.example.smartnotes.ui.fragment;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.smartnotes.R;
import com.example.smartnotes.model.Note;
import com.example.smartnotes.model.LoginResponse;
import com.example.smartnotes.network.ApiHelper;
import com.example.smartnotes.network.response.NoteResponse;
import com.example.smartnotes.ui.activity.NoteEditActivity;
import com.example.smartnotes.ui.adapter.NoteAdapter;
import com.example.smartnotes.utils.UserManager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;


import java.util.ArrayList;
import java.util.List;

public class NotesFragment extends Fragment {
    private static final int REQUEST_CODE_ADD_NOTE = 1;
    private static final int REQUEST_CODE_EDIT_NOTE = 2;
    private static final String TAG = "NotesFragment";

    private RecyclerView rvNotes;
    private FloatingActionButton fabAdd;
    private SwipeRefreshLayout swipeRefresh;
    private NoteAdapter noteAdapter;
    private List<Note> notes = new ArrayList<>();
    private boolean isLoading = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notes, container, false);
        initViews(view);
        return view;
    }

    private void initViews(View view) {
        rvNotes = view.findViewById(R.id.rvNotes);
        fabAdd = view.findViewById(R.id.fabAdd);
        swipeRefresh = view.findViewById(R.id.swipeRefresh);

        // 设置RecyclerView
        LinearLayoutManager layoutManager = new LinearLayoutManager(requireContext());
        rvNotes.setLayoutManager(layoutManager);
        noteAdapter = new NoteAdapter();
        rvNotes.setAdapter(noteAdapter);

        // 添加滑动删除功能
        ItemTouchHelper.SimpleCallback swipeCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                Note noteToDelete = notes.get(position);
                
                // 显示确认对话框
                new AlertDialog.Builder(requireContext())
                    .setTitle("删除笔记")
                    .setMessage("确定要删除这条笔记吗？")
                    .setPositiveButton("确定", (dialog, which) -> {
                        deleteNote(noteToDelete, position);
                    })
                    .setNegativeButton("取消", (dialog, which) -> {
                        // 恢复列表项
                        noteAdapter.notifyItemChanged(position);
                    })
                    .show();
            }

            @Override
            public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                View itemView = viewHolder.itemView;
                
                // 设置背景颜色
                Paint paint = new Paint();
                paint.setColor(Color.RED); // 使用一个简单的红色作为删除背景色
                
                // 绘制背景
                if (dX > 0) { // 右滑
                    c.drawRect(itemView.getLeft(), itemView.getTop(), dX,
                            itemView.getBottom(), paint);
                } else { // 左滑
                    c.drawRect(itemView.getRight() + dX, itemView.getTop(),
                            itemView.getRight(), itemView.getBottom(), paint);
                }

                // 绘制删除图标
                Drawable icon = ContextCompat.getDrawable(requireContext(), R.drawable.ic_delete);
                if (icon != null) {
                    int iconMargin = (itemView.getHeight() - icon.getIntrinsicHeight()) / 2;
                    int iconTop = itemView.getTop() + iconMargin;
                    int iconBottom = iconTop + icon.getIntrinsicHeight();

                    if (dX > 0) { // 右滑
                        int iconLeft = itemView.getLeft() + iconMargin;
                        int iconRight = iconLeft + icon.getIntrinsicWidth();
                        icon.setBounds(iconLeft, iconTop, iconRight, iconBottom);
                    } else { // 左滑
                        int iconRight = itemView.getRight() - iconMargin;
                        int iconLeft = iconRight - icon.getIntrinsicWidth();
                        icon.setBounds(iconLeft, iconTop, iconRight, iconBottom);
                    }
                    icon.setTint(Color.WHITE);
                    icon.draw(c);
                }
                
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        };

        new ItemTouchHelper(swipeCallback).attachToRecyclerView(rvNotes);

        // 设置下拉刷新
        swipeRefresh.setOnRefreshListener(this::refreshNotes);

        // 设置点击事件
        noteAdapter.setOnNoteClickListener(note -> {
            Intent intent = new Intent(requireContext(), NoteEditActivity.class);
            intent.putExtra("note", note);
            startActivityForResult(intent, REQUEST_CODE_EDIT_NOTE);
        });

        // 添加笔记按钮点击事件
        fabAdd.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), NoteEditActivity.class);
            startActivityForResult(intent, REQUEST_CODE_ADD_NOTE);
        });

        // 加载笔记数据
        refreshNotes();
    }

    private void loadNotes(boolean isRefresh) {
        isLoading = true;
        Log.d(TAG, "开始加载笔记列表");
        
        LoginResponse currentUser = UserManager.getInstance().getCurrentUser();
        if (currentUser == null || currentUser.getId() == null) {
            Log.e(TAG, "加载笔记失败：" + (currentUser == null ? "用户未登录" : "用户ID为空"));
            showError("请先登录后再查看笔记");
            finishLoading();
            return;
        }
        Long userId = currentUser.getId();
        Log.d(TAG, "当前用户ID: " + userId);
        
        ApiHelper.getNotes(userId, requireContext(), new ApiHelper.ApiCallback<List<NoteResponse>>() {
            @Override
            public void onSuccess(List<NoteResponse> noteResponses) {
                Log.d(TAG, "成功获取笔记列表，数量：" + (noteResponses != null ? noteResponses.size() : 0));
                List<Note> newNotes = convertToNotes(noteResponses);
                
                notes.clear();
                notes.addAll(newNotes);
                Log.d(TAG, "更新笔记列表，当前总数：" + notes.size());
                
                requireActivity().runOnUiThread(() -> {
                    noteAdapter.setNotes(notes);
                    Log.d(TAG, "刷新笔记列表显示");
                    finishLoading();
                });
            }

            @Override
            public void onError(String message) {
                Log.e(TAG, "加载笔记失败：" + message);
                requireActivity().runOnUiThread(() -> {
                    showError("加载笔记失败：" + message);
                    finishLoading();
                });
            }
        });
    }

    private void refreshNotes() {
        if (!isLoading) {
            loadNotes(true);
        }
    }

    private void finishLoading() {
        isLoading = false;
        Log.d(TAG, "完成加载操作");
        requireActivity().runOnUiThread(() -> {
            swipeRefresh.setRefreshing(false);
        });
    }

    private void showError(String message) {
        Log.e(TAG, "显示错误信息：" + message);
        requireActivity().runOnUiThread(() -> {
            Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
        });
    }

    private List<Note> convertToNotes(List<NoteResponse> noteResponses) {
        List<Note> noteList = new ArrayList<>();
        for (NoteResponse response : noteResponses) {
            Note note = new Note();
            note.setId(response.getId());
            note.setTitle(response.getTitle());
            note.setContent(response.getContent());
            note.setCreateTime(response.getCreatedAt());
            note.setUpdateTime(response.getUpdatedAt());
            noteList.add(note);
        }
        return noteList;
    }

    private void deleteNote(Note note, int position) {
        Log.d(TAG, "开始删除笔记，ID：" + note.getId());
        LoginResponse currentUser = UserManager.getInstance().getCurrentUser();
        if (currentUser == null) {
            showError("用户未登录");
            return;
        }

        ApiHelper.deleteNote(note.getId(), currentUser.getId(), requireContext(), new ApiHelper.ApiCallback<Void>() {
            @Override
            public void onSuccess(Void result) {
                requireActivity().runOnUiThread(() -> {
                    notes.remove(position);
                    noteAdapter.notifyItemRemoved(position);
                    Toast.makeText(requireContext(), "笔记已删除", Toast.LENGTH_SHORT).show();
                });
            }

            @Override
            public void onError(String message) {
                requireActivity().runOnUiThread(() -> {
                    showError("删除失败: " + message);
                    noteAdapter.notifyItemChanged(position);
                });
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && data != null) {
            refreshNotes();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshNotes();
    }
} 