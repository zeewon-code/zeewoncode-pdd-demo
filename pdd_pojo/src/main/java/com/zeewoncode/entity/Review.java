package com.zeewoncode.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 评价实体类
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Review implements Serializable {
    private Integer id;
    private String nickname;
    private String avator;
    private Integer rating;
    private String content;
    private List<String> images;
    private LocalDateTime createdAt;

}
