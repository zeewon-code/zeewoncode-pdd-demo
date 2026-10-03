package com.zeewoncode.vo;

import com.zeewoncode.entity.CartGroup;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

import java.io.Serializable;
import java.math.BigDecimal;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CartResult implements Serializable {

    private BigDecimal totalAmount;
    private List<CartGroup> groups;

}
