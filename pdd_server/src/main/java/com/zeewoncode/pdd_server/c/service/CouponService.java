package com.zeewoncode.pdd_server.c.service;

import com.zeewoncode.result.PageResult;
import com.zeewoncode.vo.CouponClaimVO;
import com.zeewoncode.vo.CouponOptionVO;
import com.zeewoncode.vo.CouponVO;
import com.zeewoncode.vo.UserCouponVO;

import java.math.BigDecimal;
import java.util.List;

public interface CouponService {
    /**
     * 获取优惠券列表
     * @param page
     * @param size
     * @return
     */
    PageResult<CouponVO> getCoupons(Integer page, Integer size);

    /**
     * 领取优惠券
     * @param CouponId
     * @param userId
     * @return
     */
    CouponClaimVO claimCoupon(Long CouponId, Long userId);

    /**
     * 我的优惠券
     * @param status
     * @param page
     * @param size
     * @param userId
     * @return
     */
    PageResult<UserCouponVO> getMyUserCoupons(Integer status, Integer page, Integer size, Long userId);

    /**
     * 结算页可选券
     * @param totalAmount
     * @param userId
     * @return
     */
    List<CouponOptionVO> getCouponOptions(BigDecimal totalAmount, Long userId);
}
