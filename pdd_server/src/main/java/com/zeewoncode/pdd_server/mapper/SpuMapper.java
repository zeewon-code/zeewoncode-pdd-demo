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
     * @param id
     * @return
     */
    @Select("SELECT * FROM spu WHERE id = #{id}")
    SpuDetail getSpuDetailById(Integer id);

    /**
     *  根据id查询商品
     * @param spuId
     * @return
     */
    @Select("SELECT * FROM spu WHERE id = #{spuId}")
    Spu getSpuById(Integer spuId);

    /**
     * 根据spuIds查询商品
     * @param spuIds
     * @return
     */
    List<Spu> selectSpuByIds(List<Integer> spuIds);

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
}
