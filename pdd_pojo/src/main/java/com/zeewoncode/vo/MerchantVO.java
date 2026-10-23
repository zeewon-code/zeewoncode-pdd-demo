package com.zeewoncode.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MerchantVO implements Serializable {

    private Long id;
    private Long userId;
    private String shopName;
    private String logo;
    private Integer status;

}
