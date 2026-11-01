package com.zeewoncode.pdd_server.a.service;

import com.zeewoncode.dto.CategoryCreateDTO;
import com.zeewoncode.dto.CategoryUpdateDTO;
import com.zeewoncode.vo.CategoryAdminNode;

import java.util.List;

public interface CategoryService {
    /**
     * 获取分类树
     * @return
     */
    List<CategoryAdminNode> getCategoryTree();

    /**
     * 新增分类
     * @param dto
     */
    void addCategory(CategoryCreateDTO dto);

    /**
     * 修改分类（改名/排序）
     * @param id
     * @param dto
     */
    void updateCategory(Long id, CategoryUpdateDTO dto);

    /**
     * 删除分类（有子类或挂商品则冲突）
     * @param id
     */
    void delete(Long id);
}
