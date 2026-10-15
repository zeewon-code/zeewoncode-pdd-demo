package com.zeewoncode.pdd_server.c.service;

import com.zeewoncode.entity.*;
import com.zeewoncode.req.OrderCreateReq;
import com.zeewoncode.req.OrderPreviewReq;
import com.zeewoncode.result.PageResult;

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

    /**
     * 取消待付款订单
     * @param id
     */
    void cancelOrderPendingPayMent(Integer id);

    /**
     * 获取订单支付倒计时
     * @param id
     * @return
     */
    CountdownResult getCountdown(Integer id);

    /**
     * 订单支付
     * @param id
     * @return
     */
    PayResult orderPay(Long id);

    /**
     * 获取订单列表
     * @param status
     * @param page
     * @param size
     * @return
     */
    PageResult<OrderCard> getOrderList(Integer status, Integer page, Integer size);

    /**
     * 获取订单详情
     * @param id
     * @return
     */
    OrderDetail getOrderDetail(Integer id);
}
