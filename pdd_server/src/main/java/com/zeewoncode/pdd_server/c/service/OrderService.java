package com.zeewoncode.pdd_server.c.service;

import com.zeewoncode.entity.*;
import com.zeewoncode.dto.OrderCreateDTO;
import com.zeewoncode.dto.OrderPreviewDTO;
import com.zeewoncode.result.PageResult;
import com.zeewoncode.vo.CountdownVO;
import com.zeewoncode.vo.OrderCreateVO;
import com.zeewoncode.vo.OrderPreviewVO;
import com.zeewoncode.vo.PayVO;

public interface OrderService {

    /**
     * 创建订单
     * @param req
     * @param userId
     * @return
     */
    OrderCreateVO createOrder(OrderCreateDTO req, Long userId);

    /**
     * 订单结算预览
     * @param orderPreviewDTO
     * @return
     */
    OrderPreviewVO previewOrder(OrderPreviewDTO orderPreviewDTO);

    /**
     * 取消待付款订单
     * @param orderId
     * @param userId
     */
    void cancelOrderPendingPayMent(Long orderId, Long userId);

    /**
     * 获取订单支付倒计时
     * @param orderId
     * @return
     */
    CountdownVO getCountdown(Long orderId);

    /**
     * 订单支付
     * @param orderId
     * @param userId
     * @return
     */
    PayVO orderPay(Long orderId, Long userId);

    /**
     * 获取订单列表
     * @param status
     * @param page
     * @param size
     * @param userId
     * @return
     */
    PageResult<OrderCard> getOrderList(Integer status, Integer page, Integer size, Long userId);

    /**
     * 获取订单详情
     * @param orderId
     * @param userId
     * @return
     */
    OrderDetail getOrderDetail(Long orderId, Long userId);
}
