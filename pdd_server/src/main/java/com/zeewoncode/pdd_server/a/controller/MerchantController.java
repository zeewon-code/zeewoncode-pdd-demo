package com.zeewoncode.pdd_server.a.controller;

import com.zeewoncode.dto.StatusDTO;
import com.zeewoncode.result.PageResult;
import com.zeewoncode.result.Result;
import com.zeewoncode.vo.MerchantVO;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.*;

/**
 * A端-店铺管理
 */
@RestController
@RequestMapping("/api/a/merchant")
@Slf4j
public class MerchantController {

    @Autowired
    private com.zeewoncode.pdd_server.a.service.MerchantService merchantService;

    /**
     * 店铺列表
     * @param status
     * @param keyword
     * @param page
     * @param size
     * @return
     */
    @GetMapping
    public PageResult<MerchantVO> merchantsList(
            @RequestParam(required = false) Integer status,  // 1营业 0停业
            @RequestParam(required = false) String keyword, // 店铺名模糊搜索
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size
    ) {
        log.info("店铺列表：status:{}, keyword:{}, page:{}, size:{}", status, keyword, page, size);
        PageResult<MerchantVO> result = merchantService.getMerchantsList(status, keyword, page, size);
        return result;
    }

    /**
     * 店铺详情
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public Result<MerchantVO> merchantDetail(@PathVariable Long id) {
        log.info("店铺详情：id:{}", id);
        MerchantVO result = merchantService.getMerchantDetail(id);
        return Result.success(result);
    }

    /**
     * 关店 / 恢复营业
     * @param merchantId
     * @param dto
     * @return
     */
    @PutMapping("/{id}")
    public Result updateMerchantStatus(
            @PathVariable("id") Long merchantId,
            @RequestBody @Valid StatusDTO dto
    ) {
        log.info("更新店铺状态：merchantId:{}, status:{}", merchantId, dto.getStatus());
        merchantService.updateMerchantStatus(merchantId, dto.getStatus());
        return Result.success();
    }
}
