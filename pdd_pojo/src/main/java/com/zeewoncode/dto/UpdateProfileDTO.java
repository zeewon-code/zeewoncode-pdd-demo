package com.zeewoncode.dto;

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

    private String nickName; // 昵称
    private String avatar; // 头像url
}
