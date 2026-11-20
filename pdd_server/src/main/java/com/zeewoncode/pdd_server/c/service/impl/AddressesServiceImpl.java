package com.zeewoncode.pdd_server.c.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.zeewoncode.constant.ErrorCode;
import com.zeewoncode.entity.UserAddress;
import com.zeewoncode.exception.BaseException;
import com.zeewoncode.pdd_server.c.service.AddressesService;
import com.zeewoncode.pdd_server.mapper.AddressesMapper;
import com.zeewoncode.dto.AddressDTO;
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
    public List<AddressVO> getAddresses(Long userId) {
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
    public void addAddress(AddressDTO addressDTO, Long userId) {
        UserAddress userAddress = new UserAddress();
        BeanUtils.copyProperties(addressDTO, userAddress);
        userAddress.setDeletedFlag(0);
        userAddress.setUserId(userId);
        if (addressDTO.getIsDefault() != null) {
            userAddress.setIsDefault(Boolean.TRUE.equals(addressDTO.getIsDefault()) ? 1 : 0);
        } else {
            userAddress.setIsDefault(0);
        }
        if (userAddress.getIsDefault() != null && userAddress.getIsDefault() == 1) {
            // 查找用户默认地址并修改成非默认
            if (addressesMapper.updateAddressToNonDefault(userAddress) <= 0) {
                throw new BaseException(ErrorCode.USER_ADDRESS_ISNOTDEFAULT_FAILED);
            }
        }
        // 插入新地址
        if (addressesMapper.insertAddress(userAddress) <= 0) {
            throw new BaseException(ErrorCode.USER_ADDRESS_ADD_FAILED);
        }
    }

    /**
     * 修改收货地址
     * @param addressDTO
     */
    @Override
    @Transactional
    public void updateAddress(AddressDTO addressDTO, Long userId) {
        UserAddress userAddress = new UserAddress();
        BeanUtils.copyProperties(addressDTO, userAddress);
        userAddress.setDeletedFlag(0);
        userAddress.setUserId(userId);
        if (addressDTO.getIsDefault() != null) {
            userAddress.setIsDefault(Boolean.TRUE.equals(addressDTO.getIsDefault()) ? 1 : 0);
        } else {
            userAddress.setIsDefault(0);
        }
        if (userAddress.getIsDefault() != null && userAddress.getIsDefault() == 1) {
            if (addressesMapper.updateAddressToNonDefault(userAddress) <= 0) {
                throw new BaseException(ErrorCode.USER_ADDRESS_ISNOTDEFAULT_FAILED);
            }
        }
        // 修改收货地址
        if (addressesMapper.updateUserAddress(userAddress) <= 0) {
            throw new BaseException(ErrorCode.USER_ADDRESS_UPDATE_FAILED);
        }
    }

    /**
     * 删除收货地址
     * @param id
     */
    @Override
    public void deleteAddress(Long id) {
        UserAddress userAddress = addressesMapper.selectById(id);
        if (userAddress == null) {
            throw new BaseException(ErrorCode.USER_ADDRESS_NOT_EXIST);
        }
        if (userAddress.getIsDefault() == 1) {
            throw new BaseException(ErrorCode.USER_ADDRESS_DEFAULT_CANNOT_DELETE);
        }
        if (addressesMapper.deleteAddress(id) <= 0) {
            throw new BaseException(ErrorCode.USER_ADDRESS_DELETE_FAILED);
        }
    }

    /**
     * 设置默认收货地址
     * @param id
     */
    @Override
    @Transactional
    public void setDefaultAddress(Long id, Long userId) {
        UserAddress userAddress = new UserAddress();
        userAddress.setId(id);
        userAddress.setIsDefault(1);
        userAddress.setUserId(userId);
        UserAddress userAddressDB = addressesMapper.selectById(id);
        if (userAddressDB != null && userAddressDB.getIsDefault() == 1) {
            throw new BaseException(ErrorCode.USER_ADDRESS_ISDEFAULT_ALREADY);
        }
        if (addressesMapper.updateAddressToNonDefault(userAddress) <= 0) {
            throw new BaseException(ErrorCode.USER_ADDRESS_ISNOTDEFAULT_FAILED);
        }
        if (addressesMapper.updateUserAddress(userAddress) <= 0) {
            throw new BaseException(ErrorCode.USER_ADDRESS_ISDEFAULT_FAILED);
        }
    }

    /**
     * 获取收货地址详情
     * @param addressId
     * @param userId
     * @return
     */
    @Override
    public AddressVO getAddressDetail(Long addressId, Long userId) {
        UserAddress userAddress = addressesMapper.selectById(addressId);
        if (userAddress == null || !userAddress.getUserId().equals(userId)) {
            throw new BaseException(ErrorCode.USER_ADDRESS_NOT_EXIST);
        }
        return BeanUtil.copyProperties(userAddress, AddressVO.class);
    }
}
