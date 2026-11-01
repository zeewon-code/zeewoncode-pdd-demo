package com.zeewoncode.dto;


import lombok.Data;

import java.io.Serializable;

@Data
public class CategoryUpdateDTO implements Serializable {

    private String name;
    private Integer sort;
}
