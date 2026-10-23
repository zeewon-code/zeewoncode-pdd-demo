package com.zeewoncode.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.*;
import javax.validation.constraints.Size;
import java.io.Serializable;

@AllArgsConstructor
@Builder
@Data
@NoArgsConstructor
public class ShopApplyDTO implements Serializable {

    @NotBlank(message = "店铺名称不能为空")
    @Size(min = 2, max = 20, message = "店铺名称长度需在2-20字符之间")
    @Pattern(regexp = "^[\\u4e00-\\u9fa5a-zA-Z0-9_\\-（）()\\s]+$", message = "店铺名称包含非法字符")
    private String shopName;
    private String logo;
}
