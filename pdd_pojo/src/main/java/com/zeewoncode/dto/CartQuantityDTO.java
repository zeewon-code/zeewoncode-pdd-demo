package com.zeewoncode.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CartQuantityDTO implements Serializable {

    @Min(value = 1, message = "所修改的购物车商品数量至少为1")
    @NotNull(message = "所修改的购物车商品数量不能为空")
    private Integer quantity;
}
