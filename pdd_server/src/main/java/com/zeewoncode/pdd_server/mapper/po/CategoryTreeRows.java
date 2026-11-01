package com.zeewoncode.pdd_server.mapper.po;

import lombok.Data;

@Data
public class CategoryTreeRows {
    private Long id;
    private Long parentId;
    private String name;
    private Integer level;
    private Integer sort;
    private Integer spuCount;   // 直接挂载的商品数，后续一级节点再加总
}
