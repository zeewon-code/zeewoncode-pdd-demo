package com.zeewoncode.pdd_server.a.controller;

import com.zeewoncode.entity.OrderCard;
import com.zeewoncode.result.PageResult;
import com.zeewoncode.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

/**
 * A端-订单监管
 */
@RestController
@RequestMapping("/api/a/orders")
@Slf4j
public class OrderController {

    @Autowired
    private com.zeewoncode.pdd_server.a.service.OrderService orderService;

    /**
     * 全平台订单查询
     * @param merchantId
     * @param status
     * @param startTime
     * @param endTime
     * @param page
     * @param size
     * @return
     */
    @GetMapping
    public Result<PageResult<OrderCard>> selectOrders(
            @RequestParam(required = false) Long merchantId,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) LocalDateTime startTime,
            @RequestParam(required = false) LocalDateTime endTime,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size
    ) {
        log.info("全平台订单查询: merchantId={}, status={}, startTime={}, endTime={}, page={}, Size={}",
                merchantId, status, startTime, endTime, page, size);
        return Result.success(orderService.selectOrders(merchantId, status, startTime, endTime, page, size));
    }
}
