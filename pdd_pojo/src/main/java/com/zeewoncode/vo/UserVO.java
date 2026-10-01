package com.zeewoncode.vo;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 用户视图类，用于用户信息的传输
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserVO {

    private Integer id; // 用户id
    private String nickname; // 昵称
    private String phone; // 手机号
    private String avatar; // 头像url
    private Integer userType; // 用户类型 1.买家 2.商家 9.管理员
    private Integer status; // 用户状态 1.正常 0.冻结
}
