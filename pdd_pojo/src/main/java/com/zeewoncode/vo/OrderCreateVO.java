package com.zeewoncode.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderCreateVO implements Serializable {

    private Integer orderId;
    private String orderNo;
    private BigDecimal payableAmount;
    private LocalDateTime payExpireTime; //支付过期时间
}
