package com.zeewoncode.pdd_server.b.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zeewoncode.context.BaseContext;
import com.zeewoncode.entity.Merchant;
import com.zeewoncode.entity.Review;
import com.zeewoncode.entity.Spu;
import com.zeewoncode.entity.User;
import com.zeewoncode.pdd_server.b.service.ReviewService;
import com.zeewoncode.pdd_server.mapper.MerchantMapper;
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
    public PageResult<Review> getReviewList(Long spuId, Integer rating, Integer page, Integer size) {
        // 涉及的表：user， product_review
        Long userId = BaseContext.getCurrentId().longValue();
        Integer result = isMerchantAdmin(userId.intValue());
        if (result != 4) {
            throw new RuntimeException("当前用户没有权限进行相关操作");
        }
        // 如果前端没传spuId，就把店铺旗下的所有商品id查询出来
        List<Long> spuIds = null;
        if (spuId != null) {
            //前端传了spuId，校验该spu是否是属于当前店铺的
            Spu spuById = spuMapper.getSpuById(spuId);
            Merchant merchant = merchantMapper.selectMerchantByUserId(userId);
            if (spuById == null || !spuById.getMerchantId().equals(merchant.getId())) {
                throw new RuntimeException("当前店铺下不存在该商品");
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
     * 校验当前用户是否有资格进行相关操作
     * @return
     */
    public Integer isMerchantAdmin(Integer userId) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            return 1; // 1表示当前用户不存在
        }
        Merchant merchant = merchantMapper.selectMerchantByUserId(userId.longValue());
        if (merchant == null) {
            return 2; // 2表示当前用户没有绑定店铺
        }
        if (user.getUserType() != 2) {
            return 3; // 3表示当前用户不是店铺的管理员
        }
        return 4; // 正常
    }
}
