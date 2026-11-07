package com.zeewoncode.pdd_server.a.service;

import com.zeewoncode.dto.CouponCreateDTO;
import com.zeewoncode.result.PageResult;
import com.zeewoncode.result.Result;
import com.zeewoncode.vo.CouponVO;

public interface CouponService {

    /**
     * 列出优惠券列表（含已领和未领）
     * @param page
     * @param size
     * @return
     */
    PageResult<CouponVO> listCoupons(Integer page, Integer size);

    /**
     * 创建优惠券
     * @param couponCreateDTO
     * @return
     */
    Result createCoupon(CouponCreateDTO couponCreateDTO);
}
