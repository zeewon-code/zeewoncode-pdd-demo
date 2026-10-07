package com.zeewoncode.pdd_server.mapper;

import com.zeewoncode.entity.Coupon;
import com.zeewoncode.entity.UserCoupon;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;

@Mapper
public interface CouponMapper {
    /**
     * 根据id查询优惠券
     * @param couponId
     * @return
     */
    @Select("SELECT * FROM coupon WHERE id = #{couponId} AND valid_from <= #{now} AND valid_to >= #{now}")
    Coupon selectById(Integer couponId, LocalDateTime now);


    /**
     * 根据id查询用户优惠券
     * @param userCouponId
     * @return
     */
    @Select("SELECT * FROM user_coupon WHERE id = #{userCouponId}")
    UserCoupon selectUserCouponById(Long userCouponId);
}
