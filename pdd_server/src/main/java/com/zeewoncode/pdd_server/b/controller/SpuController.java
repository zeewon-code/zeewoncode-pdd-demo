package com.zeewoncode.pdd_server.b.controller;

import com.zeewoncode.dto.SpuCreateDTO;
import com.zeewoncode.entity.SpuCard;
import com.zeewoncode.pdd_server.b.service.SpuService;
import com.zeewoncode.result.PageResult;
import com.zeewoncode.result.Result;
import com.zeewoncode.vo.SpuCreateVO;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.parameters.P;
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
    public Result<SpuCreateVO> addSpu(@RequestBody SpuCreateDTO dto) {
        log.info("新建商品: {}", dto);
        SpuCreateVO vo = spuService.addSpu(dto);
        return Result.success(vo);
    }
}
