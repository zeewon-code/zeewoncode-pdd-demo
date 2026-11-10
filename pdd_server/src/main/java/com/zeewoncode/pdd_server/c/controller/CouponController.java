package com.zeewoncode.pdd_server.c.controller;

import com.zeewoncode.pdd_server.c.service.CouponService;
import com.zeewoncode.pdd_server.security.LoginUser;
import com.zeewoncode.result.PageResult;
import com.zeewoncode.result.Result;
import com.zeewoncode.vo.CouponClaimVO;
import com.zeewoncode.vo.CouponOptionVO;
import com.zeewoncode.vo.CouponVO;
import com.zeewoncode.vo.UserCouponVO;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * C端-优惠券模块
 */
@RestController
@RequestMapping("/api/coupons")
@Slf4j
public class CouponController {

    @Autowired
    private com.zeewoncode.pdd_server.c.service.CouponService couponService;

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
    public Result<CouponClaimVO> claimCoupon(@PathVariable Long id, @AuthenticationPrincipal LoginUser loginUser) {
        log.info("领取优惠券:{}",id);
        CouponClaimVO couponClaimVO = couponService.claimCoupon(id, loginUser.getUserId());
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
            @RequestParam(defaultValue = "10") Integer size,
            @AuthenticationPrincipal LoginUser loginUser
    ) {
        log.info("获取我的优惠券:状态{}, 页码{}, 每页数量{}", status, page, size);
        PageResult<UserCouponVO> pageResult = couponService.getMyUserCoupons(status, page, size, loginUser.getUserId());
        return Result.success(pageResult);
    }

    /**
     * 结算页可选券
     * @param totalAmount
     * @return
     */
    @GetMapping("/user-coupons/options")
    public Result<List<CouponOptionVO>> getCouponOptions(@RequestParam Double totalAmount, @AuthenticationPrincipal LoginUser loginUser) {
        log.info("结算页可选券,总金额：{}", totalAmount);
        List<CouponOptionVO> couponOptionVOList = couponService.getCouponOptions(totalAmount, loginUser.getUserId());
        return Result.success(couponOptionVOList);
    }
}
