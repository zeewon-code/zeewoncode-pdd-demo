package com.zeewoncode.pdd_server.c.service.impl;

import com.zeewoncode.context.BaseContext;
import com.zeewoncode.entity.Order;
import com.zeewoncode.entity.OrderItem;
import com.zeewoncode.entity.Shipment;
import com.zeewoncode.pdd_server.c.service.ShipmentService;
import com.zeewoncode.pdd_server.mapper.OrderMapper;
import com.zeewoncode.pdd_server.mapper.ShipmentMapper;
import com.zeewoncode.pdd_server.mapper.SpuMapper;
import com.zeewoncode.vo.ShipmentStep;
import com.zeewoncode.vo.ShipmentVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Service
public class ShipmentServiceImpl implements ShipmentService {

    @Autowired
    private ShipmentMapper shipmentMapper;
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private SpuMapper spuMapper;

    /**
     * 根据id获取物流信息
     * @param shipmentId
     * @return
     */
    @Override
    public ShipmentVO getShipment(Long shipmentId) {
        // 调用Mapper层根据id获取物流信息
        Shipment shipment = shipmentMapper.selectShipmentById(shipmentId);
        if (shipment == null) {
            throw new RuntimeException("Shipment not found");
        }
        ShipmentVO shipmentVO = new ShipmentVO();
        BeanUtils.copyProperties(shipment, shipmentVO);
        shipmentVO.setShipmentId(shipment.getId());
        List<ShipmentStep> steps = new ArrayList<>();
        steps.add(new ShipmentStep(LocalDateTime.now().plusHours(3).truncatedTo(ChronoUnit.SECONDS), "已收取快件"));
        steps.add(new ShipmentStep(LocalDateTime.now().plusHours(6).truncatedTo(ChronoUnit.SECONDS), "快件正在运送中"));
        steps.add(new ShipmentStep(LocalDateTime.now().plusDays(2).truncatedTo(ChronoUnit.SECONDS), "快件正在派送中"));
        steps.add(new ShipmentStep(LocalDateTime.now().plusDays(3).plusHours(2).truncatedTo(ChronoUnit.SECONDS), "快件已送达"));
        shipmentVO.setSteps(steps);
        return shipmentVO;
    }

    /**
     * 确认收货
     * @param id
     */
    @Override
    @Transactional
    public void receive(Long id) {
        //shipment置3已签收 → 订单2待收货→3已完成 → 累加 spu.sales。
        //shipment置3已签收
        int i = shipmentMapper.updateShipmentStatus(id, 3);
        if (i <= 0) {
            throw new RuntimeException("订单签收失败");
        }
        //订单状态修改为3已完成
        Shipment shipment = shipmentMapper.selectShipmentById(id);
        Long orderId = shipment.getOrderId();
        Order order = new Order();
        order.setStatus(3);
        order.setId(orderId);
        order.setUserId(BaseContext.getCurrentId().longValue());
        order.setCompletedAt(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));
        orderMapper.update(order);
        //累加 spu.sales。
        List<OrderItem> orderItems = orderMapper.selectItemsByOrderId(orderId);
        Integer totalQuantity = 0;
        for (OrderItem orderItem : orderItems) {
            Integer quantity = orderItem.getQuantity();
            totalQuantity += quantity;
        }
        int result = spuMapper.updateSpuSalesById(orderItems.get(0).getSpuId(), totalQuantity);
        if (result <= 0) {
            throw new RuntimeException("更新商品销售量失败");
        }
    }
}
