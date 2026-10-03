package com.zeewoncode.pdd_server.c.service;

import com.zeewoncode.entity.OrderCreateResult;
import com.zeewoncode.req.OrderCreateReq;

public interface OrderService {

    /**
     * 创建订单
     * @param req
     * @return
     */
    OrderCreateResult createOrder(OrderCreateReq req);
}
