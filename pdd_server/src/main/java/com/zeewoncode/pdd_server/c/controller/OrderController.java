package com.zeewoncode.pdd_server.c.controller;

import com.zeewoncode.entity.*;
import com.zeewoncode.pdd_server.c.service.OrderService;
import com.zeewoncode.dto.OrderCreateDTO;
import com.zeewoncode.dto.OrderPreviewDTO;
import com.zeewoncode.result.PageResult;
import com.zeewoncode.result.Result;
import com.zeewoncode.vo.CountdownVO;
import com.zeewoncode.vo.OrderCreateVO;
import com.zeewoncode.vo.OrderPreviewVO;
import com.zeewoncode.vo.PayVO;
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
    public Result<OrderCreateVO> createOrder(@RequestBody OrderCreateDTO req) {
        log.info("提交订单:{}", req);
        OrderCreateVO result = orderService.createOrder(req);
        return Result.success(result);
    }


    /**
     * 订单结算预览
     * @param orderPreviewDTO
     * @return
     */
    @PostMapping("/preview")
    public Result<OrderPreviewVO> previewOrder(@RequestBody OrderPreviewDTO orderPreviewDTO) {
        log.info("订单结算预览:{}", orderPreviewDTO);
        OrderPreviewVO result = orderService.previewOrder(orderPreviewDTO);
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
    @GetMapping("/{id}/countdown")
    public Result<CountdownVO> getCountdown(@PathVariable Integer id) {
        log.info("获取订单支付倒计时:订单id：{}", id);
        CountdownVO result = orderService.getCountdown(id);
        return Result.success(result);
    }

    /**
     * 订单支付
     * @param id
     * @return
     */
    @PostMapping("/{id}/pay")
    public Result<PayVO> orderPay(@PathVariable Long id) {
        log.info("订单支付:{}", id);
        PayVO result = orderService.orderPay(id);
        return Result.success(result);
    }

    /**
     * 获取订单列表
     * @param status
     * @param page
     * @param size
     * @return
     */
    @GetMapping()
    public PageResult<OrderCard> getOrderList(@RequestParam(required = false) Integer status,
                                              @RequestParam(defaultValue = "1") Integer page,
                                              @RequestParam(defaultValue = "10") Integer size) {
        log.info("获取订单列表:状态{}, 页码{}, 每页数量{}", status, page, size);
        PageResult<OrderCard> result = orderService.getOrderList(status, page, size);
        return result;
    }

    /**
     * 获取订单详情
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public Result<OrderDetail> getOrderDetail(@PathVariable Integer id) {
        log.info("获取订单详情:{}", id);
        OrderDetail result = orderService.getOrderDetail(id);
        return Result.success(result);
    }
}
