package com.zeewoncode.vo;

import com.zeewoncode.entity.User;
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
public class LoginVO implements Serializable {

    private String token;
    private User user;
}
