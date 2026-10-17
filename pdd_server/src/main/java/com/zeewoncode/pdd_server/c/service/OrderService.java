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
     * @return
     */
    OrderCreateVO createOrder(OrderCreateDTO req);

    /**
     * 订单结算预览
     * @param orderPreviewDTO
     * @return
     */
    OrderPreviewVO previewOrder(OrderPreviewDTO orderPreviewDTO);

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
    CountdownVO getCountdown(Integer id);

    /**
     * 订单支付
     * @param id
     * @return
     */
    PayVO orderPay(Long id);

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
