package com.zeewoncode.dto;

import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderItemDTO implements Serializable {

    private Long skuId;
    @Min(value = 1, message = "商品数量必须至少为1")
    private Integer quantity;
}
