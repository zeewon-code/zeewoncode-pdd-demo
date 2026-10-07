package com.zeewoncode.pdd_server.mapper;

import com.zeewoncode.entity.Payment;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;

@Mapper
public interface PaymentMapper {

    /**
     * 插入支付信息
     * @param payment
     */
    @Insert("INSERT INTO payment (payment_no, order_id, user_id, amount, channel, status, paid_at, created_at ) " +
            "VALUES (#{paymentNo}, #{orderId}, #{userId}, #{amount}, #{channel}, #{status}, #{paidAt}, #{createdAt})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(Payment payment);
}
