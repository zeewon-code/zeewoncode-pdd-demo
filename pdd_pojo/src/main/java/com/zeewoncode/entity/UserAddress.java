package com.zeewoncode.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 收货地址实体类
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserAddress implements Serializable {

    private Integer id; // 地址id
    private Integer userId; // 用户id
    private String receiverName; // 收件人
    private String receiverPhone; // 收件人电话
    private String province; // 省份
    private String city; // 城市
    private String district; // 区域
    private String detail; // 详细地址
    private Integer isDefault; // 是否默认地址
    private Integer deletedFlag; // 删除标志
}
