package com.zeewoncode.pdd_server.c.service;

import com.zeewoncode.result.PageResult;
import com.zeewoncode.vo.CouponClaimVO;
import com.zeewoncode.vo.CouponOptionVO;
import com.zeewoncode.vo.CouponVO;
import com.zeewoncode.vo.UserCouponVO;

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
     * @param id
     * @return
     */
    CouponClaimVO claimCoupon(Long id);

    /**
     * 我的优惠券
     * @param status
     * @param page
     * @param size
     * @return
     */
    PageResult<UserCouponVO> getMyUserCoupons(Integer status, Integer page, Integer size);

    /**
     * 结算页可选券
     * @param totalAmount
     * @return
     */
    List<CouponOptionVO> getCouponOptions(Double totalAmount);
}
