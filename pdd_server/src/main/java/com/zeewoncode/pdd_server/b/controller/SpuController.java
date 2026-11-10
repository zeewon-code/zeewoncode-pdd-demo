package com.zeewoncode.pdd_server.b.controller;

import com.zeewoncode.dto.*;
import com.zeewoncode.entity.SpuCard;
import com.zeewoncode.entity.SpuDetail;
import com.zeewoncode.pdd_server.security.LoginUser;
import com.zeewoncode.result.PageResult;
import com.zeewoncode.result.Result;
import com.zeewoncode.vo.SpuCreateVO;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;


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
            @RequestParam(required = false) Integer status,
            @AuthenticationPrincipal LoginUser loginUser
            ) {
        log.info("分页查询商品列表: 页码{}, 每页数量{}, 关键字{}, 类目ID{}, 状态{}", page, size, keyword, categoryId, status);
        PageResult<SpuCard> result = spuService.listSpuCards(page, size, keyword, categoryId, status, loginUser.getUserId());
        return Result.success(result);
    }

    /**
     * 新建商品
     * @param dto
     * @return
     */
    @PostMapping
    public Result<SpuCreateVO> addSpu(@Valid @RequestBody SpuCreateDTO dto,
                                      @AuthenticationPrincipal LoginUser loginUser) {
        log.info("新建商品: {}", dto);
        SpuCreateVO vo = spuService.addSpu(dto, loginUser.getUserId());
        return Result.success(vo);
    }

    /**
     * 商品详情
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public Result<SpuDetail> getSpuDetail(@PathVariable Long id, @AuthenticationPrincipal LoginUser loginUser) {
        log.info("商品详情: {}", id);
        SpuDetail detail = spuService.getSpuDetail(id, loginUser.getUserId());
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
                          @Valid @RequestBody(required = false) SpuEditDTO dto,
                          @AuthenticationPrincipal LoginUser loginUser
    ) {
        log.info("编辑商品: {}, {}", id, dto);
        spuService.editSpu(id, dto, loginUser.getUserId());
        return Result.success();
    }

    /**
     * 软删除商品
     * @param id
     * @return
     */
    @DeleteMapping("/{id}")
    public Result softDeleteSpu(@PathVariable Long id,
                                @AuthenticationPrincipal LoginUser loginUser) {
        log.info("软删除商品: {}", id);
        spuService.softDeleteSpu(id, loginUser.getUserId());
        return Result.success();
    }

    /**
     * 上架/下架商品
     * @param id
     * @param dto
     * @return
     */
    @PutMapping("/{id}/status")
    public Result listAndDelist(
            @PathVariable Long id,
            @RequestBody SpuStatusDTO dto,
            @AuthenticationPrincipal LoginUser loginUser
    ) {
        log.info("更新商品状态: {}, {}", id, dto);
        spuService.listAndDelist(id, dto, loginUser.getUserId());
        return Result.success();
    }


}
