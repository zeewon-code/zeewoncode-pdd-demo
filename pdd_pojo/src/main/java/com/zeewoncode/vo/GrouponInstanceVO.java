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
public class GrouponInstanceVO implements Serializable {

    private Long instanceId;
    private Long defineId;
    private Long leaderUserId;
    private String leaderNickname;
    private String spuTitle;
    private BigDecimal groupPrice;
    private Integer currentCount;
    private Integer targetCount;
    private Integer remainCount;
    private Integer status;
    private LocalDateTime expireAt;
    private String orderNo;
    private Integer role; // 角色 1:团长（我发起的拼团） 2:参与者（我参加的拼团）
}
