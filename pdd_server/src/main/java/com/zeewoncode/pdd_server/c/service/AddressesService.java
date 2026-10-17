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
    List<AddressVO> getAddresses(Integer userId);

    /**
     * 添加收货地址
     */
    void addAddress(AddressDTO addressDTO);

    /**
     * 修改收货地址
     * @param addressDTO
     */
    void updateAddress(AddressDTO addressDTO);


    /**
     * 删除收货地址
     * @param id
     */
    void deleteAddress(Integer id);

    /**
     * 设置默认收货地址
     * @param id
     */
    void setDefaultAddress(Integer id);
}
