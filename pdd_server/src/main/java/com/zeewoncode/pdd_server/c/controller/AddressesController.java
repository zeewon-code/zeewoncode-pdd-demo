package com.zeewoncode.pdd_server.c.controller;

import com.zeewoncode.dto.AddressDTO;
import com.zeewoncode.pdd_server.security.LoginUser;
import com.zeewoncode.result.Result;
import com.zeewoncode.vo.AddressVO;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
    private com.zeewoncode.pdd_server.c.service.AddressesService addressesService;

    /**
     * 获取收货地址列表
     * @return
     */
    @GetMapping
    public Result<List<AddressVO>> getAddresses(@AuthenticationPrincipal LoginUser loginUser) {
        log.info("获取收货地址列表");
        return Result.success(addressesService.getAddresses(loginUser.getUserId()));
    }

    /**
     * 获取收货地址详情
     * @param addressId
     * @return
     */
    @GetMapping("/{addressId}")
    public Result<AddressVO> getAddressDetail(@PathVariable Long addressId,
                                              @AuthenticationPrincipal LoginUser loginUser) {
        log.info("获取收货地址详情:{}", addressId);
        return Result.success(addressesService.getAddressDetail(addressId, loginUser.getUserId()));
    }

    /**
     * 添加收货地址
     * @param addressDTO
     * @return
     */
    @PostMapping
    public Result<Void> addAddress(@RequestBody @Valid AddressDTO addressDTO,
                             @AuthenticationPrincipal LoginUser loginUser) {
        log.info("添加收货地址:{}", addressDTO);
        addressesService.addAddress(addressDTO, loginUser.getUserId());
        return Result.success();
    }

    /**
     * 修改收货地址
     * @param id
     * @return
     */
    @PutMapping("/{id}")
    public Result<Void> updateAddress(@PathVariable Long id,
                                @RequestBody @Valid AddressDTO addressDTO,
                                @AuthenticationPrincipal LoginUser loginUser) {
        addressDTO.setId(id);
        log.info("修改收货地址:{}", addressDTO);
        addressesService.updateAddress(addressDTO, loginUser.getUserId());
        return Result.success();
    }

    /**
     * 删除收货地址
     * @param id
     * @return
     */
    @DeleteMapping("/{id}")
    public Result<Void> deleteAddress(@PathVariable Long id) {
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
    public Result<Void> setDefaultAddress(@PathVariable Long id,
                                    @AuthenticationPrincipal LoginUser loginUser) {
        log.info("设置默认收货地址:{}", id);
        addressesService.setDefaultAddress(id, loginUser.getUserId());
        return Result.success();
    }
}
