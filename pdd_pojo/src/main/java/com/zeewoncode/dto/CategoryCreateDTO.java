package com.zeewoncode.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CategoryCreateDTO implements Serializable {

    @NotNull(message = "分类id不能为空")
    private Long parentId;
    @NotBlank(message = "分类名称不能为空")
    private String name;
    private Integer sort; // 排序基准，越小越靠前
}
