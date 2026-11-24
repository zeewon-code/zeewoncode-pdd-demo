package com.zeewoncode.entity;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Builder
public class ShipmentTrack implements Serializable {

    private Long id;
    private Long shipmentId;
    private Integer status;
    private String description;
    private LocalDateTime happenedAt;
    private LocalDateTime createdAt;
}
