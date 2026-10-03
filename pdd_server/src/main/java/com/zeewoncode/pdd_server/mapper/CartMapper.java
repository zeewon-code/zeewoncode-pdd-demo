package com.zeewoncode.pdd_server.mapper;

import com.zeewoncode.entity.CartGroup;
import com.zeewoncode.entity.CartItem;
import org.apache.ibatis.annotations.*;

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

    /**
     * 更新购物车项数量
     * @param id
     * @param quantity
     */
    @Update("UPDATE cart_item SET quantity = #{quantity} WHERE id = #{id}")
    void updateCartQuantity(Long id, Integer quantity);

    /**
     * 根据ID更新购物车项选中状态
     * @param ids
     * @param selected
     */
    void updateCartSelectByIds(List<Integer> ids, Integer selected);

    /**
     * 根据ID删除购物车项
     * @param id
     */
    @Delete("DELETE FROM cart_item WHERE id = #{id}")
    void deleteCartById(Long id);

    /**
     * 根据用户ID删除已勾选购物车项
     * @param userId
     */
    @Delete("DELETE FROM cart_item WHERE user_id = #{userId} AND selected = 1")
    void deleteCartBySelected(Long userId);

    /**
     * 根据用户ID和SKU ID删除购物车项
     * @param userId
     * @param skuId
     */
    @Delete("DELETE FROM cart_item WHERE user_id = #{userId} AND sku_id = #{skuId}")
    void deleteCartItemsByUserIdAndSkuId(Integer userId, Long skuId);
}
