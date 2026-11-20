package com.zeewoncode.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 用户注册请求参数
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterDTO implements Serializable {


    @NotBlank(message = "手机号不能为空")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String phone; // 手机号
    @NotBlank(message = "密码不能为空")
    @Pattern(regexp = "^\\S+$", message = "密码不能包含任何空白字符")
    @Size(min = 6, max = 20, message = "新密码长度在6到20个字符之间")
    private String password; // 密码
    @NotBlank(message = "昵称不能为空")
    @Pattern(regexp = "^\\S+$", message = "昵称不能包含任何空白字符")
    @Size(min = 1, max = 10, message = "昵称长度在1到10个字符之间")
    private String nickname; // 昵称
    @NotBlank(message = "验证码不能为空")
    @Pattern(regexp = "^\\S+$", message = "验证码不能包含任何空白字符")
    private String code; // 验证码
    @Pattern(
            regexp = "^https?://\\S+$",
            message = "头像URL必须以http://或https://开头，且不能包含空格"
    )
    private String avatar; // 头像URL
}
