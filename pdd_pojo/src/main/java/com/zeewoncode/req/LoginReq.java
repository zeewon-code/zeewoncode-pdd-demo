package com.zeewoncode.req;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;


/**
 * 登录请求类
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginReq implements Serializable {

    private String phone; // 手机号
    private String password; // 密码
}
