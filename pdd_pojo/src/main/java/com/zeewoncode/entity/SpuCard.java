package com.zeewoncode.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 商品列表卡片
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SpuCard implements Serializable {

    private Integer id; // 商品id
    private String title; // 商品标题
    private String mainImage; // 主图URL
    private Double minPrice; // 同商品最低价（规格不同）
    private Double maxPrice; // 同商品最高价 （规格不同）
    private Integer sales; // 销量（该商品所有规格）
    private Double groupPrice; // 拼团价
    private Boolean hasGroupon; // 是否能进行拼团
}
