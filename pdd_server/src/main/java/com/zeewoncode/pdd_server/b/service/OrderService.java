package com.zeewoncode.pdd_server.b.service;

import com.zeewoncode.dto.ShipDTO;
import com.zeewoncode.entity.OrderCard;
import com.zeewoncode.entity.OrderDetail;
import com.zeewoncode.result.PageResult;
import com.zeewoncode.vo.ShipVO;

public interface OrderService {
    /**
     * 获取本店订单列表
     * @param status
     * @param page
     * @param size
     * @param userId
     * @return
     */
    PageResult<OrderCard> getOrderList(Integer status, Integer page, Integer size, Long userId);

    /**
     * 获取本店订单详情
     * @param orderId
     * @param userId
     * @return
     */
    OrderDetail getOrderDetail(Long orderId, Long userId);

    /**
     * 订单发货
     * @param orderId
     * @param dto
     * @param userId
     * @return
     */
    ShipVO ship(Long orderId, ShipDTO dto, Long userId);
}
