package com.zeewoncode.req;


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
public class ChangePasswordReq implements Serializable {

    private String oldPassword;
    private String newPassword;
}
