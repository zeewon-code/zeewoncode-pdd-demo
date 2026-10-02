package com.zeewoncode.pdd_server.c.service;

import com.zeewoncode.entity.OrderCard;
import com.zeewoncode.entity.Review;
import com.zeewoncode.entity.ReviewStat;
import com.zeewoncode.req.ReviewsListQueryReq;
import com.zeewoncode.result.PageResult;

public interface ReviewService {
    /**
     * 获取评价列表
     * @param spuId
     * @param req
     * @return
     */
    PageResult<Review> getReviewList(Integer spuId, ReviewsListQueryReq req);

    /**
     * 获取商品评价统计
     * @param id
     * @return
     */
    ReviewStat getReviewStat(Integer id);

    /**
     * 获取待评价订单列表
     * @param page
     * @param size
     * @return
     */
    PageResult<OrderCard> getUnreviewedOrders(Integer page, Integer size, Integer userId);
}
