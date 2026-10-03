package com.zeewoncode.pdd_server.c.service;

import com.zeewoncode.req.CartAddReq;
import com.zeewoncode.req.CartQuantityReq;
import com.zeewoncode.req.CartSelectReq;
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

    /**
     * 选中或取消选中购物车
     * @param req
     */
    void selectCart(CartSelectReq req);

    /**
     * 删除购物车
     * @param id
     */
    void deleteCart(Long id);
}
