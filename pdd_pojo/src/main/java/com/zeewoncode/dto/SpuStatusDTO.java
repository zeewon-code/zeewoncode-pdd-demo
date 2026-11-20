package com.zeewoncode.dto;

import com.zeewoncode.validation.InValues;
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
public class SpuStatusDTO implements Serializable {

    @NotNull(message = "状态不能为空")
    @InValues(value = {0, 1}, message = "状态必须是0或1")
    private Integer status;
}
