package com.zeewoncode.pdd_server.a.controller;

import com.zeewoncode.pdd_server.a.service.CategoryService;
import com.zeewoncode.result.Result;
import com.zeewoncode.vo.CategoryAdminNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * A端-分类管理
 */
@RestController
@RequestMapping("/api/a/categories")
@Slf4j
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    /**
     * 获取分类树
     * @return
     */
    @GetMapping
    public Result<List<CategoryAdminNode>> getCategoryTree() {
        log.info("获取分类树");
        List<CategoryAdminNode> categoryAdminNodes = categoryService.getCategoryTree();
        return Result.success(categoryAdminNodes);
    }
}
