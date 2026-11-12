package com.zeewoncode.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderItem implements Serializable {

    private Long id;
    private Long orderId;
    private Long spuId;
    private Long skuId;
    private String title;
    private String specs;
    private String image;
    private BigDecimal price;
    private Integer quantity;
    private BigDecimal amount;
    private LocalDateTime createAt;
}
