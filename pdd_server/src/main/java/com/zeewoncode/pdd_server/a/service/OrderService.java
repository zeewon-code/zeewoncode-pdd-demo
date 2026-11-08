package com.zeewoncode.pdd_server.a.service;

import com.zeewoncode.entity.OrderCard;
import com.zeewoncode.result.PageResult;

import java.time.LocalDateTime;

public interface OrderService {

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
    PageResult<OrderCard> selectOrders(Long merchantId, Integer status, LocalDateTime startTime, LocalDateTime endTime, Integer page, Integer size);
}
