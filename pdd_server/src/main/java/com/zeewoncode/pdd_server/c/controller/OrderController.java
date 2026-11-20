package com.zeewoncode.pdd_server.c.controller;

import com.zeewoncode.entity.*;
import com.zeewoncode.dto.OrderCreateDTO;
import com.zeewoncode.dto.OrderPreviewDTO;
import com.zeewoncode.pdd_server.security.LoginUser;
import com.zeewoncode.result.PageResult;
import com.zeewoncode.result.Result;
import com.zeewoncode.vo.CountdownVO;
import com.zeewoncode.vo.OrderCreateVO;
import com.zeewoncode.vo.OrderPreviewVO;
import com.zeewoncode.vo.PayVO;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
@Slf4j
public class OrderController {

    @Autowired
    private com.zeewoncode.pdd_server.c.service.OrderService orderService;

    /**
     * 提交订单
     * @param req
     * @return
     */
    @PostMapping
    public Result<OrderCreateVO> createOrder(@RequestBody @Valid OrderCreateDTO req,
                                             @AuthenticationPrincipal LoginUser loginUser) {
        log.info("提交订单:{}", req);
        return Result.success(orderService.createOrder(req, loginUser.getUserId()));
    }


    /**
     * 订单结算预览
     * @param orderPreviewDTO
     * @return
     */
    @PostMapping("/preview")
    public Result<OrderPreviewVO> previewOrder(@RequestBody @Valid OrderPreviewDTO orderPreviewDTO) {
        log.info("订单结算预览:{}", orderPreviewDTO);
        return Result.success(orderService.previewOrder(orderPreviewDTO));
    }

    /**
     * 取消待付款订单
     * @param id
     * @return
     */
    @PostMapping("/{id}/cancel")
    public Result<Void> cancelOrderPendingPayMent(@PathVariable Long id,
                                            @AuthenticationPrincipal LoginUser loginUser) {
        log.info("取消待付款订单:{}", id);
        orderService.cancelOrderPendingPayMent(id, loginUser.getUserId());
        return Result.success();
    }

    /**
     * 获取订单支付倒计时
     * @param id
     * @return
     */
    @GetMapping("/{id}/countdown")
    public Result<CountdownVO> getCountdown(@PathVariable Long id) {
        log.info("获取订单支付倒计时:订单id：{}", id);
        return Result.success(orderService.getCountdown(id));
    }

    /**
     * 订单支付
     * @param id
     * @return
     */
    @PostMapping("/{id}/pay")
    public Result<PayVO> orderPay(@PathVariable Long id,
                                  @AuthenticationPrincipal LoginUser loginUser) {
        log.info("订单支付:{}", id);
        return Result.success(orderService.orderPay(id, loginUser.getUserId()));
    }

    /**
     * 获取订单列表
     * @param status
     * @param page
     * @param size
     * @return
     */
    @GetMapping()
    public Result<PageResult<OrderCard>> getOrderList(@RequestParam(required = false) Integer status,
                                              @RequestParam(defaultValue = "1") Integer page,
                                              @RequestParam(defaultValue = "10") Integer size,
                                              @AuthenticationPrincipal LoginUser loginUser
    ) {
        log.info("获取订单列表:状态{}, 页码{}, 每页数量{}", status, page, size);
        return Result.success(orderService.getOrderList(status, page, size, loginUser.getUserId()));
    }

    /**
     * 获取订单详情
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public Result<OrderDetail> getOrderDetail(@PathVariable Long id,
                                              @AuthenticationPrincipal LoginUser loginUser) {
        log.info("获取订单详情:{}", id);
        return Result.success(orderService.getOrderDetail(id, loginUser.getUserId()));
    }
}
