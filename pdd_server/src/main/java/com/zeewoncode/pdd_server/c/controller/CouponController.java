package com.zeewoncode.pdd_server.c.controller;

import com.zeewoncode.pdd_server.c.service.CouponService;
import com.zeewoncode.result.PageResult;
import com.zeewoncode.result.Result;
import com.zeewoncode.vo.CouponClaimVO;
import com.zeewoncode.vo.CouponVO;
import com.zeewoncode.vo.UserCouponVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * C端-优惠券模块
 */
@RestController
@RequestMapping("/api/coupons")
@Slf4j
public class CouponController {

    @Autowired
    private CouponService couponService;

    /**
     * 获取优惠券列表
     * @param page
     * @param size
     * @return
     */
    @GetMapping
    public Result<PageResult<CouponVO>> getCoupons(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        log.info("获取优惠券列表");
        PageResult<CouponVO> pageResult = couponService.getCoupons(page, size);
        return Result.success(pageResult);
    }

    /**
     * 领取优惠券
     * @param id
     * @return
     */
    @PostMapping("/{id}/claim")
    public Result<CouponClaimVO> claimCoupon(@PathVariable Long id) {
        log.info("领取优惠券:{}",id);
        CouponClaimVO couponClaimVO = couponService.claimCoupon(id);
        return Result.success(couponClaimVO);
    }

    /**
     * 我的优惠券
     * @param status
     * @param page
     * @param size
     * @return
     */
    @GetMapping("/user-coupons/me")
    public Result<PageResult<UserCouponVO>> getUserCoupons(
            @RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size
    ) {
        log.info("获取我的优惠券:状态{}, 页码{}, 每页数量{}", status, page, size);
        PageResult<UserCouponVO> pageResult = couponService.getMyUserCoupons(status, page, size);
        return Result.success(pageResult);
    }
}
