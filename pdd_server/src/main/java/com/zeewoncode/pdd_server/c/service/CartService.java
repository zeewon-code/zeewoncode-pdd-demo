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
    CartVO getCartList();

    /**
     * 添加购物车
     * @param req
     */
    void addCart(CartAddDTO req);

    /**
     * 更新购物车数量
     * @param id
     * @param req
     */
    void updateCartQuantity(Long id, CartQuantityDTO req);

    /**
     * 选中或取消选中购物车
     * @param req
     */
    void selectCart(CartSelectDTO req);

    /**
     * 删除购物车
     * @param id
     */
    void deleteCart(Long id);

    /**
     * 清空已勾选购物车条目
     */
    void deleteCartBySelected();
}
