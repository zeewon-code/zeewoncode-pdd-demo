package com.zeewoncode.pdd_server.c.controller;

import com.zeewoncode.entity.CountdownResult;
import com.zeewoncode.entity.OrderCreateResult;
import com.zeewoncode.entity.OrderPreviewResult;
import com.zeewoncode.pdd_server.c.service.OrderService;
import com.zeewoncode.req.OrderCreateReq;
import com.zeewoncode.req.OrderPreviewReq;
import com.zeewoncode.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
@Slf4j
public class OrderController {

    @Autowired
    private OrderService orderService;

    /**
     * 提交订单
     * @param req
     * @return
     */
    @PostMapping
    public Result<OrderCreateResult> createOrder(@RequestBody OrderCreateReq req) {
        log.info("提交订单:{}", req);
        OrderCreateResult result = orderService.createOrder(req);
        return Result.success(result);
    }


    /**
     * 订单结算预览
     * @param orderPreviewReq
     * @return
     */
    @PostMapping("/preview")
    public Result<OrderPreviewResult> previewOrder(@RequestBody OrderPreviewReq orderPreviewReq) {
        log.info("订单结算预览:{}", orderPreviewReq);
        OrderPreviewResult result = orderService.previewOrder(orderPreviewReq);
        return Result.success(result);
    }

    /**
     * 取消待付款订单
     * @param id
     * @return
     */
    @PostMapping("/{id}/cancel")
    public Result cancelOrderPendingPayMent(@PathVariable Integer id) {
        log.info("取消待付款订单:{}", id);
        orderService.cancelOrderPendingPayMent(id);
        return Result.success();
    }

    /**
     * 获取订单支付倒计时
     * @param id
     * @return
     */
    @PostMapping("/{id}/countdown")
    public Result<CountdownResult> getCountdown(@PathVariable Integer id) {
        log.info("获取订单支付倒计时:订单id：{}", id);
        CountdownResult result = orderService.getCountdown(id);
        return Result.success(result);
    }
}
