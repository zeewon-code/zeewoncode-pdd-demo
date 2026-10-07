package com.zeewoncode.pdd_server.mapper;

import com.zeewoncode.entity.Coupon;
import com.zeewoncode.entity.UserCoupon;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

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
    @Select("SELECT * FROM user_coupon WHERE id = #{userCouponId} AND status = 0")
    UserCoupon selectUserCouponById(Long userCouponId);

    /**
     * 更新用户优惠券状态和使用时间
     * @param userCoupon
     */
    @Update("UPDATE user_coupon SET status = #{status}, used_at = #{usedAt} WHERE user_id = #{userId} AND order_id = #{orderId}")
    void updateUserCouponStatusAndUsedAt(UserCoupon userCoupon);

    /**
     * 根据订单id查询用户优惠券
     * @param orderId
     * @return
     */
    @Select("SELECT * FROM user_coupon WHERE order_id = #{orderId} AND user_id = #{userId}")
    UserCoupon selectByOrderId(Long orderId, Long userId);


    /**
     * 更新用户优惠券
     * @param userCoupon
     */
    void updateUserCoupon(UserCoupon userCoupon);


    /**
     * 根据用户id和订单id查询用户优惠券
     * @param userId
     * @param orderId
     * @return
     */
    @Select("SELECT * FROM user_coupon WHERE user_id = #{userId} AND order_id = #{orderId}")
    UserCoupon selectUserCouponByUserIdAndOrderId(Long userId, Long orderId);

    /**
     * 更新用户优惠券订单id为空
     * @param userCoupon1
     */
    @Update("UPDATE user_coupon SET order_id = NULL WHERE id = #{id}")
    void updateUserCouponSetOrderIdToNull(UserCoupon userCoupon1);
}
