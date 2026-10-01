package com.zeewoncode.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * 商品详情实体类
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SpuDetail implements Serializable {

    private Integer id;
    private String title;
    private String subtitle;
    private Integer merchantId; // 店铺id
    private String shopName; // 店铺名称
    private String mainImage;
    private List<String> images; // 图集JSON数组
    private String details; // 图文详情（富文本/JSON）
    private Integer sales;
    private List<Sku> skus;
    private List<GrouponDefine> groupons; // 拼团规则
}
