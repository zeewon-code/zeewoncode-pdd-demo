package com.zeewoncode.pdd_server.mapper;

import com.zeewoncode.entity.Order;
import com.zeewoncode.entity.OrderCard;
import com.zeewoncode.entity.OrderItem;
import com.zeewoncode.vo.OrderItemVO;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface OrderMapper {
    /**
     * 查询待评价订单
     * @return
     */
    List<OrderCard> selectUnreviewedOrdersPage(@Param("userId") Integer userId);

    /**
     * 根据订单id查询订单明细返回
     * @param orderId
     * @return
     */
    List<OrderItemVO> selectItemsVOByOrderId(@Param("orderId") Integer orderId);

    /**
     * 根据订单明细id查询订单明细
     * @param orderItemId
     * @return
     */
    @Select("SELECT * FROM order_item WHERE id = #{orderItemId}")
    OrderItem selectItemById(Long orderItemId);

    /**
     * 根据订单id查询订单
     * @param orderId
     * @return
     */
    @Select("SELECT * FROM orders WHERE id = #{orderId}")
    Order selectOrderById(Long orderId);

    /**
     * 插入订单
     * @param order
     */
    @Insert("INSERT INTO orders (order_no, user_id, merchant_id, address_id, " +
            "receiver_name, receiver_phone, receiver_address, total_amount, " +
            "discount_amount, payable_amount, payment_id, groupon_instance_id, " +
            "status, cancel_type, paid_at, completed_at) " +
            "VALUES (#{orderNo}, #{userId}, #{merchantId}, #{addressId}, " +
            "#{receiverName}, #{receiverPhone}, #{receiverAddress}, #{totalAmount}, " +
            "#{discountAmount}, #{payableAmount}, #{paymentId}, #{grouponInstanceId}, " +
            "#{status}, #{cancelType}, #{paidAt}, #{completedAt})")
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    void insert(Order order);

    /**
     * 插入订单明细
     * @param orderItems
     */
    void insertOrderItems(List<OrderItem> orderItems);

    /**
     * 更新订单
     * @param orderDb
     */
    void update(Order orderDb);


    /**
     * 根据订单id查询订单明细
     * @param orderId
     * @return
     */
    @Select("SELECT * FROM order_item WHERE order_id = #{orderId}")
    List<OrderItem> selectItemsByOrderId(Long orderId);

    /**
     * 根据订单id删除订单明细
     * @param orderId
     */
    @Delete("DELETE FROM order_item WHERE order_id = #{orderId}")
    void deleteByOrderId(Long orderId);
}
