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
 * 修改密码请求类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChangePasswordDTO implements Serializable {

    @NotBlank(message = "旧密码不能为空")
    @Pattern(regexp = "^\\S+$", message = "旧密码不能包含任何空白字符")
    private String oldPassword;
    @NotBlank(message = "新密码不能为空")
    @Pattern(regexp = "^\\S+$", message = "新密码密码不能包含任何空白字符")
    @Size(min = 6, max = 20, message = "新密码长度在6到20个字符之间")
    private String newPassword;
}
