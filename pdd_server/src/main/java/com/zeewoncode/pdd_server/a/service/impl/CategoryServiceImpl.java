package com.zeewoncode.pdd_server.a.service.impl;

import com.zeewoncode.context.BaseContext;
import com.zeewoncode.dto.CategoryCreateDTO;
import com.zeewoncode.dto.CategoryUpdateDTO;
import com.zeewoncode.entity.Category;
import com.zeewoncode.entity.User;
import com.zeewoncode.pdd_server.a.service.CategoryService;
import com.zeewoncode.pdd_server.mapper.CategoryMapper;
import com.zeewoncode.pdd_server.mapper.SpuMapper;
import com.zeewoncode.pdd_server.mapper.UserMapper;
import com.zeewoncode.pdd_server.mapper.po.CategoryTreeRows;
import com.zeewoncode.vo.CategoryAdminNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryMapper categoryMapper;
    @Autowired
    private SpuMapper spuMapper;
    @Autowired
    private UserMapper userMapper;

    /**
     * 获取分类树
     * @return
     */
    @Override
    public List<CategoryAdminNode> getCategoryTree() {
        Long userId = BaseContext.getCurrentId().longValue();
        if (!isAdmin(userId)) {
            throw new RuntimeException("无权限访问");
        }
        List<CategoryTreeRows> rows = categoryMapper.selectCategoryTreeRows();
        Map<Long, CategoryAdminNode> nodeMap = rows.stream().collect(Collectors.toMap(CategoryTreeRows::getId, r -> {
            CategoryAdminNode node = new CategoryAdminNode();
            node.setId(r.getId());
            node.setName(r.getName());
            node.setLevel(r.getLevel());
            node.setSort(r.getSort());
            node.setSpuCount(r.getSpuCount());
            node.setChildren(new ArrayList<>());
            return node;
        }));
        // 父节点
        List<CategoryAdminNode> roots = new ArrayList<>();
        for (CategoryTreeRows r : rows) {
            CategoryAdminNode node = nodeMap.get(r.getId());
            if (r.getParentId() == 0) roots.add(node);
            else nodeMap.get(r.getParentId()).getChildren().add(node);
        }
        // 计算父节点的分类的商品数量
        roots.forEach(root -> root.setSpuCount(root.getSpuCount()
                + root.getChildren().stream().mapToInt(CategoryAdminNode::getSpuCount).sum()));
        return roots;
    }

    /**
     * 新增分类
     * @param dto
     */
    @Override
    public void addCategory(CategoryCreateDTO dto) {
        Long userId = BaseContext.getCurrentId().longValue();
        if (!isAdmin(userId)) {
            throw new RuntimeException("无权限访问");
        }
        Category category = new Category();
        category.setName(dto.getName());
        category.setParentId(dto.getParentId());
        if (dto.getSort() == null) {
            // 前端没传sort，默认是当前分类的sort的最大值+1
            Integer maxSort = categoryMapper.selectMaxSortGroupByParentId(dto.getParentId());
            if (maxSort == null) category.setSort(1);
            else category.setSort(maxSort + 1);
        }
        category.setLevel(dto.getParentId() == 0 ? 1 : 2);
        category.setCreatedAt(LocalDateTime.now());
        if (categoryMapper.insert(category) == 0) {
            throw new RuntimeException("新增分类失败");
        }
    }

    /**
     * 修改分类（改名/排序）
     * @param id
     * @param dto
     */
    @Override
    public void updateCategory(Long id, CategoryUpdateDTO dto) {
        Long userId = BaseContext.getCurrentId().longValue();
        if (!isAdmin(userId)) {
            throw new RuntimeException("无权限访问");
        }
        Category category = new Category();
        category.setId(id);
        category.setName(dto.getName());
        category.setSort(dto.getSort());
        if (categoryMapper.update(category) == 0) {
            throw new RuntimeException("修改分类失败");
        }
    }

    // 校验当前用户是否为管理员
    private boolean isAdmin(Long userId) {
        User user = userMapper.selectById(userId.intValue());
        if (user == null || user.getUserType() != 9
                || user.getStatus() != 1 || user.getDeletedFlag() != 0) {
            return false;
        }
        return true;
    }
}
