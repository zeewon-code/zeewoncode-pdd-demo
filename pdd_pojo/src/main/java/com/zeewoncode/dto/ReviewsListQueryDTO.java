package com.zeewoncode.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 评价列表查询请求参数
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReviewsListQueryDTO implements Serializable {

    private Integer rating; //评分筛选
    @Builder.Default
    private Integer page = 1; // 默认第一页
    @Builder.Default
    private Integer size = 10; // 默认每页10条
}
