package com.zeewoncode.dto;

import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReviewCreateDTO implements Serializable {

    private Long orderId;
    private Long orderItemId;
    private Integer rating;
    @Pattern(regexp = "^\\S+$", message = "评论内容不能包含任何空白字符")
    private String content;
    private List<String> images;
}
