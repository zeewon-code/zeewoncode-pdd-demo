package com.zeewoncode.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Coupon implements Serializable {

    private Long id;
    private String title;
    private Integer type;
    private Double conditionAmount;
    private Double discountAmount;
    private Double discountRate;
    private LocalDateTime validFrom;
    private LocalDateTime validTo;
    private Long total;
    private Integer issued;
    private LocalDateTime createdAt;
}
