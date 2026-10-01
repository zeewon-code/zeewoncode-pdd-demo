package com.zeewoncode.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 拼团规则实体类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GrouponDefine implements Serializable {

    private Integer id;
    private Integer skuId;
    private Integer spuId;
    private Double groupPrice;
    private Integer targetCount; // 成团所需人数
    private Integer expireHours; // 成团时限
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Integer status; // 1.启用 2.禁用

}
