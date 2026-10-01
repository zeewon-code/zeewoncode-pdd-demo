package com.zeewoncode.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;


/**
 * 用户实体类
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User implements Serializable {

    private Integer id; // 用户ID
    private String nickname; // 用户昵称
    private String phone; // 手机号
    private String password; // 密码
    private String avatar; // 头像URL
    private Integer status; // 用户状态 1：正常 0：冻结
    private Integer userType;  // 用户类型 1.卖家 2.商家 9.管理员
    private LocalDateTime createdAt; // 创建时间
    private LocalDateTime updatedAt; // 更新时间
    private Integer deletedFlag; // 删除标志 0：未删除 1：已删除
}
