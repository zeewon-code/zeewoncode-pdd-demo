package com.zeewoncode.req;

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
public class UpdateProfileReq {

    private String nickName; // 昵称
    private String avatar; // 头像url
}
