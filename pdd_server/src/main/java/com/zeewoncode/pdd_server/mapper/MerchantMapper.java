package com.zeewoncode.pdd_server.mapper;


import com.zeewoncode.entity.Merchant;
import org.apache.ibatis.annotations.Mapper;
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
}
