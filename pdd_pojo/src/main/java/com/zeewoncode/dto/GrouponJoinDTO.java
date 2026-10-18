package com.zeewoncode.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GrouponJoinDTO implements Serializable {

    @NotNull(message = "地址ID不能为空")
    private Long addressId;
    @NotNull(message = "数量不能为空")
    private Integer quantity;
}
