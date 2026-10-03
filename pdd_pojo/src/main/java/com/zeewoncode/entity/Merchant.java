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
public class Merchant implements Serializable {

    private Long id;
    private Long userId;
    private String shopName;
    private String logo;
    private Integer status;
    private LocalDateTime createdAt;
    private Integer deletedFlag;
}
