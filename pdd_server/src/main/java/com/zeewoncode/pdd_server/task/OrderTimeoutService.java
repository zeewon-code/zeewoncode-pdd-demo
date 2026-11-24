package com.zeewoncode.pdd_server.task;

import com.zeewoncode.entity.GrouponInstance;
import com.zeewoncode.entity.Order;
import com.zeewoncode.entity.OrderItem;
import com.zeewoncode.entity.UserCoupon;
import com.zeewoncode.pdd_server.mapper.CouponMapper;
import com.zeewoncode.pdd_server.mapper.GrouponMapper;
import com.zeewoncode.pdd_server.mapper.OrderMapper;
import com.zeewoncode.pdd_server.mapper.SkuMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderTimeoutService {

    private final OrderMapper orderMapper;
    private final SkuMapper skuMapper;
    private final CouponMapper couponMapper;
    private final GrouponMapper grouponMapper;
    private final GrouponExpireService grouponExpireService;

    @Transactional
    public void closeOne(Long orderId) {
        Order order = orderMapper.selectOrderById(orderId);
        if (order == null || order.getStatus() != 0) {
            return;
        }

        // 团长超时：整团失败，交给 failInstance 统一处理
        if (isLeaderTimeout(order)) {
            grouponExpireService.failInstance(order.getGrouponInstanceId());
            return;
        }

        // 普通订单/团员订单：只关自己
        if (orderMapper.closeTimeoutOrder(orderId) != 1) {
            return;
        }

        // 2. 释放预占库存
        for (OrderItem item : orderMapper.selectItemsByOrderId(orderId)) {
            skuMapper.releaseLockStock(item.getSkuId(), item.getQuantity());
        }

        // 3. 回退优惠券
        if (order.getDiscountAmount() != null
                && order.getDiscountAmount().compareTo(BigDecimal.ZERO) > 0) {
            couponMapper.updateUserCouponStatusAndUsedAt(UserCoupon.builder()
                    .userId(order.getUserId())
                    .orderId(orderId)
                    .status(0)
                    .usedAt(null)
                    .build());
        }

        // 4. 拼团订单减少人数
        if (order.getGrouponInstanceId() != null) {
            grouponMapper.reduceGrouponInstanceCurrentCount(order.getGrouponInstanceId());
        }

        log.info("超时关闭订单: orderId={}", orderId);
    }

    private boolean isLeaderTimeout(Order order) {
        if (order.getGrouponInstanceId() == null) {
            return false;
        }
        GrouponInstance instance =
                grouponMapper.selectGrouponInstanceById(order.getGrouponInstanceId());
        return instance != null
                && instance.getStatus() == 0
                && instance.getLeaderUserId().equals(order.getUserId());
    }
}
