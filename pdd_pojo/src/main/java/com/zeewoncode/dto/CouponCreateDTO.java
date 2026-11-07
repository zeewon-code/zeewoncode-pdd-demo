package com.zeewoncode.dto;

import com.zeewoncode.validation.InValues;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class CouponCreateDTO implements Serializable {

    @NotBlank(message = "券名不能为空")
    private String title;
    @InValues(value = {1,2}, message = "券类型只能是满减或折扣")
    @NotNull
    private Integer type;
    @Positive(message = "满减金额必须大于0")
    private Double conditionAmount;
    @Positive(message = "折扣金额必须大于0")
    private Double discountAmount;
    @DecimalMin(value = "0.01", message = "折扣最高不能高于0.1折")
    @DecimalMax(value = "0.99", message = "折扣最低不能低于过9.9折")
    private Double discountRate;
    @NotNull(message = "优惠券生效时间不允许为空")
    private LocalDateTime validFrom;
    @NotNull(message = "优惠券过期时间不允许为空")
    private LocalDateTime validTo;
    @NotNull(message = "优惠券总数不允许为空")
    @Min(value = 1, message = "优惠券总数必须大于等于1")
    private Integer total;
}
