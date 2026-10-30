package com.zeewoncode.pdd_server.b.controller;

import com.zeewoncode.dto.SkuDTO;
import com.zeewoncode.dto.SkuUpdateDTO;
import com.zeewoncode.dto.StockDTO;
import com.zeewoncode.result.Result;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * B端-商品规格模块
 */
@RestController
@RequestMapping("/api/b/skus")
@Slf4j
public class SkuController {

    @Autowired
    private com.zeewoncode.pdd_server.b.service.SkuService skuService;

    /**
     * 新增SKU
     * @param id
     * @param dto
     * @return
     */
    @PostMapping("/{id}")
    public Result addSku(
            @PathVariable Long id,
            @Valid @RequestBody SkuDTO dto
    ) {
        log.info("新建SKU: {}, {}", id, dto);
        skuService.addSku(id, dto);
        return Result.success();
    }


    /**
     * 编辑 SKU（价格/规格/图）
     * @param id
     * @param dto
     * @return
     */
    @PutMapping("/{id}")
    public Result editSku(
            @PathVariable Long id,
            @Valid @RequestBody SkuUpdateDTO dto
    ) {
        log.info("编辑 SKU: {}, {}", id, dto);
        skuService.editSku(id, dto);
        return Result.success();
    }

    /**
     * 删除 SKU
     * @param id
     * @return
     */
    @Validated
    @DeleteMapping("/{id}")
    public Result deleteSku(@PathVariable @NotNull(message = "ID不能为空") Long id) {
        log.info("删除 SKU: {}", id);
        skuService.deleteSku(id);
        return Result.success();
    }

    /**
     * 调整库存
     * @param id
     * @param dto
     * @return
     */
    @PutMapping("/{id}/stock")
    public Result updateStock(
            @PathVariable Long id,
            @Valid @RequestBody StockDTO dto
    ){
        log.info("调整库存: {}, {}", id, dto);
        skuService.updateStock(id, dto);
        return Result.success();

    }
}
