package com.zeewoncode.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 低库存sku
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LowStockSkus implements Serializable {

    private Long skuId;
    private Long spuId;
    private String title;
    private String specs;
    private Integer stock; // stock <= 10低库存
}
