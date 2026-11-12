package com.zeewoncode.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BDashboard implements Serializable {

    private BigDecimal todaySales;
    private Long todayOrderCount;
    private BigDecimal totalSales;
    private Long totalOrderCount;
    private Long pendingShipCount;
    private List<LowStockSkus> lowStockSkus; // 低库存
}
