package com.zeewoncode.dto;

import jakarta.validation.constraints.*;
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
    @DecimalMin(value = "0", inclusive = false, message = "商品规格价格必须大于0")
    @Digits(integer = 18, fraction = 2, message = "金额小数位最多有两位")
    private BigDecimal price;
    private String image;
}
