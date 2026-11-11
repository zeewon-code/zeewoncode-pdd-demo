package com.zeewoncode.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SkuUpdateDTO implements Serializable {

    @NotBlank(message = "规格不能为空")
    private String specs;
    @NotNull(message = "价格不能为空")
    @Positive(message = "价格必须大于0")
    private Double price;
    private String image;
}
