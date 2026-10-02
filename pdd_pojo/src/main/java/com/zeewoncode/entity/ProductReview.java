package com.zeewoncode.entity;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductReview {

    private Long id;
    private Long orderId;
    private Long orderItemId;
    private Long userId;
    private Long spuId;
    private Integer rating;
    private String content;
    private String images;
    private LocalDateTime createdAt;

}
