package com.zeewoncode.pdd_server.b.service;

import com.zeewoncode.entity.Review;
import com.zeewoncode.entity.ReviewStat;
import com.zeewoncode.result.PageResult;
import com.zeewoncode.result.Result;

public interface ReviewService {
    /**
     * 本店商品评价列表
     * @param spuId
     * @param rating
     * @param page
     * @param size
     * @param userId
     * @return
     */
    PageResult<Review> getReviewList(Long spuId, Integer rating, Integer page, Integer size, Long userId);

    /**
     * 本店商品评价统计
     * @param userId
     * @return
     */
    Result<ReviewStat> getReviewStat(Long userId);

}
