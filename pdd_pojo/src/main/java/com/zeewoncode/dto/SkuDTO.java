package com.zeewoncode.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SkuDTO implements Serializable {

    @NotBlank(message = "规格不能为空")
    private String specs;
    @NotNull(message = "价格不能为空")
    @DecimalMin(value = "0.01", message = "价格不能小于0.01")
    private BigDecimal price;
    @NotNull(message = "库存不能为空")
    @Min(value = 1, message = "库存不能小于1")
    private Integer stock;
    private String image;
}
