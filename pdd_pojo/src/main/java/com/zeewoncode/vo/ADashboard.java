package com.zeewoncode.vo;

import lombok.Builder;
import lombok.Data;
import java.io.Serializable;
import java.util.List;

@Data
@Builder
public class ADashboard implements Serializable {

    private Long userCount; // user_type=1
    private Long merchantCount; // 含停业，不含软删
    private Long spuCount; // 含下架，不含软删
    private Long totalOrderCount; // 全部状态
    private Long todayOrderCount; // 按created_at,全部状态
    private Long todayPaidOrderCount;// 按paid_at, status=1/2/3
    private Double todayGmv; // 今日支付成功订单的实付金额合计，单位元，两位小数
    private Double totalGmv; // 全部支付成功订单的实付金额合计，单位元，两位小数
    private List<OrderStatusDist> orderStatusDist; //按当前状态，固定6项，无订单时 count=0
}
