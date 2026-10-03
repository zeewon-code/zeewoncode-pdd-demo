package com.zeewoncode.pdd_server.c.service;

import com.zeewoncode.req.CartAddReq;
import com.zeewoncode.req.CartQuantityReq;
import com.zeewoncode.vo.CartResult;

public interface CartService {
    /**
     * 查看购物车列表
     * @return
     */
    CartResult getCartList();

    /**
     * 添加购物车
     * @param req
     */
    void addCart(CartAddReq req);

    /**
     * 更新购物车数量
     * @param id
     * @param req
     */
    void updateCartQuantity(Long id, CartQuantityReq req);
}
