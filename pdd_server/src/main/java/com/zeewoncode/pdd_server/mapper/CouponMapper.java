package com.zeewoncode.pdd_server.mapper;

import com.zeewoncode.entity.Coupon;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface CouponMapper {
    /**
     * 根据id查询优惠券
     * @param couponId
     * @return
     */
    @Select("SELECT * FROM coupon WHERE id = #{couponId} AND valid_from &lt; LocalDateTime.now() AND valid_to &gt; LocalDateTime.now()")
    Coupon selectById(Integer couponId);
}
