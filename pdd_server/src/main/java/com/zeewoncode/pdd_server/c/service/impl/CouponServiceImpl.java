package com.zeewoncode.pdd_server.c.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zeewoncode.entity.Coupon;
import com.zeewoncode.pdd_server.c.service.CouponService;
import com.zeewoncode.pdd_server.mapper.CouponMapper;
import com.zeewoncode.result.PageResult;
import com.zeewoncode.vo.CouponVO;
import org.springframework.stereotype.Service;

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
}
