package com.zeewoncode.pdd_server.a.controller;

import com.zeewoncode.dto.CouponCreateDTO;
import com.zeewoncode.entity.CouponRecord;
import com.zeewoncode.result.PageResult;
import com.zeewoncode.result.Result;
import com.zeewoncode.vo.CouponVO;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * A端-优惠券管理
 */
@RestController
@RequestMapping("/api/a/coupons")
@Slf4j
public class CouponController {

    @Autowired
    private com.zeewoncode.pdd_server.a.service.CouponService couponService;

    /**
     * 列出优惠券列表（含已领和未领）
     * @param page
     * @param size
     * @return
     */
    @GetMapping
    public PageResult<CouponVO> listCoupons(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size
    ) {
        log.info("优惠券列表（含已领/剩余）: 页码{}, 每页数量{}", page, size);
        return couponService.listCoupons(page, size);
    }

    /**
     * 创建优惠券
     * @param couponCreateDTO
     * @return
     */
    @PostMapping
    public Result createCoupon(@RequestBody @Valid CouponCreateDTO couponCreateDTO) {
        log.info("创建优惠券：{}", couponCreateDTO);
        return couponService.createCoupon(couponCreateDTO);
    }

    /**
     * 获取优惠券领取/使用记录
     * @param id
     * @param page
     * @param size
     * @return
     */
    @GetMapping("/{id}/records")
    public PageResult<CouponRecord> getCouponRecord(
            @PathVariable Long id,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size
    ) {
        log.info("获取优惠券记录: {}", id);
        return couponService.getCouponRecord(id, page, size);
    }
}
