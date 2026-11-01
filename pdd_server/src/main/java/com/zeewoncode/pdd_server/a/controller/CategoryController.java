package com.zeewoncode.pdd_server.a.controller;

import com.zeewoncode.dto.CategoryCreateDTO;
import com.zeewoncode.pdd_server.a.service.CategoryService;
import com.zeewoncode.result.Result;
import com.zeewoncode.vo.CategoryAdminNode;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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

    /**
     * 新增分类
     * @param dto
     * @return
     */
    @PostMapping
    public Result addCategory(@RequestBody @Valid CategoryCreateDTO dto) {
        log.info("新增分类：{}", dto);
        categoryService.addCategory(dto);
        return Result.success();
    }
}
