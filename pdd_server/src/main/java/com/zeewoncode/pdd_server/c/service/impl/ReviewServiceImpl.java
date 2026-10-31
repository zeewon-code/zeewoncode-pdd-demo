package com.zeewoncode.pdd_server.c.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zeewoncode.constant.MessageConstant;
import com.zeewoncode.constant.RatingConstant;
import com.zeewoncode.context.BaseContext;
import com.zeewoncode.entity.*;
import com.zeewoncode.pdd_server.c.service.ReviewService;
import com.zeewoncode.pdd_server.mapper.OrderMapper;
import com.zeewoncode.pdd_server.mapper.ReviewMapper;
import com.zeewoncode.pdd_server.mapper.SpuMapper;
import com.zeewoncode.dto.ReviewCreateDTO;
import com.zeewoncode.dto.ReviewsListQueryDTO;
import com.zeewoncode.result.PageResult;
import com.zeewoncode.result.ReviewCreateResult;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ReviewServiceImpl implements ReviewService {

    @Autowired
    private ReviewMapper reviewMapper;
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private SpuMapper spuMapper;

    /**
     * 获取评价列表
     * @param spuId
     * @param req
     * @return
     */
    @Override
    public PageResult<Review> getReviewList(Integer spuId, ReviewsListQueryDTO req) {
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
        Map<Integer, Long> countMap = ratingList.stream()
                .collect(Collectors.groupingBy(r -> r.getRating(), Collectors.counting()));
        Map<String, Integer> ratingDist = new HashMap<>();
        ratingDist.put(RatingConstant.RATING_1, countMap.getOrDefault(1, 0L).intValue());
        ratingDist.put(RatingConstant.RATING_2, countMap.getOrDefault(2, 0L).intValue());
        ratingDist.put(RatingConstant.RATING_3, countMap.getOrDefault(3, 0L).intValue());
        ratingDist.put(RatingConstant.RATING_4, countMap.getOrDefault(4, 0L).intValue());
        ratingDist.put(RatingConstant.RATING_5, countMap.getOrDefault(5, 0L).intValue());
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

    /**
     * 发表评价
     * @param req
     * @return
     */
    @Override
    public ReviewCreateResult addReview(ReviewCreateDTO req) {
        Order order = orderMapper.selectOrderById(req.getOrderId());
        if (order == null) {
            throw new RuntimeException(MessageConstant.ORDER_NOT_EXIST);
        }
        if (order.getStatus() != 3) {
            throw new RuntimeException(MessageConstant.ORDER_STATUS_NOT_ALLOW_ADD_REVIEW);
        }
        ProductReview productReview = new ProductReview();
        BeanUtils.copyProperties(req, productReview);
        productReview.setUserId(BaseContext.getCurrentId().longValue());
        OrderItem orderItem = orderMapper.selectItemById(req.getOrderItemId());
        productReview.setSpuId(orderItem.getSpuId());
        productReview.setCreatedAt(LocalDateTime.now());
        reviewMapper.insert(productReview);
        ReviewCreateResult result = ReviewCreateResult.builder().reviewId(productReview.getId()).build();
        return result;
    }
}
