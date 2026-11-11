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
    private Double conditionAmount;
    private Double discountAmount;
    private Double discountRate;
    private LocalDateTime validTo;
    private Integer remain;
    private Integer issued;
    private Integer type;
}
