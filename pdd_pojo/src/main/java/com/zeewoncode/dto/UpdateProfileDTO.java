package com.zeewoncode.dto;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 修改个人信息请求类
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateProfileDTO {

    @Pattern(regexp = "^\\S+$", message = "昵称不能包含任何空白字符")
    @Size(min = 1, max = 10, message = "昵称长度在1到10个字符之间")
    private String nickName; // 昵称
    @Pattern(
            regexp = "^https?://\\S+$",
            message = "头像URL必须以http://或https://开头，且不能包含空格"
    )
    private String avatar; // 头像url
}
