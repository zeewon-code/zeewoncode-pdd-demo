package com.zeewoncode.pdd_server.mapper.po;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class InstanceRef {

    private Long instanceId;
    private Integer role; // 角色 1:团长（我发起的拼团） 2:参与者（我参加的拼团）
    private LocalDateTime joinTime;  //排序基准
}
