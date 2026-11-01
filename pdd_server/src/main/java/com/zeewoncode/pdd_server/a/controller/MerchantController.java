package com.zeewoncode.pdd_server.a.controller;

import com.zeewoncode.result.PageResult;
import com.zeewoncode.vo.MerchantVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
}
