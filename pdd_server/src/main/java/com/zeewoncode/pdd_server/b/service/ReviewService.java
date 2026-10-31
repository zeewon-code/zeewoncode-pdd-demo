package com.zeewoncode.pdd_server.b.service;

import com.zeewoncode.entity.Review;
import com.zeewoncode.result.PageResult;

public interface ReviewService {
    /**
     * 本店商品评价列表
     * @param spuId
     * @param rating
     * @param page
     * @param size
     * @return
     */
    PageResult<Review> getReviewList(Long spuId, Integer rating, Integer page, Integer size);
}
