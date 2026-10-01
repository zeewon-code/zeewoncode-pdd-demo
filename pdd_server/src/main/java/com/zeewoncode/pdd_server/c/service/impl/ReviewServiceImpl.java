package com.zeewoncode.pdd_server.c.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zeewoncode.entity.Review;
import com.zeewoncode.pdd_server.c.service.ReviewService;
import com.zeewoncode.pdd_server.mapper.ReviewMapper;
import com.zeewoncode.req.ReviewsListQueryReq;
import com.zeewoncode.result.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReviewServiceImpl implements ReviewService {

    @Autowired
    private ReviewMapper reviewMapper;

    /**
     * 获取评价列表
     * @param spuId
     * @param req
     * @return
     */
    @Override
    public PageResult<Review> getReviewList(Integer spuId, ReviewsListQueryReq req) {
        PageHelper.startPage(req.getPage(), req.getSize());
        List<Review> reviewList = reviewMapper.selectReviewList(spuId, req);
        PageInfo<Review> pageInfo = new PageInfo<>(reviewList);
        PageResult<Review> result = new PageResult<>();
        result.setTotal((int) pageInfo.getTotal());
        result.setRecords(pageInfo.getList());
        result.setSize(pageInfo.getSize());
        result.setPage(pageInfo.getPageNum());
        return result;
    }
}
