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
public class OrderPreviewVO implements Serializable {

    private BigDecimal totalAmount;
    private BigDecimal discountAmount; // 优惠总额（优惠券）
    private BigDecimal payableAmount;
}
