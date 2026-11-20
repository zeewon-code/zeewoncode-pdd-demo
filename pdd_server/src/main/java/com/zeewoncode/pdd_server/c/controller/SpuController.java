package com.zeewoncode.pdd_server.c.controller;

import com.zeewoncode.entity.SpuCard;
import com.zeewoncode.entity.SpuDetail;
import com.zeewoncode.dto.SpuListQueryDTO;
import com.zeewoncode.result.PageResult;
import com.zeewoncode.result.Result;
import com.zeewoncode.vo.CategoryNodeVO;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 *  C端-商品浏览模块
 */
@RestController
@RequestMapping("/api")
@Slf4j
public class SpuController {

    @Autowired
    private com.zeewoncode.pdd_server.c.service.SpuService spuService;

    /**
     * 获取商品分类树
     * @return
     */
    @GetMapping("/categories")
    public Result<List<CategoryNodeVO>> getCategoryTree() {
        log.info("获取商品分类树");
        return Result.success(spuService.getCategoryTree());
    }

    /**
     * 获取商品列表
     * @param req
     * @return
     */
    @PostMapping("/spu/list")
    public Result<PageResult<SpuCard>> getSpuList(@RequestBody @Valid SpuListQueryDTO req) {
        log.info("获取商品列表");
        return Result.success(spuService.getSpuList(req));
    }

    /**
     * 获取商品详情
     * @return
     */
    @GetMapping("/spu/{id}")
    public Result<SpuDetail> getSpuDetail(@PathVariable Long id) {
        log.info("获取商品详情");
        return Result.success(spuService.getSpuDetail(id));
    }


}
