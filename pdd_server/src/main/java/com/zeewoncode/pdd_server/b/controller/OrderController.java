package com.zeewoncode.pdd_server.b.controller;


import com.zeewoncode.dto.ShipDTO;
import com.zeewoncode.entity.OrderCard;
import com.zeewoncode.entity.OrderDetail;
import com.zeewoncode.result.PageResult;
import com.zeewoncode.result.Result;
import com.zeewoncode.vo.ShipVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * B端-订单管理
 */
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

    /**
     * 获取本店订单详情
     * @param id 订单id
     * @return
     */
    @GetMapping("/{id}")
    public Result<OrderDetail> getOrderDetail(@PathVariable Long id) {
        log.info("获取本店订单详情: 订单id{}", id);
        OrderDetail result = orderService.getOrderDetail(id);
        return Result.success(result);
    }

    /**
     * 订单发货
     * @param id
     * @param dto
     * @return
     */
    @PostMapping("/{id}/ship")
    public Result<ShipVO> ship(
            @PathVariable Long id,
            @RequestBody ShipDTO dto
    ) {
        log.info("订单发货: 订单id{}, 发货信息{}", id, dto);
        ShipVO result = orderService.ship(id, dto);
        return Result.success(result);
    }
}
