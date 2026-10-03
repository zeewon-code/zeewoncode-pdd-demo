package com.zeewoncode.pdd_server.mapper;

import com.zeewoncode.entity.CartGroup;
import com.zeewoncode.entity.CartItem;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface CartMapper {
    /**
     * 根据用户ID查询购物车列表
     * @param userId
     * @return
     */
    @Select("SELECT * FROM cart_item WHERE user_id = #{userId}")
    List<CartItem> selectCartItemByUserId(Integer userId);


}
