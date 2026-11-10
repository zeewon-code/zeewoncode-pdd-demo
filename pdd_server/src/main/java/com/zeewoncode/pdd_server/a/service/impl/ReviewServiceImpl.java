package com.zeewoncode.pdd_server.a.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zeewoncode.entity.Review;
import com.zeewoncode.entity.Spu;
import com.zeewoncode.pdd_server.a.service.ReviewService;
import com.zeewoncode.pdd_server.mapper.ReviewMapper;
import com.zeewoncode.pdd_server.mapper.SpuMapper;
import com.zeewoncode.result.PageResult;
import com.zeewoncode.result.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReviewServiceImpl implements ReviewService {

    @Autowired
    private SpuMapper spuMapper;
    @Autowired
    private ReviewMapper reviewMapper;


    /**
     * 全平台评价列表
     * @param spuId
     * @param keyword
     * @param rating
     * @param page
     * @param size
     * @return
     */
    @Override
    public PageResult<Review> reviewList(Long spuId, String keyword, Integer rating, Integer page, Integer size) {
        if (spuId != null) {
            Spu spuById = spuMapper.getSpuById(spuId);
            if (spuById == null || spuById.getDeletedFlag() != 0) {
                throw new RuntimeException("商品不存在");
            }
        }
        PageHelper.startPage(page, size);
        List<Review> reviewList = reviewMapper.selectReviewListBySpuIdAndKeywordAndRating(spuId, keyword, rating);
        PageInfo<Review> pageInfo = new PageInfo<>(reviewList);
        if (reviewList.isEmpty()) {
            return PageResult.empty(pageInfo);
        }
        return PageResult.of(pageInfo, reviewList);
    }


    /**
     * 删除违规评价
     * @param id
     * @return
     */
    @Override
    public Result deleteReview(Long id) {
        if (reviewMapper.deleteById(id) > 0) {
            return Result.success("删除成功");
        } else {
            return Result.error("删除失败");
        }
    }
}
