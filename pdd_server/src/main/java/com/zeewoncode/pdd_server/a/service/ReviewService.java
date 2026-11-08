package com.zeewoncode.pdd_server.a.service;

import com.zeewoncode.entity.Review;
import com.zeewoncode.result.PageResult;

public interface ReviewService {

    /**
     * 全平台评价列表
     * @param spuId
     * @param keyword
     * @param rating
     * @param page
     * @param size
     * @return
     */
    PageResult<Review> reviewList(Long spuId, String keyword, Integer rating, Integer page, Integer size);
}
