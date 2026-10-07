package com.zeewoncode.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class PayResult implements Serializable {

    private Long paymentId;
    private String paymentNo;
    private BigDecimal paidAmount;
}
