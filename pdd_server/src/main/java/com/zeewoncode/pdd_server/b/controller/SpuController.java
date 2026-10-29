package com.zeewoncode.pdd_server.b.controller;

import com.zeewoncode.dto.*;
import com.zeewoncode.entity.SpuCard;
import com.zeewoncode.entity.SpuDetail;
import com.zeewoncode.result.PageResult;
import com.zeewoncode.result.Result;
import com.zeewoncode.vo.SpuCreateVO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


/**
 * B端-商品模块
 */
@RestController
@RequestMapping("/api/b")
@Slf4j
public class SpuController {

    @Autowired
    private com.zeewoncode.pdd_server.b.service.SpuService spuService;

    /**
     * 分页查询商品列表
     * @param page
     * @param size
     * @param keyword
     * @param categoryId
     * @param status
     * @return
     */
    @GetMapping("/spu")
    public Result<PageResult<SpuCard>> listSpuCards(
            @RequestParam(defaultValue = "1", required = false) Integer page,
            @RequestParam(defaultValue = "10", required = false) Integer size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Integer status
    ) {
        log.info("分页查询商品列表: 页码{}, 每页数量{}, 关键字{}, 类目ID{}, 状态{}", page, size, keyword, categoryId, status);
        PageResult<SpuCard> result = spuService.listSpuCards(page, size, keyword, categoryId, status);
        return Result.success(result);
    }

    /**
     * 新建商品
     * @param dto
     * @return
     */
    @PostMapping("/spu")
    public Result<SpuCreateVO> addSpu(@Valid @RequestBody SpuCreateDTO dto) {
        log.info("新建商品: {}", dto);
        SpuCreateVO vo = spuService.addSpu(dto);
        return Result.success(vo);
    }

    /**
     * 商品详情
     * @param id
     * @return
     */
    @GetMapping("/spu/{id}")
    public Result<SpuDetail> getSpuDetail(@PathVariable Long id) {
        log.info("商品详情: {}", id);
        SpuDetail detail = spuService.getSpuDetail(id);
        return Result.success(detail);
    }

    /**
     * 编辑商品
     * @param id
     * @param dto
     * @return
     */
    @PutMapping("/spu/{id}")
    public Result editSpu(@PathVariable Long id,
                          @Valid @RequestBody(required = false) SpuEditDTO dto) {
        log.info("编辑商品: {}, {}", id, dto);
        spuService.editSpu(id, dto);
        return Result.success();
    }

    /**
     * 软删除商品
     * @param id
     * @return
     */
    @DeleteMapping("/spu/{id}")
    public Result softDeleteSpu(@PathVariable Long id) {
        log.info("软删除商品: {}", id);
        spuService.softDeleteSpu(id);
        return Result.success();
    }

    /**
     * 上架/下架商品
     * @param id
     * @param dto
     * @return
     */
    @PutMapping("/spu/{id}/status")
    public Result listAndDelist(
            @PathVariable Long id,
            @RequestBody SpuStatusDTO dto
    ) {
        log.info("更新商品状态: {}, {}", id, dto);
        spuService.listAndDelist(id, dto);
        return Result.success();
    }

    /**
     * 新增SKU
     * @param id
     * @param dto
     * @return
     */
    @PostMapping("/skus/{id}")
    public Result addSku(
            @PathVariable Long id,
            @Valid @RequestBody SkuDTO dto
    ) {
        log.info("新建SKU: {}, {}", id, dto);
        spuService.addSku(id, dto);
        return Result.success();
    }


    /**
     * 编辑 SKU（价格/规格/图）
     * @param id
     * @param dto
     * @return
     */
    @PutMapping("/skus/{id}")
    public Result editSku(
            @PathVariable Long id,
            @Valid @RequestBody SkuUpdateDTO dto
    ) {
        log.info("编辑 SKU: {}, {}", id, dto);
        spuService.editSku(id, dto);
        return Result.success();
    }

    /**
     * 删除 SKU
     * @param id
     * @return
     */
    @Validated
    @DeleteMapping("/skus/{id}")
    public Result deleteSku(@PathVariable @NotNull(message = "ID不能为空") Long id) {
        log.info("删除 SKU: {}", id);
        spuService.deleteSku(id);
        return Result.success();
    }
}
