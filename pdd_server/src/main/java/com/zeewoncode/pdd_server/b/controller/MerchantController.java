package com.zeewoncode.pdd_server.b.controller;

import com.zeewoncode.dto.ShopApplyDTO;
import com.zeewoncode.dto.ShopUpdateDTO;
import com.zeewoncode.pdd_server.b.service.MerchantService;
import com.zeewoncode.result.Result;
import com.zeewoncode.vo.BDashboard;
import com.zeewoncode.vo.MerchantVO;
import com.zeewoncode.vo.ShopApplyVO;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * B端-开店与店铺模块
 */
@RestController
@RequestMapping("/api/b/merchant")
@Slf4j
public class MerchantController {

    @Autowired
    private com.zeewoncode.pdd_server.b.service.MerchantService merchantService;

    /**
     * 申请开店
     * @param shopApplyDTO
     * @return
     */
    @PostMapping("/apply")
    public Result<ShopApplyVO> applyShop(@RequestBody @Valid ShopApplyDTO shopApplyDTO) {
        log.info("申请开店:{}", shopApplyDTO);
        ShopApplyVO shopApplyVO = merchantService.applyShop(shopApplyDTO);
        return Result.success();
    }

    /**
     * 获取我的店铺信息
     * @return
     */
    @GetMapping("/mine")
    public Result<MerchantVO> getMyShop() {
        log.info("获取我的店铺信息");
        MerchantVO merchantVO = merchantService.getMyShop();
        return Result.success(merchantVO);
    }

    /**
     * 修改店铺信息
     * @param shopUpdateDTO
     * @return
     */
    @PutMapping("/mine")
    public Result updateShop(@RequestBody @Valid ShopUpdateDTO shopUpdateDTO) {
        log.info("修改店铺信息:{}", shopUpdateDTO);
        merchantService.updateShop(shopUpdateDTO);
        return Result.success();
    }

    /**
     * 修改店铺状态
     * @param status
     * @return
     */
    @PutMapping("/mine/status")
    public Result updateShopStatus(@RequestParam Integer status) {
        log.info("修改店铺状态:{}", status);
        merchantService.updateShopStatus(status);
        return Result.success();
    }

    /**
     * 商家经营看板
     * @return
     */
    @GetMapping("/dashboard")
    public Result<BDashboard> getBDashboard() {
        log.info("获取商家经营看板数据");
        BDashboard bDashboard = merchantService.getBDashboard();
        return Result.success(bDashboard);
    }
}
