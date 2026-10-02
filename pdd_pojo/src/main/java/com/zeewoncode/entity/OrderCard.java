package com.zeewoncode.entity;

import com.zeewoncode.vo.OrderItemVO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.core.annotation.Order;

import java.io.Serializable;
import java.util.List;

/**
 * 订单卡片信息
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderCard implements Serializable {

    private Integer id;
    private String orderNo; // 订单号
    private Integer status;
    private String statusText;  //0待付款 1待发货 2待收货 3已完成 4已取消 5已关闭
    private Double totalAmount; // 订单总金额
    private Double payableAmount;  // 应付金额
    private String createAt;
    private List<OrderItemVO> items;
}
