package com.zeewoncode.pdd_server.a.service.impl;


import com.zeewoncode.pdd_server.a.service.DashboardService;
import com.zeewoncode.pdd_server.mapper.MerchantMapper;
import com.zeewoncode.pdd_server.mapper.OrderMapper;
import com.zeewoncode.pdd_server.mapper.SpuMapper;
import com.zeewoncode.pdd_server.mapper.UserMapper;
import com.zeewoncode.vo.ADashboard;
import com.zeewoncode.vo.OrderStatusDist;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

@Service
public class DashboardServiceImpl implements DashboardService {

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private MerchantMapper merchantMapper;
    @Autowired
    private SpuMapper spuMapper;
    @Autowired
    private OrderMapper orderMapper;

    /**
     * 获取平台看板数据
     * @return
     */
    @Override
    public ADashboard getDashboardData() {
        // userCount, merchantCount, spuCount, totalOrderCount, todayOrderCount
        // todayPaidOrderCount, todayGmv, totalGmv
        // orderStatusDist
        // userCount
        Long userCount = userMapper.selectCountByUserType(1);
        // merchantCount
        Long merchantCount = merchantMapper.selectCount();
        // spuCount
        Long spuCount = spuMapper.selectCount();
        // totalOrderCount
        Long totalOrderCount = orderMapper.selectOrderCount(null, null);
        // todayOrderCount
        LocalDateTime now = LocalDate.now().atStartOfDay();
        LocalDateTime nowPlusOneDay = now.plusDays(1);
        Long todayOrderCount = orderMapper.selectOrderCount(now, nowPlusOneDay);
        // todayPaidOrderCount
        Long todayPaidOrderCount = orderMapper.selectPaidCount(now, nowPlusOneDay);
        // todayGmv
        BigDecimal todayGmv = orderMapper.selectGmv(now, nowPlusOneDay).setScale(2, RoundingMode.HALF_UP);
        // totalGmv
        BigDecimal totalGmv = orderMapper.selectGmv(null, null).setScale(2, RoundingMode.HALF_UP);
        // orderStatusDist
        List<OrderStatusDist> dist = orderMapper.selectOrderStatusDist();
        ADashboard aDashboard = ADashboard.builder()
                .userCount(userCount)
                .merchantCount(merchantCount)
                .spuCount(spuCount)
                .totalOrderCount(totalOrderCount)
                .todayOrderCount(todayOrderCount)
                .todayPaidOrderCount(todayPaidOrderCount)
                .todayGmv(todayGmv)
                .totalGmv(totalGmv)
                .orderStatusDist(dist)
                .build();
        if (dist.size() < 6) {
            List<Integer> all = Arrays.asList(0,1,2,3,4,5);
            List<Integer> exist = new ArrayList<>();
            List<Integer> res = new ArrayList<>();
            for (OrderStatusDist item : dist) exist.add(item.getStatus());
            for (Integer num : all) {
                // 如果exist里面不包含这个元素，就add进去
                if (!exist.contains(num)) res.add(num);
            }

            for (Integer code : res) {
                OrderStatusDist orderStatusDist = new OrderStatusDist(
                        code,
                        0L,
                        OrderStatusEnum.getByCode(code).getDesc()
                );
                dist.add(orderStatusDist);
            }
            aDashboard.setOrderStatusDist(dist);
        }
        dist.sort(Comparator.comparingInt(OrderStatusDist::getStatus));
        return aDashboard;
    }

    @Getter
    private enum OrderStatusEnum {
        WAIT_PAY(0, "待付款"),
        WAIT_SEND(1, "待发货"),
        WAIT_RECEIVE(2, "待收货"),
        COMPLETED(3, "已完成"),
        CANCELLED(4, "已取消"),
        CLOSED_TIMEOUT(5, "已关闭(超时)");

        private final Integer code;
        private final String desc;

        OrderStatusEnum(Integer code, String desc) {
            this.code = code;
            this.desc = desc;
        }

        // 根据code查找枚举（循环遍历，就是前面讲的循环+枚举）
        public static OrderStatusEnum getByCode(Integer code) {
            for (OrderStatusEnum status : OrderStatusEnum.values()) {
                if (status.getCode().equals(code)) {
                    return status;
                }
            }
            return null;
        }
    }

}
