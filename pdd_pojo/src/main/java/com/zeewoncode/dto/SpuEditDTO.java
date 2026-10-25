package com.zeewoncode.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SpuEditDTO implements Serializable {

    @NotNull(message = "分类ID不能为空")
    private Long categoryId;
    @NotBlank(message = "标题不能为空")
    private String title;
    private String subtitle;
    private String mainImage;
    private List<String> images;
    private String details;

}
