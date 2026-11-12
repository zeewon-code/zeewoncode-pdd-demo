package com.zeewoncode.pdd_server.a.controller;

import com.zeewoncode.dto.SpuStatusDTO;
import com.zeewoncode.entity.SpuCard;
import com.zeewoncode.result.PageResult;
import com.zeewoncode.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


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
    public Result<PageResult<SpuCard>> listAllSpuCards(
            @RequestParam(required = false) Long merchantId,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size
    ) {
        log.info("获取商品监督列表：merchantId:{}, status:{}, categoryId:{}, page:{}, size:{}", merchantId, status, categoryId, page, size);
        PageResult<SpuCard> result = spuService.listAllSpuCards(merchantId, status, categoryId, page, size);
        return Result.success(result);
    }

    /**
     * 强制下架 / 恢复（平台兜底）
     * @param id
     * @param dto
     * @return
     */
    @PutMapping("/{id}/status")
    public Result PlatformForceControl(
            @PathVariable Long id,
            @RequestBody SpuStatusDTO dto

    ) {
        log.info("强制下架 / 恢复（平台兜底）：spuId:{}, dto:{}", id, dto);
        Result result = spuService.PlatformForceControl(id, dto);
        return result;
    }
}
