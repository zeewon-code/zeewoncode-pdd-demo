package com.zeewoncode.pdd_server.b.controller;


import com.zeewoncode.entity.OrderCard;
import com.zeewoncode.result.PageResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/b/orders")
@Slf4j
public class OrderController {
    @Autowired
    private com.zeewoncode.pdd_server.b.service.OrderService  orderService;

    /**
     * 获取本店订单列表
     * @param status
     * @param page
     * @param size
     * @return
     */
    @GetMapping
    public PageResult<OrderCard> getOrderList(
            @RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size
    ) {
        log.info("获取本店订单列表:状态{}, 页码{}, 每页数量{}", status, page, size);
        PageResult<OrderCard> result = orderService.getOrderList(status, page, size);
        return result;
    }
}
