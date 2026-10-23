package com.zeewoncode.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CouponOptionVO implements Serializable {
    private Long userCouponId;
    private Long couponId;
    private String title;
    private Integer type;
    private Double conditionAmount;
    private Double discountAmount;
    private Double discountRate;
    private LocalDateTime validTo;

}
