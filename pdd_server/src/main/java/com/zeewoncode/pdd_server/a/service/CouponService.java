package com.zeewoncode.pdd_server.a.service;

import com.zeewoncode.result.PageResult;
import com.zeewoncode.vo.CouponVO;

public interface CouponService {

    /**
     * 列出优惠券列表（含已领和未领）
     * @param page
     * @param size
     * @return
     */
    PageResult<CouponVO> listCoupons(Integer page, Integer size);
}
