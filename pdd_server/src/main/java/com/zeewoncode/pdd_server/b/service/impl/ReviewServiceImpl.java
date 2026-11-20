package com.zeewoncode.pdd_server.b.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zeewoncode.constant.ErrorCode;
import com.zeewoncode.exception.BaseException;
import com.zeewoncode.constant.RatingConstant;
import com.zeewoncode.entity.*;
import com.zeewoncode.pdd_server.b.service.ReviewService;
import com.zeewoncode.pdd_server.mapper.MerchantMapper;
import com.zeewoncode.pdd_server.mapper.ReviewMapper;
import com.zeewoncode.pdd_server.mapper.SpuMapper;
import com.zeewoncode.result.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ReviewServiceImpl implements ReviewService {

    @Autowired
    private MerchantMapper merchantMapper;
    @Autowired
    private SpuMapper spuMapper;
    @Autowired
    private ReviewMapper reviewMapper;


    /**
     * 本店商品评价列表
     * @param spuId
     * @param rating
     * @param page
     * @param size
     * @return
     */
    @Override
    public PageResult<Review> getReviewList(Long spuId, Integer rating, Integer page,
                                            Integer size, Long userId) {
        // 涉及的表：user， product_review
        // 如果前端没传spuId，就把店铺旗下的所有商品id查询出来
        List<Long> spuIds = null;
        if (spuId != null) {
            //前端传了spuId，校验该spu是否是属于当前店铺的
            Spu spuById = spuMapper.getSpuById(spuId);
            Merchant merchant = merchantMapper.selectMerchantByUserId(userId);
            if (spuById == null || !spuById.getMerchantId().equals(merchant.getId()) || spuById.getDeletedFlag() != 0) {
                throw new BaseException(ErrorCode.SPU_NOT_BELONG_TO_MERCHANT);
            }
            spuIds = List.of(spuId);
        } else {
            Merchant merchant = merchantMapper.selectMerchantByUserId(userId);
            List<Spu> spus = spuMapper.selectSpuByMerchantId(merchant.getId(), null, null, null);
            spuIds = spus.stream().map(Spu::getId).toList();
        }
        PageHelper.startPage(page, size);
        List<Review> reviewList = reviewMapper.selectReviewListBySpuIds(spuIds, rating);
        PageInfo<Review> pageInfo = new PageInfo<>(reviewList);
        if (pageInfo.getList() == null || pageInfo.getList().isEmpty()) {
            return PageResult.empty(pageInfo);
        }
        return PageResult.of(pageInfo, pageInfo.getList());
    }

    /**
     * 本店商品评价统计
     * @return
     */
    @Override
    public ReviewStat getReviewStat(Long userId) {
        Merchant merchant = merchantMapper.selectMerchantByUserId(userId);
        List<Spu> spus = spuMapper.selectSpuByMerchantId(merchant.getId(), null, null, null);
        List<Long> spuIds = spus.stream().map(Spu::getId).toList();
        List<Integer> ratings = reviewMapper.selectRatingBySpuIds(spuIds);
        ReviewStat reviewStat = new ReviewStat();
        reviewStat.setTotal(ratings.size());
        double ratingSum = ratings.stream()
                .mapToDouble(Integer::doubleValue)
                .sum();
        reviewStat.setAvgRating(ratingSum / ratings.size());
        Map<Integer, Long> countMap = ratings.stream()
                .collect(Collectors.groupingBy(r -> r, Collectors.counting()));
        Map<String, Long> ratingDist = new HashMap<>();
        ratingDist.put(RatingConstant.RATING_1, countMap.getOrDefault(1, 0L));
        ratingDist.put(RatingConstant.RATING_2, countMap.getOrDefault(2, 0L));
        ratingDist.put(RatingConstant.RATING_3, countMap.getOrDefault(3, 0L));
        ratingDist.put(RatingConstant.RATING_4, countMap.getOrDefault(4, 0L));
        ratingDist.put(RatingConstant.RATING_5, countMap.getOrDefault(5, 0L));
        reviewStat.setRatingDist(ratingDist);
        return reviewStat;
    }
}
