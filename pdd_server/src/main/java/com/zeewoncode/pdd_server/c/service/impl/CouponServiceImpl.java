package com.zeewoncode.pdd_server.c.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zeewoncode.context.BaseContext;
import com.zeewoncode.entity.Coupon;
import com.zeewoncode.entity.UserCoupon;
import com.zeewoncode.pdd_server.c.service.CouponService;
import com.zeewoncode.pdd_server.mapper.CouponMapper;
import com.zeewoncode.result.PageResult;
import com.zeewoncode.vo.CouponClaimVO;
import com.zeewoncode.vo.CouponOptionVO;
import com.zeewoncode.vo.CouponVO;
import com.zeewoncode.vo.UserCouponVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CouponServiceImpl implements CouponService {

    private final CouponMapper couponMapper;

    public CouponServiceImpl(CouponMapper couponMapper) {
        this.couponMapper = couponMapper;
    }

    /**
     * 获取优惠券列表
     * @param page
     * @param size
     * @return
     */
    @Override
    public PageResult<CouponVO> getCoupons(Integer page, Integer size) {
        // id,title,type,conditionAmount, discountAmount, discountRate, validTo, remain
        PageHelper.startPage(page, size);
        // 查出有效期内未发完的券
        List<CouponVO> voList =  couponMapper.selectCouponsByValidTime(LocalDateTime.now());
        PageInfo<CouponVO> couponVOPageInfo = new PageInfo<>(voList);
        if (voList.isEmpty()) {
            return PageResult.empty(couponVOPageInfo);
        }
        PageResult<CouponVO> couponVOPageResult = PageResult.of(couponVOPageInfo, voList);
        return couponVOPageResult;

    }

    /**
     * 领取优惠券
     * @param id
     * @return
     */
    @Override
    @Transactional
    public CouponClaimVO claimCoupon(Long id) {
        Long userId = BaseContext.getCurrentId().longValue();
        // 判断用户是否已经领取过该优惠券
        if (couponMapper.selectUserCouponByCouponId(id, userId) != null) {
            throw new RuntimeException("您已经领取过该优惠券，请勿重复领取");
        }
        // 根据优惠券id查询优惠券是否存在
        Coupon coupon = couponMapper.selectById(id, LocalDateTime.now());
        if (coupon == null) {
            throw new RuntimeException("优惠券不存在");
        }
        // 根据优惠券id对优惠券已领数量+1，每人限领一张
        int i = couponMapper.updateCouponCountById(id);
        if (i <= 0) throw new RuntimeException("优惠券领取失败");
        // 插入用户优惠券记录
        UserCoupon userCoupon = UserCoupon.builder()
                .userId(userId)
                .couponId(id)
                .status(0)
                .build();
        couponMapper.insertUserCoupon(userCoupon);
        Long userCouponId = userCoupon.getId();
        if (userCouponId == null) return null;
        CouponClaimVO vo = CouponClaimVO.builder()
                .userCouponId(userCouponId)
                .build();
        return vo;
    }

    /**
     * 获取我的优惠券列表
     * @param status
     * @param page
     * @param size
     * @return
     */
    @Override
    public PageResult<UserCouponVO> getMyUserCoupons(Integer status, Integer page, Integer size) {
        Long userId = BaseContext.getCurrentId().longValue();
        PageHelper.startPage(page, size);
        List<UserCouponVO> voList = couponMapper.selectMyUserCoupons(userId, status);
        PageInfo<UserCouponVO> userCouponVOPageInfo = new PageInfo<>(voList);
        if (voList.isEmpty()) {
            return PageResult.empty(userCouponVOPageInfo);
        }
        for (UserCouponVO vo : voList) {
            if (vo.getStatus() == 0) {
                vo.setUsable(1);
            } else vo.setUsable(0);
        }
        return PageResult.of(userCouponVOPageInfo, voList);
    }

    /**
     * 结算页可选券
     * @param totalAmount
     * @return
     */
    @Override
    public List<CouponOptionVO> getCouponOptions(Double totalAmount) {
        // 过滤满减门槛不满足、已过期或已使用的券。
        List<CouponOptionVO> couponOptionVOList = couponMapper.selectCouponOptions(totalAmount, LocalDateTime.now(), BaseContext.getCurrentId().longValue());
        return couponOptionVOList;
    }
}
