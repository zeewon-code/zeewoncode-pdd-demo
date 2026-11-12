package com.zeewoncode.pdd_server.a.controller;

import com.zeewoncode.entity.Review;
import com.zeewoncode.result.PageResult;
import com.zeewoncode.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * A端-评价监管
 */
@RestController
@RequestMapping("/api/a/reviews")
@Slf4j
public class ReviewController {

    @Autowired
    private com.zeewoncode.pdd_server.a.service.ReviewService reviewService;

    /**
     * 全平台评价列表
     * @param spuId
     * @param keyword
     * @param rating
     * @param page
     * @param size
     * @return
     */
    @GetMapping
    public Result<PageResult<Review>> reviewList(
            @RequestParam(required = false) Long spuId,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer rating,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size
    ) {
        log.info("全平台评价列表：{},{},{}", spuId, keyword, rating);
        PageResult<Review> result = reviewService.reviewList(spuId, keyword, rating, page, size);
        return Result.success(result);
    }

    /**
     * 删除违规评价
     * @param id
     * @return
     */
    @DeleteMapping("/{id}")
    public Result deleteReview(@PathVariable Long id) {
        log.info("删除评价：{}", id);
        return reviewService.deleteReview(id);
    }
}
