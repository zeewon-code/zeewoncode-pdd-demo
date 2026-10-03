package com.zeewoncode.pdd_server.mapper;

import com.zeewoncode.entity.Category;
import com.zeewoncode.entity.Spu;
import com.zeewoncode.entity.SpuCard;
import com.zeewoncode.entity.SpuDetail;
import com.zeewoncode.req.SpuListQueryReq;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

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
    List<SpuCard> selectSpuList(SpuListQueryReq req);

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
}
