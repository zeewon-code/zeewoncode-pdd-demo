package com.zeewoncode.pdd_server.b.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zeewoncode.dto.ShipDTO;
import com.zeewoncode.entity.*;
import com.zeewoncode.pdd_server.b.service.OrderService;
import com.zeewoncode.pdd_server.mapper.MerchantMapper;
import com.zeewoncode.pdd_server.mapper.OrderMapper;
import com.zeewoncode.pdd_server.mapper.ShipmentMapper;
import com.zeewoncode.result.PageResult;
import com.zeewoncode.vo.ShipVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private MerchantMapper merchantMapper;
    @Autowired
    private ShipmentMapper shipmentMapper;

    /**
     * 获取本店订单列表
     * @param status
     * @param page
     * @param size
     * @return
     */
    @Override
    public PageResult<OrderCard> getOrderList(Integer status, Integer page, Integer size, Integer userId) {
        if (!haveMerchantOperation(userId)) {
            throw new RuntimeException("当前用户没有权限进行相关操作");
        }
        Merchant merchant = merchantMapper.selectMerchantByUserId(userId.longValue());
        // 1.启动分页
        PageHelper.startPage(page, size);
        // 2.查询订单列表
        List<OrderCard> orderList = orderMapper.selectOrderList(null, status, merchant.getId(), null, null);
        // 3.包装分页结果
        PageInfo<OrderCard> pageInfo = new PageInfo<>(orderList);
        if (orderList == null) {
            return PageResult.empty(pageInfo);
        }
        return PageResult.of(pageInfo, orderList);
    }

    /**
     * 获取本店订单详情
     * @param orderId
     * @return
     */
    @Override
    public OrderDetail getOrderDetail(Long orderId, Integer userId) {
        if (!haveMerchantOperation(userId)) {
            throw new RuntimeException("当前用户没有权限进行相关操作");
        }
        OrderDetail orderDetail = orderMapper.selectOrderDetail(userId.longValue(), orderId.intValue());
        // 1.补充receiver类字段
        Receiver receiver = orderMapper.selectReceiverByOrderId(orderId.intValue());
        // 2.补充ShipVO类字段
        Shipment shipment = shipmentMapper.selectShipmentByOrderId(orderId.intValue());
        ShipVO shipVO = new ShipVO();
        if (shipment != null) {
            shipVO = ShipVO.builder()
                    .shipmentId(shipment.getId() != null ? shipment.getId() : null)
                    .trackingNo(shipment.getTrackingNo() != null ? shipment.getTrackingNo() : null)
                    .build();
        }
        orderDetail.setShipVO(shipVO);
        orderDetail.setReceiver(receiver != null ? receiver : null);
        return orderDetail;
    }

    /**
     * 订单发货
     * @param orderId
     * @param dto
     * @return
     */
    @Override
    public ShipVO ship(Long orderId, ShipDTO dto, Integer userId) {
        if (!haveMerchantOperation(userId)) {
            throw new RuntimeException("当前用户没有权限进行相关操作");
        }
        // 查询该订单是否存在
        Order orderDb = orderMapper.selectOrderById(orderId);
        if (orderDb == null || orderDb.getStatus() != 1) {
            throw new RuntimeException("订单不存在或订单状态异常，无法发货");
        }
        // 2.更新订单状态
        Order order = Order.builder().id(orderId).status(2).build();
        if (orderMapper.update(order) != 1) {
            throw new RuntimeException("订单状态更新失败");
        }
        // 3.插入物流信息
        Shipment shipment = Shipment.builder()
                .orderId(orderId)
                .trackingNo(dto.getTrackingNo())
                .carrier(dto.getCarrier())
                .status(1)
                .shippedAt(LocalDateTime.now())
                .build();
        if (shipmentMapper.insert(shipment) == 0) {
            throw new RuntimeException("物流信息插入失败");
        }
        return ShipVO.builder()
                .shipmentId(shipment.getId())
                .trackingNo(shipment.getTrackingNo())
                .build();
    }

    /**
     * 校验当前用户是否有资格进行相关操作
     * @return
     */
    public Boolean haveMerchantOperation(Integer userId) {
        Merchant merchant = merchantMapper.selectMerchantByUserId(userId.longValue());
        if (merchant == null || merchant.getDeletedFlag() == 0) {
            return false;
        }
        return true;
    }
}
