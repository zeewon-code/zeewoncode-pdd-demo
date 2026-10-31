package com.zeewoncode.pdd_server.b.controller;


import com.zeewoncode.entity.Review;
import com.zeewoncode.result.PageResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * B端-评价模块
 */
@RestController
@RequestMapping("/api/b/reviews")
@Slf4j
public class ReviewController {
    @Autowired
    private com.zeewoncode.pdd_server.b.service.ReviewService reviewService;

    /**
     * 本店商品评价列表
     * @param spuId
     * @param rating
     * @param page
     * @param size
     * @return
     */
    @GetMapping
    public PageResult<Review> getReviewList(
            @RequestParam(required = false) Long spuId,
            @RequestParam(required = false) Integer rating,
            @RequestParam(required = false, defaultValue = "1") Integer page,
            @RequestParam(required = false, defaultValue = "10") Integer size
    ) {
        log.info("spuId:{}, rating:{}, page:{}, size:{}", spuId, rating, page, size);
        return reviewService.getReviewList(spuId, rating, page, size);
    }
}
