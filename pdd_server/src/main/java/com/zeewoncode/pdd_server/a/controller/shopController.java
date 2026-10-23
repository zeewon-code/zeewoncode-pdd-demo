package com.zeewoncode.pdd_server.a.controller;

import com.zeewoncode.context.BaseContext;
import com.zeewoncode.dto.ShopApplyDTO;
import com.zeewoncode.pdd_server.a.service.ShopService;
import com.zeewoncode.result.Result;
import com.zeewoncode.vo.ShopApplyVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * B端-开店与店铺模块
 */
@RestController
@RequestMapping("/api/b/shops")
@Slf4j
public class shopController {

    @Autowired
    private ShopService shopService;

    /**
     * 申请开店
     * @param shopApplyDTO
     * @return
     */
    @PostMapping("/apply")
    public Result<ShopApplyVO> applyShop(@RequestBody @Valid ShopApplyDTO shopApplyDTO) {
        log.info("申请开店:{}", shopApplyDTO);
        ShopApplyVO shopApplyVO = shopService.applyShop(shopApplyDTO);
        return Result.success();
    }
}
