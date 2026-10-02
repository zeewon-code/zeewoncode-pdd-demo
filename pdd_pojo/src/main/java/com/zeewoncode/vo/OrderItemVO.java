package com.zeewoncode.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderItemVO implements Serializable {
    private Integer id; // 订单明细id
    private String title;
    private String specs;
    private String image;
    private Double price;
    private Integer quantity;
    private Double amount;
}
