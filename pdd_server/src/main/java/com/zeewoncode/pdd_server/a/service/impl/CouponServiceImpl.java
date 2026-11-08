package com.zeewoncode.pdd_server.a.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zeewoncode.context.BaseContext;
import com.zeewoncode.dto.CouponCreateDTO;
import com.zeewoncode.entity.Coupon;
import com.zeewoncode.entity.CouponRecord;
import com.zeewoncode.entity.User;
import com.zeewoncode.pdd_server.a.service.CouponService;
import com.zeewoncode.pdd_server.mapper.CouponMapper;
import com.zeewoncode.pdd_server.mapper.UserMapper;
import com.zeewoncode.result.PageResult;
import com.zeewoncode.result.Result;
import com.zeewoncode.vo.CouponVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CouponServiceImpl implements CouponService {

    @Autowired
    private CouponMapper couponMapper;
    @Autowired
    private UserMapper userMapper;

     /**
     * 列出优惠券列表（含已领和未领）
     * @param page
     * @param size
     * @return
     */
    @Override
    public PageResult<CouponVO> listCoupons(Integer page, Integer size) {
        Long userId = BaseContext.getCurrentId().longValue();
        if (!isAdmin(userId)) {
            throw new RuntimeException("没有此权限进行查看");
        }
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
        Long userId = BaseContext.getCurrentId().longValue();
        if (!isAdmin(userId)) {
            throw new RuntimeException("没有此权限进行查看");
        }
        if (couponCreateDTO.getConditionAmount() != null && couponCreateDTO.getDiscountAmount() != null) {
            if (couponCreateDTO.getConditionAmount() <= couponCreateDTO.getDiscountAmount()) {
                return Result.error("优惠券满减门槛必须大于满减金额");
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
        Long userId = BaseContext.getCurrentId().longValue();
        if (!isAdmin(userId)) {
            throw new RuntimeException("没有此权限进行查看");
        }
        PageHelper.startPage(page, size);
        List<CouponRecord> records = couponMapper.selectCouponRecord(couponId);
        PageInfo<CouponRecord> info = new PageInfo<>(records);
        if (records.isEmpty()) {
            return PageResult.empty(info);
        }
        return PageResult.of(info, records);
    }

    // 校验当前用户是否为管理员
    private boolean isAdmin(Long userId) {
        User user = userMapper.selectById(userId.intValue());
        if (user == null || user.getUserType() != 9
                || user.getStatus() != 1 || user.getDeletedFlag() != 0) {
            return false;
        }
        return true;
    }
}
