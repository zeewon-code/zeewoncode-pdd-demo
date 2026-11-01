package com.zeewoncode.pdd_server.mapper;

import com.zeewoncode.entity.Category;
import com.zeewoncode.pdd_server.mapper.po.CategoryTreeRows;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface CategoryMapper {


    @Select("SELECT * FROM category WHERE id = #{categoryId}")
    Category selectCategoryById(Long categoryId);

    /**
     * 查询所有商品分类，按sort升序
     * @return
     */
    @Select("SELECT * FROM category ORDER BY sort ASC")
    List<Category> selectByAsc();

    List<CategoryTreeRows> selectCategoryTreeRows();

}
