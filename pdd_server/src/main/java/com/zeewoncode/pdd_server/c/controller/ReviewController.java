package com.zeewoncode.pdd_server.c.controller;

import com.zeewoncode.entity.Review;
import com.zeewoncode.pdd_server.c.service.ReviewService;
import com.zeewoncode.req.ReviewsListQueryReq;
import com.zeewoncode.result.PageResult;
import com.zeewoncode.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
