package com.zeewoncode.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShipmentVO implements Serializable {

    private Long shipmentId;
    private Long orderId;
    private String trackingNo; // 运单号
    private String carrier; // 快递公司
    private Integer status; // 0-待发货 1-已发货 2-配送中 3-已签收
    private LocalDateTime shippedAt; // 发货时间
    private LocalDateTime receivedAt; // 签收时间
    private LocalDateTime createdAt;
    private List<ShipmentStep> steps; // 物流运送轨迹
}
