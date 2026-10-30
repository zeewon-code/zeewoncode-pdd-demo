package com.zeewoncode.pdd_server.mapper.po;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class GrouponInstancesCount {

    private Long defineId;
    private Integer runningCount;
    private Integer successCount;
    private Integer failCount;

}
