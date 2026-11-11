package com.zeewoncode.pdd_server.c.service;

import com.zeewoncode.entity.OrderCard;
import com.zeewoncode.entity.Review;
import com.zeewoncode.entity.ReviewStat;
import com.zeewoncode.dto.ReviewCreateDTO;
import com.zeewoncode.dto.ReviewsListQueryDTO;
import com.zeewoncode.result.PageResult;
import com.zeewoncode.result.ReviewCreateResult;

public interface ReviewService {
    /**
     * 获取评价列表
     * @param spuId
     * @param req
     * @return
     */
    PageResult<Review> getReviewList(Long spuId, ReviewsListQueryDTO req);

    /**
     * 获取商品评价统计
     * @param spuId
     * @return
     */
    ReviewStat getReviewStat(Long spuId);

    /**
     * 获取待评价订单列表
     * @param page
     * @param size
     * @param userId
     * @return
     */
    PageResult<OrderCard> getUnreviewedOrders(Integer page, Integer size, Long userId);

    /**
     * 发表评价
     * @param req
     * @param userId
     * @return
     */
    ReviewCreateResult addReview(ReviewCreateDTO req, Long userId);
}
