package com.zeewoncode.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;

@AllArgsConstructor
@Builder
@Data
@NoArgsConstructor
public class ShopApplyDTO implements Serializable {

    @NotBlank(message = "店铺名称不能为空")
    @Size(min = 1, max = 10, message = "店铺名称长度需在1-10字符之间")
    @Pattern(regexp = "^\\S+$", message = "昵称不能包含任何空白字符")
    private String shopName;
    @Pattern(
            regexp = "^https?://\\S+$",
            message = "头像URL必须以http://或https://开头，且不能包含空格"
    )
    @NotBlank(message = "店铺头像不能为空")
    private String logo;
}
