package com.zeewoncode.pdd_server.b.service;

import com.zeewoncode.entity.OrderCard;
import com.zeewoncode.entity.OrderDetail;
import com.zeewoncode.result.PageResult;

public interface OrderService {
    /**
     * 获取本店订单列表
     * @param status
     * @param page
     * @param size
     * @return
     */
    PageResult<OrderCard> getOrderList(Integer status, Integer page, Integer size);

    /**
     * 获取本店订单详情
     * @param orderId
     * @return
     */
    OrderDetail getOrderDetail(Long orderId);
}
