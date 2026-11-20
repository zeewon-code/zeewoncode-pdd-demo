package com.zeewoncode.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartAddDTO implements Serializable {

    private Long skuId;
    @Min(value = 1, message = "数量必须大于等于1")
    @NotNull(message = "数量不能为空")
    private Integer quantity;
}
