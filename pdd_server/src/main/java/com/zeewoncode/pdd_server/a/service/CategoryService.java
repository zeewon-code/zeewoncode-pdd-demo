package com.zeewoncode.pdd_server.a.service;

import com.zeewoncode.vo.CategoryAdminNode;

import java.util.List;

public interface CategoryService {
    /**
     * 获取分类树
     * @return
     */
    List<CategoryAdminNode> getCategoryTree();
}
