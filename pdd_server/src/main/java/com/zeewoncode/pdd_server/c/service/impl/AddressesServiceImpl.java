package com.zeewoncode.pdd_server.c.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.zeewoncode.context.BaseContext;
import com.zeewoncode.entity.UserAddress;
import com.zeewoncode.pdd_server.c.service.AddressesService;
import com.zeewoncode.pdd_server.mapper.AddressesMapper;
import com.zeewoncode.req.AddressReq;
import com.zeewoncode.vo.AddressVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AddressesServiceImpl implements AddressesService {

    @Autowired
    private AddressesMapper addressesMapper;


    /**
     * 根据用户ID获取收货地址列表
     * @param userId
     * @return
     */
    @Override
    public List<AddressVO> getAddresses(Integer userId) {
        List<UserAddress> userAddresses = addressesMapper.selectAddressesByUserId(userId);
        List<AddressVO> addressVOList = userAddresses.stream().map(addr -> {
            AddressVO addressVO = BeanUtil.copyProperties(addr, AddressVO.class);
            Integer isDefault = addr.getIsDefault();
            addressVO.setIsDefault(Integer.valueOf(1).equals(isDefault));
            return addressVO;
        }).collect(Collectors.toList());
        return addressVOList;
    }

    /**
     * 添加收货地址
     */
    @Override
    @Transactional
    public void addAddress(AddressReq addressReq) {
        Integer userId = BaseContext.getCurrentId();
        UserAddress userAddress = new UserAddress();
        BeanUtils.copyProperties(addressReq, userAddress);
        userAddress.setDeletedFlag(0);
        userAddress.setUserId(userId);
        if (addressReq.getIsDefault() != null) {
            userAddress.setIsDefault(Boolean.TRUE.equals(addressReq.getIsDefault()) ? 1 : 0);
        } else {
            userAddress.setIsDefault(0);
        }
        if (userAddress.getIsDefault() != null && userAddress.getIsDefault() == 1) {
            // 查找用户默认地址并修改成非默认
            addressesMapper.updateAddressToNonDefault(userAddress);
        }
        // 插入新地址
        addressesMapper.insertAddress(userAddress);
    }

    /**
     * 修改收货地址
     * @param addressReq
     */
    @Override
    @Transactional
    public void updateAddress(AddressReq addressReq) {
        Integer userId = BaseContext.getCurrentId();
        UserAddress userAddress = new UserAddress();
        BeanUtils.copyProperties(addressReq, userAddress);
        userAddress.setDeletedFlag(0);
        userAddress.setUserId(userId);
        if (addressReq.getIsDefault() != null) {
            userAddress.setIsDefault(Boolean.TRUE.equals(addressReq.getIsDefault()) ? 1 : 0);
        } else {
            userAddress.setIsDefault(0);
        }
        if (userAddress.getIsDefault() != null && userAddress.getIsDefault() == 1) {
            // 查找用户默认地址并修改成非默认
            addressesMapper.updateAddressToNonDefault(userAddress);
        }
        // 修改收货地址
        addressesMapper.updateUserAddress(userAddress);
    }

    /**
     * 删除收货地址
     * @param id
     */
    @Override
    public void deleteAddress(Integer id) {
        addressesMapper.deleteAddress(id);
    }

    /**
     * 设置默认收货地址
     * @param id
     */
    @Override
    @Transactional
    public void setDefaultAddress(Integer id) {
        UserAddress userAddress = new UserAddress();
        userAddress.setId(id);
        userAddress.setIsDefault(1);
        userAddress.setUserId(BaseContext.getCurrentId());
        UserAddress userAddressDB = addressesMapper.selectById(id);
        if (userAddressDB != null && userAddressDB.getIsDefault() != null && userAddressDB.getIsDefault() == 1) {
            throw new RuntimeException("当前地址已经是默认地址");
        }
        addressesMapper.updateAddressToNonDefault(userAddress);
        addressesMapper.updateUserAddress(userAddress);
    }
}
