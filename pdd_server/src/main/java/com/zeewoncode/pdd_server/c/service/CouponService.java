package com.zeewoncode.pdd_server.c.service;

import com.zeewoncode.result.PageResult;
import com.zeewoncode.vo.CouponVO;

public interface CouponService {
    /**
     * 获取优惠券列表
     * @param page
     * @param size
     * @return
     */
    PageResult<CouponVO> getCoupons(Integer page, Integer size);
}
