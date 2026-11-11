package com.zeewoncode.pdd_server.mapper;

import com.zeewoncode.entity.Category;
import com.zeewoncode.pdd_server.mapper.po.CategoryTreeRows;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
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

    /**
     * 根据parentId查找同一分类下排序基准的最大值
     * @param parentId
     * @return
     */
    @Select("select MAX(sort) from category where parent_id = #{parentId} group by parent_id")
    Integer selectMaxSortGroupByParentId(Long parentId);

    /**
     * 新增分类
     * @param category
     * @return
     */
    @Insert("insert into category (parent_id, name, level, sort, created_at) " +
            "values (#{parentId}, #{name}, #{level}, #{sort}, #{createdAt})")
    int insert(Category category);

    /**
     * 修改分类（改名/排序）
     * @param category
     * @return
     */
    int update(Category category);

    /**
     * 查询该分类下是否存在子类
     * @param parentId
     * @return
     */
    @Select("select count(*) from category where parent_id = #{parentId} ")
    int selectIsParentIdById(Long parentId);

    /**
     * 删除分类
     * @param categoryId
     * @return
     */
    @Delete("delete from category where id = #{categoryId}")
    int delete(Long categoryId);
}
