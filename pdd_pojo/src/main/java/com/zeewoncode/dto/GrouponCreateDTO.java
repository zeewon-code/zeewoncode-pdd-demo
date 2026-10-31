package com.zeewoncode.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GrouponCreateDTO implements Serializable {

    @NotNull(message = "商品规格不能为空")
    private Long skuId;
    @NotNull(message = "团购价格不能为空")
    private Double groupPrice;
    @NotNull(message = "成团人数不能为空")
    private Integer targetCount;
    @NotNull(message = "成团期限不能为空")
    @Min(value = 4, message = "成团期限不能少于4小时，最多为24小时")
    @Max(value = 24, message = "成团期限不能少于4小时，最多为24小时")
    private Integer expireHours;
    @NotNull(message = "拼团活动开始时间不能为空")
    private LocalDateTime startTime;
    @NotNull(message = "拼团活动结束时间不能为空")
    private LocalDateTime endTime;
}
