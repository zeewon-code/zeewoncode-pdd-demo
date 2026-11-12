package com.zeewoncode.pdd_server.a.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zeewoncode.dto.CouponCreateDTO;
import com.zeewoncode.entity.Coupon;
import com.zeewoncode.entity.CouponRecord;
import com.zeewoncode.pdd_server.a.service.CouponService;
import com.zeewoncode.pdd_server.mapper.CouponMapper;
import com.zeewoncode.result.PageResult;
import com.zeewoncode.result.Result;
import com.zeewoncode.vo.CouponVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class CouponServiceImpl implements CouponService {

    @Autowired
    private CouponMapper couponMapper;


     /**
     * 列出优惠券列表（含已领和未领）
     * @param page
     * @param size
     * @return
     */
    @Override
    public PageResult<CouponVO> listCoupons(Integer page, Integer size) {
        PageHelper.startPage(page, size);
        List<CouponVO> couponVOList = couponMapper.selectCoupons();
        PageInfo<CouponVO> couponVOPageInfo = new PageInfo<>(couponVOList);
        if (couponVOList.isEmpty()) {
            return PageResult.empty(couponVOPageInfo);
        }
        return PageResult.of(couponVOPageInfo, couponVOList);
    }

    /**
     * 创建优惠券
     * @param couponCreateDTO
     * @return
     */
    @Override
    public Result createCoupon(CouponCreateDTO couponCreateDTO) {
        // 校验优惠券金额（两种情况）
        Integer type = couponCreateDTO.getType();
        BigDecimal conditionAmountDto = couponCreateDTO.getConditionAmount();
        BigDecimal discountAmountDto = couponCreateDTO.getDiscountAmount();
        BigDecimal discountRateDto = couponCreateDTO.getDiscountRate();
        // 1. 如果是满减券，满减门槛必须大于满减金额
        if (type == 1) {
            if (conditionAmountDto == null || discountAmountDto == null) {
                return Result.error("优惠券满减金额或优惠金额未输入，请重新输入");
            }
            BigDecimal conditionAmount = conditionAmountDto.setScale(2, RoundingMode.HALF_UP);
            BigDecimal discountAmount = discountAmountDto.setScale(2, RoundingMode.HALF_UP);
            if (conditionAmount.compareTo(discountAmount) <= 0) {
                return Result.error("优惠券满减门槛必须大于满减金额，请重新输入");
            }
        }
        // 2. 如果是折扣券，折扣金额必须大于0
        if (type == 2) {
            if (discountRateDto == null) {
                return Result.error("优惠券折扣未输入，请重新输入");
            }
        }
        if (couponCreateDTO.getValidFrom().isAfter(couponCreateDTO.getValidTo())) {
            return Result.error("优惠券生效时间必须早于过期时间生效");
        }
        Coupon coupon = new Coupon();
        BeanUtils.copyProperties(couponCreateDTO, coupon);
        coupon.setIssued(0);
        coupon.setCreatedAt(LocalDateTime.now());
        if (couponMapper.insert(coupon) > 0) {
            return Result.success();
        } else {
            return Result.error("创建优惠券失败");
        }
    }

    /**
     * 获取优惠券领取/使用记录
     * @param couponId
     * @param page
     * @param size
     * @return
     */
    @Override
    public PageResult<CouponRecord> getCouponRecord(Long couponId, Integer page, Integer size) {
        PageHelper.startPage(page, size);
        List<CouponRecord> records = couponMapper.selectCouponRecord(couponId);
        PageInfo<CouponRecord> info = new PageInfo<>(records);
        if (records.isEmpty()) {
            return PageResult.empty(info);
        }
        return PageResult.of(info, records);
    }
}
