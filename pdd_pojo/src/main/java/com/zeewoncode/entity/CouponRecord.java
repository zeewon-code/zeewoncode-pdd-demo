package com.zeewoncode.entity;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class CouponRecord implements Serializable {

    private Long userCouponId;
    private Long userId;
    private String nickname;
    private Integer status;
    private LocalDateTime receivedAt;
    private LocalDateTime usedAt;

}
