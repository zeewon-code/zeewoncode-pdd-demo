package com.zeewoncode.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 商品规格实体类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Sku implements Serializable {
    private Integer id;
    private Integer spuId;
    private String specs; //规格描述
    private Double price;
    private Integer stock;
    private Integer lockedStock; //预占库存（下单未支付）
    private String image;
    private Integer status; // 状态：0->禁售；1->可售
    private Integer deletedFlag;

}
