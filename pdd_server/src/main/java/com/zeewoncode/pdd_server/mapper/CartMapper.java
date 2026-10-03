package com.zeewoncode.pdd_server.mapper;

import com.zeewoncode.entity.CartGroup;
import com.zeewoncode.entity.CartItem;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface CartMapper {
    /**
     * 根据用户ID查询购物车列表
     * @param userId
     * @param skuId
     * @return
     */
    @Select("SELECT * FROM cart_item WHERE user_id = #{userId} AND sku_id = #{skuId}")
    List<CartItem> selectCartItemByUserIdAndSkuId(Long userId, Long skuId);


    /**
     * 根据用户ID查询购物车列表
     * @param userId
     * @return
     */
    @Select("SELECT * FROM cart_item WHERE user_id = #{userId}")
    List<CartItem> selectCartItemByUserId(Long userId);

    /**
     * 更新购物车项
     * @param cartItem
     * @param userId
     */
    void updateCartItem(CartItem cartItem, Long userId);

    /**
     * 插入购物车项
     * @param cartItem
     */
    @Insert("INSERT INTO cart_item (sku_id, quantity, selected, spu_id, user_id) " +
            "VALUES (#{skuId}, #{quantity}, #{selected}, #{spuId}, #{userId})")
    void insert(CartItem cartItem);
}
