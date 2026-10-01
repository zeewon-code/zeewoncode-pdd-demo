package com.zeewoncode.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 登录结果类
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LoginResult implements Serializable {

    private String token;
    private User user;
}
