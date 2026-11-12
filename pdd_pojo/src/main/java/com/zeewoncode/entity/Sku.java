package com.zeewoncode.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 商品规格实体类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Sku implements Serializable {
    private Long id;
    private Long spuId;
    private String specs; //规格描述
    private BigDecimal price;
    private Integer stock;
    private Integer lockedStock; //预占库存（下单未支付）
    private String image;
    private Integer status; // 状态：0->禁售；1->可售
    private Integer deletedFlag; // 删除标志：0->未删除；1->已删除
}
