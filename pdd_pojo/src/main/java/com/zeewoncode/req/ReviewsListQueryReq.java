package com.zeewoncode.req;

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
public class ReviewsListQueryReq implements Serializable {

    private Integer rating; //评分筛选
    private Integer page = 1; // 默认第一页
    private Integer size = 10; // 默认每页10条
}
