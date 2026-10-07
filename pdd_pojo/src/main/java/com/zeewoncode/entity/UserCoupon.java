package com.zeewoncode.entity;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserCoupon {

    private Long id;
    private Long userId;
    private Long couponId;
    private Integer status;
    private Long orderId;
    private LocalDateTime receiveAt;
    private LocalDateTime usedAt;
}
