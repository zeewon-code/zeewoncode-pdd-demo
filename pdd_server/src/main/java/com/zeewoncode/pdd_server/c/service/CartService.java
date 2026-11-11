package com.zeewoncode.pdd_server.c.service;

import com.zeewoncode.dto.CartAddDTO;
import com.zeewoncode.dto.CartQuantityDTO;
import com.zeewoncode.dto.CartSelectDTO;
import com.zeewoncode.vo.CartVO;

public interface CartService {
    /**
     * 查看购物车列表
     * @return
     */
    CartVO getCartList(Long userId);

    /**
     * 添加购物车
     * @param req
     */
    void addCart(CartAddDTO req, Long userId);

    /**
     * 更新购物车数量
     * @param cartItemId
     * @param req
     */
    void updateCartQuantity(Long cartItemId, CartQuantityDTO req);

    /**
     * 选中或取消选中购物车
     * @param req
     */
    void selectCart(CartSelectDTO req);

    /**
     * 删除购物车
     * @param cartItemId
     */
    void deleteCart(Long cartItemId);

    /**
     * 清空已勾选购物车条目
     */
    void deleteCartBySelected(Long userId);
}
