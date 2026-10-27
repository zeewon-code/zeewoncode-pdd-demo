package com.zeewoncode.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SkuUpdateDTO implements Serializable {

    private String specs;
    private Double price;
    private String image;
}
