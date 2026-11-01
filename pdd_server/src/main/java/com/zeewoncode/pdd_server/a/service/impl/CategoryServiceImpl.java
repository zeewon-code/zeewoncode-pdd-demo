package com.zeewoncode.pdd_server.a.service.impl;

import com.zeewoncode.context.BaseContext;
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
