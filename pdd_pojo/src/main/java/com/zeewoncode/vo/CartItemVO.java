package com.zeewoncode.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CartItemVO implements Serializable {

    private Long id;
    private Long skuId;
    private Long spuId;
    private String title;
    private String specs;
    private String image;
    private BigDecimal price;
    private Integer quantity;
    private Boolean selected;
}
