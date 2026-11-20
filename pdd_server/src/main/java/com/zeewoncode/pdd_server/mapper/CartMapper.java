package com.zeewoncode.pdd_server.mapper;

import com.zeewoncode.entity.CartItem;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface CartMapper {
    /**
     * 根据用户ID和skuId查询购物车列表
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
     */
    int updateCartItem(CartItem cartItem);

    /**
     * 插入购物车项
     * @param cartItem
     */
    @Insert("INSERT INTO cart_item (sku_id, quantity, selected, spu_id, user_id) " +
            "VALUES (#{skuId}, #{quantity}, #{selected}, #{spuId}, #{userId})")
    int insert(CartItem cartItem);

    /**
     * 更新购物车项数量
      * @param cartItemId
     * @param quantity
     */
    @Update("UPDATE cart_item SET quantity = #{quantity} WHERE id = #{cartItemId}")
    int updateCartQuantity(Long cartItemId, Integer quantity);

    /**
     * 根据ID更新购物车项选中状态
      * @param cartItemIds
     * @param selected
     */
    int updateCartSelectByIds(List<Long> cartItemIds, Integer selected);

    /**
     * 根据ID删除购物车项
     * @param cartItemId
     */
    @Delete("DELETE FROM cart_item WHERE id = #{cartItemId}")
    int deleteCartById(Long cartItemId);

    /**
     * 根据用户ID删除已勾选购物车项
     * @param userId
     */
    @Delete("DELETE FROM cart_item WHERE user_id = #{userId} AND selected = 1")
    int deleteCartBySelected(Long userId);

    /**
     * 根据用户ID和SKU ID删除购物车项
     * @param userId
     * @param skuId
     */
    @Delete("DELETE FROM cart_item WHERE user_id = #{userId} AND sku_id = #{skuId}")
    int deleteCartItemsByUserIdAndSkuId(Long userId, Long skuId);

    /**
     * 根据用户ID和SKU ID列表删除购物车项
     * @param userId
     * @param skuIds
     */
    int deleteCartItemsByUserIdAndSkuIds(Long userId, List<Long> skuIds);
}
