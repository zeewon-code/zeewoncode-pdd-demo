package com.zeewoncode.entity;

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
public class GrouponInstance implements Serializable {

    private Long id;
    private Long defineId;
    private Long leaderUserId;
    private Integer currentCount; // 已拼人数
    private Integer targetCount; // 成团人数
    private Integer status; // 0：拼团中 1：拼团成功 2：拼团失败（超时退款） 3：已取消
    private LocalDateTime expireAt; // 成团截止时间
    private LocalDateTime createdAt;
}
