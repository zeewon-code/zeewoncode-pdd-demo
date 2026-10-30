package com.zeewoncode.pdd_server.b.service;

import com.zeewoncode.entity.OrderCard;
import com.zeewoncode.result.PageResult;

public interface OrderService {
    PageResult<OrderCard> getOrderList(Integer status, Integer page, Integer size);
}
