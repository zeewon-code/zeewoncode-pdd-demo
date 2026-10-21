package com.zeewoncode.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CouponVO implements Serializable {

    private Long id;
    private String title;
    private BigDecimal conditionAmount;
    private BigDecimal discountAmount;
    private Double discountRate;
    private LocalDateTime validTo;
    private Integer remain;
}
