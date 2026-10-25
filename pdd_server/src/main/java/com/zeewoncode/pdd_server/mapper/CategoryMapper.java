package com.zeewoncode.pdd_server.mapper;

import com.zeewoncode.entity.Category;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface CategoryMapper {


    @Select("SELECT * FROM category WHERE id = #{categoryId}")
    Category selectCategoryById(Long categoryId);
}
