package com.zeewoncode.utils;

import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import java.util.regex.Pattern;

/**
 * 店铺信息校验器：名称 + 头像 URL，纯业务校验，不涉及注解
 */
@Component
public class ShopValidator {

    private static final Pattern NAME_PATTERN = Pattern.compile("^[\\u4e00-\\u9fa5a-zA-Z0-9\\s\\-_.&()（）]{2,50}$");
    private static final Pattern URL_PATTERN  = Pattern.compile(
            "^https?://[\\w.-]+(?:\\.[\\w.-]+)+[\\w\\-._~:/?#\\[\\]@!$&'()*+,;=%]+$");

    /**
     * 校验店铺名称，不合法直接抛异常
     */
    public void validateName(String name) {
        if (!StringUtils.hasText(name)) {
            throw new IllegalArgumentException("店铺名称不能为空");
        }
        String trimmed = name.trim();
        if (!NAME_PATTERN.matcher(trimmed).matches()) {
            throw new IllegalArgumentException("店铺名称仅支持中文、英文、数字及 -_.&()，长度 2~50");
        }
        if (trimmed.replaceAll("\\s+", "").isEmpty()) {
            throw new IllegalArgumentException("店铺名称不能为纯空格");
        }
    }

    /**
     * 校验头像 URL，不合法直接抛异常（允许为空，非必填）
     */
    public void validateAvatarUrl(String avatarUrl) {
        if (!StringUtils.hasText(avatarUrl)) {
            return; // 非必填，为空跳过
        }
        String url = avatarUrl.trim();
        if (!URL_PATTERN.matcher(url).matches()) {
            throw new IllegalArgumentException("头像 URL 格式不合法，必须以 http:// 或 https:// 开头");
        }
        if (!isImageFile(url)) {
            throw new IllegalArgumentException("头像必须是图片格式（jpg/png/jpeg/webp/gif）");
        }
        if (url.length() > 512) {
            throw new IllegalArgumentException("头像 URL 长度不能超过 512 字符");
        }
    }

    /**
     * 如果需要同时校验两个字段，一次抛完所有错误
     */
    public void validate(String name, String avatarUrl) {
        StringBuilder sb = new StringBuilder();
        try {
            validateName(name);
        } catch (IllegalArgumentException e) {
            sb.append(e.getMessage()).append("; ");
        }
        try {
            validateAvatarUrl(avatarUrl);
        } catch (IllegalArgumentException e) {
            sb.append(e.getMessage()).append("; ");
        }
        if (sb.length() > 0) {
            throw new IllegalArgumentException(sb.toString());
        }
    }

    private boolean isImageFile(String url) {
        String lower = url.toLowerCase().split("\\?")[0];
        return lower.endsWith(".jpg") || lower.endsWith(".jpeg")

                || lower.endsWith(".png") || lower.endsWith(".webp")
                || lower.endsWith(".gif");
    }
}