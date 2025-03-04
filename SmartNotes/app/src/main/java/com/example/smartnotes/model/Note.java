package com.example.smartnotes.model;

import java.io.Serializable;
import java.util.Date;

public class Note implements Serializable {
    private Long id;
    private Long userId;
    private String title;
    private String content;
    private String summary;
    private Date createTime;
    private Date updateTime;
    private boolean hasSummary;

    public Note() {
        this.createTime = new Date();
        this.updateTime = new Date();
        this.hasSummary = false;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getSummary() { return summary; }
    public void setSummary(String summary) { 
        this.summary = summary;
        this.hasSummary = summary != null && !summary.isEmpty();
    }

    public Date getCreateTime() { return createTime; }
    public void setCreateTime(Date createTime) { this.createTime = createTime; }

    public Date getUpdateTime() { return updateTime; }
    public void setUpdateTime(Date updateTime) { this.updateTime = updateTime; }

    public boolean isHasSummary() { return hasSummary; }
    public void setHasSummary(boolean hasSummary) { this.hasSummary = hasSummary; }

    public String getPreviewContent() {
        if (hasSummary && summary != null) {
            return summary;
        }
        if (content != null && content.length() > 100) {
            return content.substring(0, 100) + "...";
        }
        return content != null ? content : "";
    }
} 