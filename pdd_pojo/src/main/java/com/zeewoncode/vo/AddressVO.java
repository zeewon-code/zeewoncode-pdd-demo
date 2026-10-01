package com.zeewoncode.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 收货地址视图对象
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AddressVO implements Serializable {
    private Integer id; // 地址id
    private String receiverName; // 收件人
    private String receiverPhone; // 收件人电话
    private String province; // 省份
    private String city; // 城市
    private String district; // 区域
    private String detail; // 详细地址
    private Boolean isDefault; // 是否默认地址
}
