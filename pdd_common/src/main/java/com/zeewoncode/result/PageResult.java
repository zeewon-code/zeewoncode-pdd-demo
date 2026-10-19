package com.zeewoncode.result;

import com.github.pagehelper.PageInfo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

/**
 * 分页查询结果
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PageResult<T> implements Serializable {
    private Integer total; // 总记录数
    private Integer size;
    private Integer page;
    private Integer pages;
    private List<T> records;

    public static <T> PageResult<T> empty(PageInfo pageInfo) {
        PageResult<T> result = new PageResult<>();
        // 1. 复制分页元信息
        result.setPage(pageInfo.getPageNum());
        result.setSize(pageInfo.getPageSize());
        result.setPages(pageInfo.getPages());
        result.setTotal((int) pageInfo.getTotal());       // 注意：total 可能为 0 也可能不为 0
        // 2. 数据列表强制设为不可变空集合
        result.setRecords(Collections.emptyList());
        return result;
    }
    public static <T> PageResult<T> of(PageInfo pageInfo, List<T> records) {
        PageResult<T> result = new PageResult<>();
        // 1. 复制分页元信息
        result.setPage(pageInfo.getPageNum());
        result.setSize(pageInfo.getPageSize());
        result.setPages(pageInfo.getPages());
        result.setTotal((int) pageInfo.getTotal());       // 注意：total 可能为 0 也可能不为 0
        // 2. 复制数据列表
        result.setRecords(records);
        return result;
    }
}
