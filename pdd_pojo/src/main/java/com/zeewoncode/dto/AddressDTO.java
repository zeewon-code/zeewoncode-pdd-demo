package com.zeewoncode.dto;



import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 收货地址请求参数
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AddressDTO implements Serializable {
    private Long id; // 地址id
    @NotBlank(message = "收件人名称不能为空")
    @Pattern(regexp = "^\\S+$", message = "收件人名称不能包含任何空白字符")
    @Size(min = 1, max = 10, message = "收件人名称长度在1到10个字符之间")
    private String receiverName; // 收件人名称
    @NotBlank(message = "电话不能为空")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "电话格式不正确")
    private String receiverPhone; // 收件人电话
    @NotBlank(message = "省份不能为空")
    @Pattern(regexp = "^\\S+$", message = "省份不能包含任何空白字符")
    private String province; // 省份
    @NotBlank(message = "城市不能为空")
    @Pattern(regexp = "^\\S+$", message = "城市不能包含任何空白字符")
    private String city;
    @NotBlank(message = "区域不能为空")
    @Pattern(regexp = "^\\S+$", message = "区域不能包含任何空白字符")
    private String district;
    @NotBlank(message = "详细地址不能为空")
    @Pattern(regexp = "^\\S+$", message = "详细地址不能包含任何空白字符")
    private String detail; // 详细地址
    private Boolean isDefault; // 是否默认地址

}
