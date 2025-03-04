package com.example.smartnotes.utils;

public class TextProcessor {
    /**
     * 对文本进行预处理
     * 1. 去除多余的空格和换行符
     * 2. 去除特殊字符
     * 3. 确保文本不为空
     */
    public static String preprocessText(String text) {
        if (text == null || text.trim().isEmpty()) {
            return "";
        }

        // 1. 将多个连续空格替换为单个空格
        text = text.replaceAll("\\s+", " ");
        
        // 2. 将多个连续换行符替换为单个换行符
        text = text.replaceAll("\\n+", "\n");
        
        // 3. 去除行首行尾的空白字符
        text = text.trim();
        
        // 4. 去除特殊字符，但保留基本标点符号
        text = text.replaceAll("[^\\w\\s,.!?，。！？、]", "");
        
        return text;
    }
} 