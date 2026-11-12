package com.zeewoncode.entity;

import com.zeewoncode.vo.OrderItemVO;
import com.zeewoncode.vo.ShipVO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderDetail implements Serializable {

    private Long id;
    private String orderNo;
    private Integer status;
    private String statusText;
    private BigDecimal totalAmount;
    private BigDecimal discountAmount;
    private BigDecimal payableAmount;
    private Receiver receiver;
    private List<OrderItemVO> items;
    private ShipVO shipVO;
}
