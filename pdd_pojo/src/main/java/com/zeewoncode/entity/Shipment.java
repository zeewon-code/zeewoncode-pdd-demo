package com.zeewoncode.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Shipment implements Serializable {

    private Long id;
    private Long orderId;
    private String trackingNo; // 运单号
    private String carrier; // 快递公司
    private Integer status; // 0-代发货 1-已发货 2-配送中 3-已签收
    private LocalDateTime shippedAt; // 发货时间
    private LocalDateTime receivedAt; // 签收时间
    private LocalDateTime createdAt; // 创建时间

}
