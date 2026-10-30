package com.zeewoncode.pdd_server.mapper;

import com.zeewoncode.entity.*;
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
    List<OrderItemVO> selectItemsVOByOrderId(Long orderId);

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
     * @param order
     */
    int update(Order order);


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

    /**
     * 根据用户id和订单状态查询订单列表
     * @param userId
     * @param status
     * @param merchantId
     * @return
     */
    List<OrderCard> selectOrderList(Long userId, Integer status, Long merchantId);

    /**
     * 根据用户id和订单id查询订单详情
     * @param userId
     * @param id
     * @return
     */
    OrderDetail selectOrderDetail(Long userId, Integer id);

    /**
     * 根据订单id查询收货人信息
     * @param id
     * @return
     */
    @Select("SELECT receiver_name as name, receiver_phone as phone, receiver_address as address " +
            "FROM orders " +
            "WHERE id = #{id}")
    Receiver selectReceiverByOrderId(Integer id);

    /**
     * 根据团购实例id和用户id查询订单
     * @param instanceIds
     * @param userId
     * @param leaderUserIds
     * @return
     */
    List<Order> selectOrderByInstanceIdsAndUserIds(List<Long> instanceIds, Long userId, List<Long> leaderUserIds);

    /**
     * 根据团购实例id和用户id更新订单状态和取消类型
     */
    void updateOrderStatusAndCancelTypeByInstanceIdAndUserId(Long instanceId, Long userId, Integer status, Integer cancelType);

    /**
     * 根据订单id列表查询订单商品数量
     * @param orderIds
     * @return
     */
    Integer selectQuantityByOrderIds(List<Long> orderIds);


    /**
     * 根据团购实例id和用户id查询订单id列表
     * @param instanceId
     * @param userId
     * @return
     */
    List<Long> selectOrderIdsByInstanceIdAndUserId(Long instanceId, Long userId);

    /**
     * 根据团购实例id列表查询订单id列表
     * @param instanceIds
     * @return
     */
    List<Long> selectOrderIdsByInstanceIds(List<Long> instanceIds);

    /**
     * 根据团购实例id列表更新订单状态和取消类型
     * @param instanceIds
     * @param status
     * @param cancelType
     * @return
     */
    int updateOrderStatusAndCancelTypeByInstanceIds(List<Long> instanceIds, int status, int cancelType);



}
