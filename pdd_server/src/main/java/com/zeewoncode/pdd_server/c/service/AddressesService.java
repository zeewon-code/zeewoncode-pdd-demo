package com.zeewoncode.pdd_server.c.service;

import com.zeewoncode.dto.AddressDTO;
import com.zeewoncode.vo.AddressVO;

import java.util.List;

/**
 * C端-收货地址模块
 */
public interface AddressesService {

    /**
     * 获取收货地址列表
     * @param userId
     * @return
     */
    List<AddressVO> getAddresses(Long userId);

    /**
     * 添加收货地址
     * @param addressDTO
     * @param userId
     */
    void addAddress(AddressDTO addressDTO, Long userId);

    /**
     * 修改收货地址
     * @param addressDTO
     * @param userId
     */
    void updateAddress(AddressDTO addressDTO, Long userId);


    /**
     * 删除收货地址
     * @param addressId
     */
    void deleteAddress(Long addressId);

    /**
     * 设置默认收货地址
     * @param addressId
     */
    void setDefaultAddress(Long addressId, Long userId);
}
