package com.zeewoncode.pdd_server.a.controller;

import com.zeewoncode.entity.SpuCard;


import com.zeewoncode.result.PageResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * A端-商品监督模块
 */
@RestController
@RequestMapping("/api/a/spu")
@Slf4j
public class SpuController {

    @Autowired
    private com.zeewoncode.pdd_server.a.service.SpuService spuService;

    /**
     * 获取商品监督列表
     * @param merchantId
     * @param status
     * @param categoryId
     * @param page
     * @param size
     * @return
     */
    @GetMapping
    public PageResult<SpuCard> listAllSpuCards(
            @RequestParam(required = false) Long merchantId,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size
    ) {
        log.info("获取商品监督列表：merchantId:{}, status:{}, categoryId:{}, page:{}, size:{}", merchantId, status, categoryId, page, size);
        PageResult<SpuCard> result = spuService.listAllSpuCards(merchantId, status, categoryId, page, size);
        return result;
    }
}
