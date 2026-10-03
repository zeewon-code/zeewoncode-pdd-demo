package com.zeewoncode.pdd_server.mapper;

import com.zeewoncode.entity.Sku;
import com.zeewoncode.req.OrderItemReq;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;


@Mapper
public interface SkuMapper {
    /**
     * 根据spu_id查询sku列表
     * @param id
     * @return
     */
    @Select("select id, spu_id, specs, price, stock, locked_stock, image, status " +
            "from sku " +
            "where spu_id = #{id} AND deleted_flag = 0 " +
            "ORDER BY price ASC")
    List<Sku> selectSkuListBySpuId(Integer id);

    /**
     * 根据id查询sku
     * @param skuId
     * @return
     */
    @Select("select * from sku where id = #{skuId} and stock > 0 and status = 1 and deleted_flag = 0")
    Sku selectSkuById(Long skuId);

    /**
     * 预占库存
     * @param items
     * @return
     */
    boolean lockStock(List<OrderItemReq> items);
}
