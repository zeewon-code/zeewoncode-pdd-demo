package com.zeewoncode.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GrouponJoinVO implements Serializable {

    private Long instanceId;
    private Integer currentCount;
    private Integer targetCount;
    private Integer groupStatus;
    private String orderNo;
}
