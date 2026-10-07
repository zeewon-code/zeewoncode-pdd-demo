package com.zeewoncode.pdd_server.c.controller;

import com.zeewoncode.entity.OrderCreateResult;
import com.zeewoncode.entity.OrderPreviewResult;
import com.zeewoncode.pdd_server.c.service.OrderService;
import com.zeewoncode.req.OrderCreateReq;
import com.zeewoncode.req.OrderPreviewReq;
import com.zeewoncode.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
