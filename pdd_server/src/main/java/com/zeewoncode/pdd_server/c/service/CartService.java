package com.zeewoncode.pdd_server.c.service;

import com.zeewoncode.vo.CartResult;

public interface CartService {
    /**
     * 查看购物车列表
     * @return
     */
    CartResult getCartList();
}
