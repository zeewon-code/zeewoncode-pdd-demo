package com.zeewoncode.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryAdminNode implements Serializable {

    private Long id;
    private Long parentId;
    private String name;
    private Integer level;
    private Integer sort;
    private Integer spuCount;
    private List<CategoryAdminNode> children;
}
