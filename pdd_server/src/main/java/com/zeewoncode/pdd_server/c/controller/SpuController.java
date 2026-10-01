package com.zeewoncode.pdd_server.c.controller;

import com.zeewoncode.entity.SpuCard;
import com.zeewoncode.entity.SpuDetail;
import com.zeewoncode.pdd_server.c.service.SpuService;
import com.zeewoncode.req.SpuListQueryReq;
import com.zeewoncode.result.PageResult;
import com.zeewoncode.result.Result;
import com.zeewoncode.vo.CategoryNodeVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 *  C端-商品浏览模块
 */
@RestController
@RequestMapping("/api")
@Slf4j
public class SpuController {

    @Autowired
    private SpuService spuService;

    /**
     * 获取商品分类树
     * @return
     */
    @GetMapping("/categories")
    public List<CategoryNodeVO> getCategoryTree() {
        log.info("获取商品分类树");
        return spuService.getCategoryTree();
    }

    /**
     * 获取商品列表
     * @param req
     * @return
     */
    @GetMapping("/spu/list")
    public Result<PageResult<SpuCard>> getSpuList(SpuListQueryReq req) {
        log.info("获取商品列表");
        PageResult<SpuCard> result = spuService.getSpuList(req);
        return Result.success(result);
    }

    /**
     * 获取商品详情
     * @return
     */
    @GetMapping("/spu/{id}")
    public Result<SpuDetail> getSpuDetail(@PathVariable Integer id) {
        log.info("获取商品详情");
        SpuDetail result = spuService.getSpuDetail(id);
        return Result.success(result);
    }
}
