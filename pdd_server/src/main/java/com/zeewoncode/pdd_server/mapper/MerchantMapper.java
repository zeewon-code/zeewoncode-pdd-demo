package com.zeewoncode.pdd_server.mapper;


import com.zeewoncode.dto.ShopApplyDTO;
import com.zeewoncode.entity.Merchant;
import com.zeewoncode.vo.MerchantVO;
import org.apache.ibatis.annotations.*;

import java.util.List;

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

    /**
     * 更新商户信息
     * @param merchant
     * @return
     */
    int update(Merchant merchant);

    /**
     * 根据店铺状态和店铺名关键字查询商户列表
     * @param status
     * @param keyword
     * @return
     */
    List<MerchantVO> selectList(Integer status, String keyword);

    /**
     * 根据商户ID和状态更新商户状态
     * @param merchantId
     * @param status
     * @return
     */
    @Update("UPDATE merchant SET status = #{status} WHERE id = #{merchantId}")
    int updateMerchantStatus(Long merchantId, Integer status);

    /**
     * 查询商户总数
     * @return
     */
    @Select("SELECT COUNT(*) FROM merchant WHERE deleted_flag = 0")
    Long selectCount();
}
