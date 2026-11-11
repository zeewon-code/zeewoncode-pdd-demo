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
public class GrouponInitiateVO implements Serializable {

    private Long instanceId;
    private String instanceNo;
    private String orderNo;
    private Integer currentCount;
    private Integer targetCount;
    private LocalDateTime expireAt;
    private Double groupPrice;
    private Double payableAmount;
}
