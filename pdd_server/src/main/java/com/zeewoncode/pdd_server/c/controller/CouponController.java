package com.zeewoncode.pdd_server.c.controller;

import com.zeewoncode.pdd_server.c.service.CouponService;
import com.zeewoncode.result.PageResult;
import com.zeewoncode.result.Result;
import com.zeewoncode.vo.CouponVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
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
}
