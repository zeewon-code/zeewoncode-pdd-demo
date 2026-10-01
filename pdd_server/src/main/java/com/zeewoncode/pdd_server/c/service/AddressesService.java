package com.zeewoncode.pdd_server.c.service;

import com.zeewoncode.entity.UserAddress;
import com.zeewoncode.req.AddressReq;
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
    void addAddress(AddressReq addressReq);

    /**
     * 修改收货地址
     * @param addressReq
     */
    void updateAddress(AddressReq addressReq);


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
