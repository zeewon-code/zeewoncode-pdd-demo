package com.zeewoncode.pdd_server.mapper;

import com.zeewoncode.entity.UserAddress;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface AddressesMapper {
    /**
     * 根据用户ID获取收货地址列表
     * @param userId
     * @return
     */
    @Select("select * from user_address where user_id = #{userId}")
    List<UserAddress> selectAddressesByUserId(Integer userId);

    /**
     * 将用户其他收货地址修改为非默认
     */
    @Update("update user_address set is_default = 0 where user_id = #{userId}")
    void updateAddressToNonDefault(UserAddress userAddress);

    /**
     * 添加收货地址
     * @param userAddress
     */
    @Insert("insert into user_address (user_id, receiver_name, receiver_phone, province, city, district, detail, is_default) " +
            "values (#{userId}, #{receiverName}, #{receiverPhone}, #{province}, #{city}, #{district}, #{detail}, #{isDefault})")
    void insertAddress(UserAddress userAddress);


    /**
     * 修改收货地址
     * @param userAddress
     */
    void updateUserAddress(UserAddress userAddress);

    /**
     * 删除收货地址
     * @param id
     */
    @Delete("delete from user_address where id = #{id}")
    void deleteAddress(Integer id);


    /**
     * 根据ID获取收货地址信息
     * @param id
     * @return
     */
    @Select("select * from user_address where id = #{id}")
    UserAddress selectById(Integer id);
}
