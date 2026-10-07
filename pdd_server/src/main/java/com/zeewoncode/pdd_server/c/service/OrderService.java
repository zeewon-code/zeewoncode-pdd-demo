package com.zeewoncode.pdd_server.c.service;

import com.zeewoncode.entity.OrderCreateResult;
import com.zeewoncode.entity.OrderPreviewResult;
import com.zeewoncode.req.OrderCreateReq;
import com.zeewoncode.req.OrderPreviewReq;

public interface OrderService {

    /**
     * 创建订单
     * @param req
     * @return
     */
    OrderCreateResult createOrder(OrderCreateReq req);

    /**
     * 订单结算预览
     * @param orderPreviewReq
     * @return
     */
    OrderPreviewResult previewOrder(OrderPreviewReq orderPreviewReq);
}
