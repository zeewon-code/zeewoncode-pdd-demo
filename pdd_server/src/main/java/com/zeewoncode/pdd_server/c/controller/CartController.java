package com.zeewoncode.pdd_server.c.controller;

import com.zeewoncode.dto.CartAddDTO;
import com.zeewoncode.dto.CartQuantityDTO;
import com.zeewoncode.dto.CartSelectDTO;
import com.zeewoncode.pdd_server.security.LoginUser;
import com.zeewoncode.vo.CartVO;
import com.zeewoncode.result.Result;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

/**
 * C端-购物车模块
 */
@RestController
@RequestMapping("/api/cart")
@Slf4j
public class CartController {

    @Autowired
    private com.zeewoncode.pdd_server.c.service.CartService cartService;

    /**
     * 查看购物车列表
     * @return
     */
    @GetMapping
    public Result<CartVO> getCartList(@AuthenticationPrincipal LoginUser loginUser) {
        log.info("查看购物车列表");
        return Result.success(cartService.getCartList(loginUser.getUserId()));
    }

    /**
     * 添加购物车
     * @param req
     * @return
     */
    @PostMapping("/add")
    public Result<Void> addCart(@RequestBody @Valid CartAddDTO req, @AuthenticationPrincipal LoginUser loginUser) {
        log.info("添加购物车:{}", req);
        cartService.addCart(req, loginUser.getUserId());
        return Result.success();
    }

    /**
     * 更新购物车数量
     * @param id
     * @param req
     * @return
     */
    @PutMapping("/{id}/quantity")
    public Result<Void> updateCartQuantity(@PathVariable Long id,
                                     @RequestBody @Valid CartQuantityDTO req) {
        log.info("更新购物车数量:id:{},quantity:{}", id,req);
        cartService.updateCartQuantity(id, req);
        return Result.success();
    }


    /**
     * 选中或取消选中购物车
     * @param req
     * @return
     */
    @PutMapping("/select")
    public Result<Void> selectCart(@RequestBody CartSelectDTO req) {
        log.info("选中或取消选中购物车:{}", req);
        cartService.selectCart(req);
        return Result.success();
    }

    /**
     * 删除购物车
     * @param id
     * @return
     */
    @DeleteMapping("/{id}")
    public Result<Void> deleteCart(@PathVariable Long id) {
        log.info("删除购物车:id:{}", id);
        cartService.deleteCart(id);
        return Result.success();
    }

    /**
     * 清空已勾选购物车条目
     * @return
     */
    @DeleteMapping("/selected")
    public Result<Void> deleteCartBySelected(@AuthenticationPrincipal LoginUser loginUser) {
        log.info("清空已勾选购物车条目");
        cartService.deleteCartBySelected(loginUser.getUserId());
        return Result.success();
    }
}
