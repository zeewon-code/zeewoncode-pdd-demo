package com.zeewoncode.pdd_server.mapper;

import com.zeewoncode.entity.Order;
import com.zeewoncode.entity.OrderCard;
import com.zeewoncode.entity.OrderItem;
import com.zeewoncode.vo.OrderItemVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface OrderMapper {
    /**
     * 查询待评价订单
     * @return
     */
    List<OrderCard> selectUnreviewedOrdersPage(@Param("userId") Integer userId);

    /**
     * 根据订单id查询订单明细
     * @param orderId
     * @return
     */
    List<OrderItemVO> selectItemsByOrderId(@Param("orderId") Integer orderId);

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
}
