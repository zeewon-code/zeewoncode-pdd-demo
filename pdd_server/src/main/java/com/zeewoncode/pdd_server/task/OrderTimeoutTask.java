package com.zeewoncode.pdd_server.task;

import com.zeewoncode.entity.Order;
import com.zeewoncode.pdd_server.mapper.OrderMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderTimeoutTask {

    private static final int BATCH_SIZE = 200;
    private static final long PAY_TIMEOUT_MINUTES = 15;

    private final OrderMapper orderMapper;
    private final OrderTimeoutService orderTimeoutService;

    @Scheduled(fixedDelay = 60_000)
    public void closeTimeoutOrders() {
        LocalDateTime deadline = LocalDateTime.now().minusMinutes(PAY_TIMEOUT_MINUTES);
        List<Order> orders = orderMapper.selectTimeoutOrders(deadline, BATCH_SIZE);
        for (Order order : orders) {
            try {
                orderTimeoutService.closeOne(order.getId());
            } catch (Exception e) {
                log.error("关闭超时订单失败: orderId={}", order.getId(), e);
            }
        }
    }
}
