package com.zeewoncode.pdd_server.c.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zeewoncode.constant.RatingConstant;
import com.zeewoncode.entity.OrderCard;
import com.zeewoncode.entity.Review;
import com.zeewoncode.entity.ReviewStat;
import com.zeewoncode.pdd_server.c.service.ReviewService;
import com.zeewoncode.pdd_server.mapper.OrderMapper;
import com.zeewoncode.pdd_server.mapper.ReviewMapper;
import com.zeewoncode.req.ReviewsListQueryReq;
import com.zeewoncode.result.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ReviewServiceImpl implements ReviewService {

    @Autowired
    private ReviewMapper reviewMapper;
    @Autowired
    private OrderMapper orderMapper;

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

    /**
     * 获取商品评价统计
     * @param id
     * @return
     */
    @Override
    public ReviewStat getReviewStat(Integer id) {
        List<Review> ratingList =  reviewMapper.selectReviewListBySpuId(id);
        ReviewStat reviewStat = new ReviewStat();
        reviewStat.setTotal(ratingList.size());
        double ratingSum = ratingList.stream()
                .mapToDouble(Review::getRating)
                .sum();
        reviewStat.setAvgRating(ratingSum / ratingList.size());
        Map<String, Integer> ratingDist = new HashMap<>();
        ratingDist.put(RatingConstant.RATING_1, (int) ratingList.stream().
                filter(r -> Integer.valueOf(1).equals(r.getRating()))
                .count());
        ratingDist.put(RatingConstant.RATING_2, (int) ratingList.stream().
                filter(r -> Integer.valueOf(2).equals(r.getRating()))
                .count());
        ratingDist.put(RatingConstant.RATING_3, (int) ratingList.stream()
                .filter(r -> Integer.valueOf(3).equals(r.getRating()))
                .count());
        ratingDist.put(RatingConstant.RATING_4, (int) ratingList.stream()
                .filter(r -> Integer.valueOf(4).equals(r.getRating()))
                .count());
        ratingDist.put(RatingConstant.RATING_5, (int) ratingList.stream()
                .filter(r -> Integer.valueOf(5).equals(r.getRating()))
                .count());
        reviewStat.setRatingDist(ratingDist);
        return reviewStat;
    }

    /**
     * 获取待评价订单列表
     * @param page
     * @param size
     * @return
     */
    @Override
    public PageResult<OrderCard> getUnreviewedOrders(Integer page, Integer size, Integer userId) {
        PageHelper.startPage(page, size);
        List<OrderCard> orderList = orderMapper.selectUnreviewedOrdersPage(userId);
        PageInfo<OrderCard> pageInfo = new PageInfo<>(orderList);
        PageResult<OrderCard> result = new PageResult<>();
        result.setTotal((int) pageInfo.getTotal());
        result.setRecords(pageInfo.getList());
        result.setSize(pageInfo.getSize());
        result.setPage(pageInfo.getPageNum());
        return result;
    }
}
