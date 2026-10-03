package com.zeewoncode.pdd_server.c.controller;

import com.zeewoncode.pdd_server.c.service.CartService;
import com.zeewoncode.req.CartAddReq;
import com.zeewoncode.vo.CartResult;
import com.zeewoncode.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * C端-购物车模块
 */
@RestController
@RequestMapping("/api/cart")
@Slf4j
public class CartController {

    @Autowired
    private CartService cartService;

    /**
     * 查看购物车列表
     * @return
     */
    @GetMapping
    public Result<CartResult> getCartList() {
        log.info("查看购物车列表");
        CartResult result = cartService.getCartList();
        return Result.success(result);
    }


    /**
     * 添加购物车
     * @param req
     * @return
     */
    @PostMapping("/add")
    public Result addCart(@RequestBody CartAddReq req) {
        log.info("添加购物车:{}", req);
        cartService.addCart(req);
        return Result.success();
    }
}
