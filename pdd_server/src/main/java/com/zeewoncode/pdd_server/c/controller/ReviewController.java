package com.zeewoncode.pdd_server.c.controller;

import com.zeewoncode.context.BaseContext;
import com.zeewoncode.entity.OrderCard;
import com.zeewoncode.entity.Review;
import com.zeewoncode.entity.ReviewStat;
import com.zeewoncode.pdd_server.c.service.ReviewService;
import com.zeewoncode.req.ReviewsListQueryReq;
import com.zeewoncode.result.PageResult;
import com.zeewoncode.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * C端-评价模块
 */
@RestController
@RequestMapping("/api")
@Slf4j
public class ReviewController {

    @Autowired
    private ReviewService reviewService;


    /**
     * 获取评价列表
     * @param id
     * @param req
     * @return
     */
    @GetMapping("/spu/{id}/reviews")
    public Result<PageResult<Review>> getReviewList(@PathVariable Integer id,
                                                    ReviewsListQueryReq req) {
        log.info("获取评价列表:{},{}", id, req);
        return Result.success(reviewService.getReviewList(id, req));
    }

    /**
     * 获取评价统计
     * @param id
     * @return
     */
    @GetMapping("/spu/{id}/reviews/stat")
    public Result<ReviewStat> getReviewStat(@PathVariable Integer id) {
        log.info("统计商品id：{}的商品评分分布", id);
        ReviewStat reviewStat = reviewService.getReviewStat(id);
        return Result.success(reviewStat);
    }

    /**
     * 获取待评价订单列表
     * @param page
     * @param size
     * @return
     */
    @GetMapping("/orders/unreviewed")
    public Result<PageResult<OrderCard>> getUnreviewedOrders(@RequestParam(defaultValue = "1") Integer page,
                                                             @RequestParam(defaultValue = "10") Integer size) {
        log.info("获取待评价订单列表");
        Integer userId = BaseContext.getCurrentId();
        PageResult<OrderCard> unReviewedOrders = reviewService.getUnreviewedOrders(page, size, userId);
        return Result.success(unReviewedOrders);
    }
}
