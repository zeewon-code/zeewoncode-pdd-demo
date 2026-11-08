package com.zeewoncode.pdd_server.a.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zeewoncode.context.BaseContext;
import com.zeewoncode.entity.ProductReview;
import com.zeewoncode.entity.Review;
import com.zeewoncode.entity.Spu;
import com.zeewoncode.entity.User;
import com.zeewoncode.pdd_server.a.service.ReviewService;
import com.zeewoncode.pdd_server.mapper.ReviewMapper;
import com.zeewoncode.pdd_server.mapper.SpuMapper;
import com.zeewoncode.pdd_server.mapper.UserMapper;
import com.zeewoncode.result.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReviewServiceImpl implements ReviewService {

    @Autowired
    private UserMapper userMapper;
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
        Long userId = BaseContext.getCurrentId().longValue();
        if (!isAdmin(userId)) {
            throw new RuntimeException("没有此权限进行查看");
        }
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

    // 校验当前用户是否为管理员
    private boolean isAdmin(Long userId) {
        User user = userMapper.selectById(userId.intValue());
        if (user == null || user.getUserType() != 9
                || user.getStatus() != 1 || user.getDeletedFlag() != 0) {
            return false;
        }
        return true;
    }
}
