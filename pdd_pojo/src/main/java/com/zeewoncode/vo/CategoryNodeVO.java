package com.zeewoncode.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Slf4j
public class CategoryNodeVO implements Serializable {

    private Long id;
    private String name;
    private Integer level;
    private List<CategoryNodeVO> children;
}
