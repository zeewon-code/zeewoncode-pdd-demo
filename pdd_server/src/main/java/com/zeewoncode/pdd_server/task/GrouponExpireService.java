package com.zeewoncode.pdd_server.task;

import com.zeewoncode.entity.GrouponInstance;
import com.zeewoncode.entity.Order;
import com.zeewoncode.entity.OrderItem;
import com.zeewoncode.entity.UserCoupon;
import com.zeewoncode.pdd_server.mapper.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class GrouponExpireService {

    private final GrouponMapper grouponMapper;
    private final OrderMapper orderMapper;
    private final SkuMapper skuMapper;
    private final CouponMapper couponMapper;
    private final PaymentMapper paymentMapper;

    @Transactional
    public void expireOne(Long instanceId) {
        GrouponInstance instance = grouponMapper.selectGrouponInstanceById(instanceId);
        if (instance == null || instance.getStatus() != 0
                || instance.getExpireAt().isAfter(LocalDateTime.now())) {
            return;
        }

        // 满员：补记成团
        if (instance.getCurrentCount() >= instance.getTargetCount()) {
            grouponMapper.closeInstance(instanceId, 1);
            log.info("拼团成团: instanceId={}", instanceId);
            return;
        }

        // 未满员：整团失败
        failInstance(instanceId);
    }


    /**
     * 让整个拼团失败：实例置 2，取消团内所有订单，处理库存/退款/优惠券
     * 可由「拼团过期」和「团长订单超时」两个入口调用
     */
    @Transactional
    public void failInstance(Long instanceId) {
        GrouponInstance instance = grouponMapper.selectGrouponInstanceById(instanceId);

        // 不存在或已经处理过（已成团/已失败/已取消）直接返回
        if (instance == null || instance.getStatus() != 0) {
            return;
        }

        // 条件更新 0 -> 2，保证只处理一次
        if (grouponMapper.closeInstance(instanceId, 2) != 1) {
            return;
        }

        for (Order order : orderMapper.selectGrouponOrders(instanceId)) {
            // 条件取消：只有 待付款/待发货 的订单才处理
            if (orderMapper.cancelGrouponOrder(order.getId()) != 1) {
                continue;
            }
            handleOrder(order);
        }

        log.info("拼团失败: instanceId={}, current={}/{}",
                instanceId, instance.getCurrentCount(), instance.getTargetCount());
    }

    private void handleOrder(Order order) {
        List<OrderItem> items = orderMapper.selectItemsByOrderId(order.getId());
        for (OrderItem item : items) {
            if (order.getStatus() == 0) {
                // 未支付：只释放预占
                skuMapper.releaseLockStock(item.getSkuId(), item.getQuantity());
            } else {
                // 已支付：库存已真实扣减，需要加回来
                skuMapper.restoreStock(item.getSkuId(), item.getQuantity());
            }
        }

        if (order.getStatus() == 1) {
            paymentMapper.refundByOrderId(order.getId());
        }

        if (order.getDiscountAmount() != null
                && order.getDiscountAmount().compareTo(BigDecimal.ZERO) > 0) {
            couponMapper.updateUserCouponStatusAndUsedAt(UserCoupon.builder()
                    .userId(order.getUserId())
                    .orderId(order.getId())
                    .status(0)
                    .usedAt(null)
                    .build());
        }
    }
}
