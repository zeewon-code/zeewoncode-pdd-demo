package com.zeewoncode.utils;

import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.regex.Pattern;

/**
 * 信息校验器：名称或标题 +  URL，纯业务校验，不涉及注解
 */
//@Component
public class InfoValidator {

    private InfoValidator() { throw new UnsupportedOperationException(); }


    private static final Pattern NAME_PATTERN = Pattern.compile("^[\\u4e00-\\u9fa5a-zA-Z0-9\\s\\-_.&()（）]{2,50}$");
    private static final Pattern URL_PATTERN  = Pattern.compile(
            "^https?://[\\w.-]+(?:\\.[\\w.-]+)+[\\w\\-._~:/?#\\[\\]@!$&'()*+,;=%]+$");

    /**
     * 校验标题或名称信息，不合法直接抛异常
     */
    public static void validateName(String name) {
        if (!StringUtils.hasText(name)) {
            throw new IllegalArgumentException("名称或标题不能为空");
        }
        String trimmed = name.trim();
        if (!NAME_PATTERN.matcher(trimmed).matches()) {
            throw new IllegalArgumentException("名称或标题仅支持中文、英文、数字及 -_.&()，长度 2~50");
        }
        if (trimmed.replaceAll("\\s+", "").isEmpty()) {
            throw new IllegalArgumentException("名称或标题不能为纯空格");
        }
    }

    /**
     * 校验URL，不合法直接抛异常（允许为空，非必填）
     */
    public static void validateAvatarUrl(String avatarUrl) {

        if (!StringUtils.hasText(avatarUrl)) {
            return; // 非必填，为空跳过
        }
        String trimmedUrl = avatarUrl.trim();
        if (!URL_PATTERN.matcher(trimmedUrl).matches()) {
            throw new IllegalArgumentException("URL 格式不合法，必须以 http:// 或 https:// 开头");
        }
        if (!isImageFile(trimmedUrl)) {
            throw new IllegalArgumentException("必须是图片格式（jpg/png/jpeg/webp/gif）");
        }
        if (trimmedUrl.length() > 512) {
            throw new IllegalArgumentException(" URL 长度不能超过 512 字符");
        }

    }

    /**
     * 校验URL列表，不合法直接抛异常（允许为空，非必填）
     */
    public static void validateAvatarUrlList(List<String> avatarUrl) {
        for (String url : avatarUrl) {
            if (!StringUtils.hasText(url)) {
                return; // 非必填，为空跳过
            }
            String trimmedUrl = url.trim();
            if (!URL_PATTERN.matcher(trimmedUrl).matches()) {
                throw new IllegalArgumentException("URL 格式不合法，必须以 http:// 或 https:// 开头");
            }
            if (!isImageFile(trimmedUrl)) {
                throw new IllegalArgumentException("必须是图片格式（jpg/png/jpeg/webp/gif）");
            }
            if (trimmedUrl.length() > 512) {
                throw new IllegalArgumentException(" URL 长度不能超过 512 字符");
            }
        }
    }


    private static boolean isImageFile(String url) {
        String lower = url.toLowerCase().split("\\?")[0];
        return lower.endsWith(".jpg") || lower.endsWith(".jpeg")
                || lower.endsWith(".png") || lower.endsWith(".webp")
                || lower.endsWith(".gif");
    }
}