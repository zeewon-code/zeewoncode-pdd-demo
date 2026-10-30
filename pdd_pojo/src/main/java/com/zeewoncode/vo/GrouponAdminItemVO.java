package com.zeewoncode.vo;

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
public class GrouponAdminItemVO implements Serializable {

    private Long id;
    private Long spuId;
    private Long skuId;
    private String title;
    private Double groupPrice;
    private Integer targetCount;
    private Integer status;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Integer runningCount;
    private Integer successCount;
    private Integer failCount;
}
