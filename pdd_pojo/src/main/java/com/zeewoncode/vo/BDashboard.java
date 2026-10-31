package com.zeewoncode.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BDashboard implements Serializable {

    private Double todaySales;
    private Long todayOrderCount;
    private Double totalSales;
    private Long totalOrderCount;
    private Long pendingShipCount;
    private List<LowStockSkus> lowStockSkus; // 低库存
}
