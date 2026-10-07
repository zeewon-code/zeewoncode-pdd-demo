package com.zeewoncode.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderPreviewResult implements Serializable {

    private Double totalAmount;
    private Double discountAmount; // 优惠总额（优惠券）
    private Double payableAmount;
}
