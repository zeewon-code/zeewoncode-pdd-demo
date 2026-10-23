package com.zeewoncode.pdd_server.mapper;


import com.zeewoncode.dto.ShopApplyDTO;
import com.zeewoncode.entity.Merchant;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface MerchantMapper {
    /**
     * 根据ID查询商户信息
     * @param merchantId
     * @return
     */
    @Select("SELECT * FROM merchant WHERE id = #{merchantId}")
    Merchant selectMerchantById(Long merchantId);

    /**
     * 根据店铺名称查询商户信息
     * @param shopName
     * @return
     */
    @Select("SELECT * FROM merchant WHERE shop_name = #{shopName}")
    Merchant selectMerchantByShopName(String shopName);

    /**
     * 插入商户信息
     * @param merchant
     * @return
     */
    @Options(useGeneratedKeys = true, keyProperty = "id")
    @Insert("INSERT INTO merchant (shop_name, logo, status, user_id, " +
            "created_at, deleted_flag) " +
            "VALUES (#{shopName}, #{logo}, #{status}, #{userId}, " +
            "#{createdAt}, #{deletedFlag})")
    int insert(Merchant merchant);

    /**
     * 根据用户ID查询商户信息
     * @param userId
     * @return
     */
    @Select("SELECT * FROM merchant WHERE user_id = #{userId}")
    Merchant selectMerchantByUserId(Long userId);
}
