package com.zeewoncode.pdd_server.mapper;

import com.zeewoncode.entity.Category;
import com.zeewoncode.entity.Spu;
import com.zeewoncode.entity.SpuCard;
import com.zeewoncode.entity.SpuDetail;
import com.zeewoncode.dto.SpuListQueryDTO;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface SpuMapper {
    /**
     * 查询所有商品分类，按sort升序
     * @return
     */
    @Select("SELECT * FROM category ORDER BY sort ASC")
    List<Category> selectListOrderByAsc();

    /**
     * 查询商品列表
     * @param req
     * @return
     */
    List<SpuCard> selectSpuList(SpuListQueryDTO req);

    /**
     * 根据id查询商品详情
     * @param spuId
     * @return
     */
    @Select("SELECT * FROM spu WHERE id = #{spuId}")
    SpuDetail getSpuDetailById(Long spuId);

    /**
     *  根据id查询商品
     * @param spuId
     * @return
     */
    @Select("SELECT * FROM spu WHERE id = #{spuId}")
    Spu getSpuById(Long spuId);

    /**
     * 根据spuIds查询商品
     * @param spuIds
     * @return
     */
    List<Spu> selectSpuByIds(List<Long> spuIds);

    /**
     * 根据spuId更新商品销售量
     * @param spuId
     * @param totalQuantity
     * @return
     */
    @Update("UPDATE spu SET sales = sales + #{totalQuantity} WHERE id = #{spuId}")
    int updateSpuSalesById(Long spuId, Integer totalQuantity);

    /**
     * 根据merchantId查询商品
     * @param merchantId
      * @param keyword
      * @param categoryId
      * @param status
     * @return
     */
    List<Spu> selectSpuByMerchantId(Long merchantId, String keyword, Long categoryId, Integer status);

    /**
     * 插入商品
     * @param spu
     */
    int insert(Spu spu);

    /**
     * 更新商品
     * @param spu
     * @return
     */
    int update(Spu spu);

    /**
     * 软删除商品
     * @param spuId
     * @return
     */
    @Delete("UPDATE spu SET deleted_flag = 1 WHERE id = #{spuId}")
    int softDeleteSpu(Long spuId);

    /**
     * 上下架商品
     * @param spuId
     * @param status
     * @return
     */
    @Update("UPDATE spu SET status = #{status} WHERE id = #{spuId} AND deleted_flag = 0")
    int listAndDelist(Long spuId, Integer status);

    /**
     * 根据merchantId查询商品id
     * @param merchantId
     * @return
     */
    @Select("SELECT id FROM spu " +
            "WHERE merchant_id = #{merchantId} AND deleted_flag = 0")
    List<Long> selectIdByMerchantId(Long merchantId);

    /**
     * 根据merchantId查询商品id
     * @param merchantId
     * @return
     */
    @Select("SELECT id FROM spu " +
            "WHERE merchant_id = #{merchantId} AND deleted_flag = 0")
    List<Long> selectIdsByMerchantId(Long merchantId);

    /**
     * 根据分类id查询商品行数
     * @param categoryId
     * @return
     */
    @Select("select count(*) from spu where category_id = #{categoryId} AND deleted_flag = 0")
    int selectSpuCountByCategoryId(Long categoryId);

    /**
     * 根据merchantId、状态、分类id查询商品id
     * @param merchantId
     * @param status
     * @param categoryId
     * @return
     */
    List<Long> selectIds(Long merchantId, Integer status, Long categoryId);

    /**
     * 商品总数
     * @return
     */
    @Select("select count(*) from spu where deleted_flag = 0")
    Long selectCount();
}
