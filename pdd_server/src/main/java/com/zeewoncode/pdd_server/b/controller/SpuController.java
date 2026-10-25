package com.zeewoncode.pdd_server.b.controller;

import com.zeewoncode.dto.SpuCreateDTO;
import com.zeewoncode.dto.SpuEditDTO;
import com.zeewoncode.entity.SpuCard;
import com.zeewoncode.entity.SpuDetail;
import com.zeewoncode.pdd_server.b.service.SpuService;
import com.zeewoncode.result.PageResult;
import com.zeewoncode.result.Result;
import com.zeewoncode.vo.SpuCreateVO;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * B端-商品模块
 */
@RestController
@RequestMapping("/api/b/spu")
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
    @GetMapping
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
    @PostMapping
    public Result<SpuCreateVO> addSpu(@RequestBody @Valid SpuCreateDTO dto) {
        log.info("新建商品: {}", dto);
        SpuCreateVO vo = spuService.addSpu(dto);
        return Result.success(vo);
    }

    /**
     * 商品详情
     * @param id
     * @return
     */
    @GetMapping("/{id}")
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
    @PutMapping("/{id}")
    public Result editSpu(@PathVariable Long id,
                            @RequestBody(required = false) @Valid SpuEditDTO dto) {
        log.info("编辑商品: {}, {}", id, dto);
        spuService.editSpu(id, dto);
        return Result.success();
    }

    /**
     * 软删除商品
     * @param id
     * @return
     */
    @DeleteMapping("/{id}")
    public Result softDeleteSpu(@PathVariable Long id) {
        log.info("软删除商品: {}", id);
        spuService.softDeleteSpu(id);
        return Result.success();
    }
}
