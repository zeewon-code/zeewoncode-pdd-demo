package com.zeewoncode.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
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
    @Pattern(regexp = "^\\S+$", message = "昵称不能包含任何空白字符")
    @Size(min = 1, max = 10, message = "昵称长度在1到10个字符之间")
    private String title;
    @Pattern(regexp = "^\\S+$", message = "昵称不能包含任何空白字符")
    @Size(min = 1, max = 10, message = "昵称长度在1到10个字符之间")
    private String subtitle;
    private String mainImage;
    private List<String> images;
    private String details;

}
