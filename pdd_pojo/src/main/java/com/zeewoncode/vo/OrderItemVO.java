package com.zeewoncode.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderItemVO implements Serializable {
    private Long id; // 订单明细id
    private String title;
    private String specs;
    private String image;
    private BigDecimal price;
    private Integer quantity;
    private BigDecimal amount;
}
