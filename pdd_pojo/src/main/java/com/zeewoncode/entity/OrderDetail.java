package com.zeewoncode.entity;

import com.zeewoncode.vo.OrderItemVO;
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
public class OrderDetail implements Serializable {

    private Integer id;
    private String orderNo;
    private Integer status;
    private String statusText;
    private Double totalAmount;
    private Double discountAmount;
    private Double payableAmount;
    private Receiver receiver;
    private List<OrderItemVO> items;
    private Shipment shipment;
}
