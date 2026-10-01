package com.zeewoncode.pdd_server.c.controller;


import com.zeewoncode.context.BaseContext;
import com.zeewoncode.entity.UserAddress;
import com.zeewoncode.pdd_server.c.service.AddressesService;
import com.zeewoncode.req.AddressReq;
import com.zeewoncode.result.Result;
import com.zeewoncode.vo.AddressVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * C端-收货地址模块
 */
@RestController
@RequestMapping("/api/addresses")
@Slf4j
public class AddressesController {

    @Autowired
    private AddressesService addressesService;

    /**
     * 获取收货地址列表
     * @return
     */
    @GetMapping
    public Result<List<AddressVO>> getAddresses() {
        log.info("获取收货地址列表");
        Integer userId = BaseContext.getCurrentId();
        List<AddressVO> addressVOList = addressesService.getAddresses(userId);
        return Result.success(addressVOList);
    }

    /**
     * 添加收货地址
     * @param addressReq
     * @return
     */
    @PostMapping
    public Result addAddress(@RequestBody AddressReq addressReq) {
        log.info("添加收货地址:{}", addressReq);
        addressesService.addAddress(addressReq);
        return Result.success();
    }

    /**
     * 修改收货地址
     * @param id
     * @return
     */
    @PutMapping("/{id}")
    public Result updateAddress(@PathVariable Integer id,
                                @RequestBody AddressReq addressReq) {
        addressReq.setId(id);
        log.info("修改收货地址:{}", addressReq);
        addressesService.updateAddress(addressReq);
        return Result.success();
    }

    /**
     * 删除收货地址
     * @param id
     * @return
     */
    @DeleteMapping("/{id}")
    public Result deleteAddress(@PathVariable Integer id) {
        log.info("删除收货地址:{}", id);
        addressesService.deleteAddress(id);
        return Result.success();
    }

    /**
     * 设置默认收货地址
     * @param id
     * @return
     */
    @PutMapping("/{id}/default")
    public Result setDefaultAddress(@PathVariable Integer id) {
        log.info("设置默认收货地址:{}", id);
        addressesService.setDefaultAddress(id);
        return Result.success();
    }
}
