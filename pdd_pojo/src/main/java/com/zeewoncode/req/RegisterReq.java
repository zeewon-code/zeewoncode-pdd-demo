package com.zeewoncode.req;

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
public class RegisterReq implements Serializable {


    private String phone; // 手机号
    private String password; // 密码
    private String nickname; // 昵称
    private String code; // 验证码
}
