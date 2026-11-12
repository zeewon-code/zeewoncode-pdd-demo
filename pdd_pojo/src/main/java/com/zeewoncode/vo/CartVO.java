package com.zeewoncode.vo;

import com.zeewoncode.entity.CartGroup;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

import java.io.Serializable;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CartVO implements Serializable {

    private BigDecimal totalAmount;
    private List<CartGroup> groups;

}
