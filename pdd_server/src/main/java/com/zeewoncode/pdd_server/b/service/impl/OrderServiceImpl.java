package com.zeewoncode.pdd_server.b.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zeewoncode.context.BaseContext;
import com.zeewoncode.entity.*;
import com.zeewoncode.pdd_server.b.service.OrderService;
import com.zeewoncode.pdd_server.mapper.MerchantMapper;
import com.zeewoncode.pdd_server.mapper.OrderMapper;
import com.zeewoncode.pdd_server.mapper.ShipmentMapper;
import com.zeewoncode.pdd_server.mapper.UserMapper;
import com.zeewoncode.result.PageResult;
import com.zeewoncode.vo.ShipVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private MerchantMapper merchantMapper;
    @Autowired
    private UserMapper userMapper;
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
    public PageResult<OrderCard> getOrderList(Integer status, Integer page, Integer size) {
        Integer userId = BaseContext.getCurrentId();
        User user = userMapper.selectById(userId);
        if (user == null || user.getUserType() != 2) {
            throw new RuntimeException("当前用户不存在或者当前用户不是店铺的管理员，没有该权限进行相关操作");
        }
        Merchant merchant = merchantMapper.selectMerchantByUserId(userId.longValue());
        if (merchant == null) {
            throw new RuntimeException("当前用户没有绑定店铺，没有该权限进行相关操作");
        }
        // 1.启动分页
        PageHelper.startPage(page, size, true);
        // 2.查询订单列表
        List<OrderCard> orderList = orderMapper.selectOrderList(null, status, merchant.getId());
        // 3.包装分页结果
        PageInfo<OrderCard> pageInfo = new PageInfo<>(orderList);
        if (pageInfo == null) {
            return PageResult.empty(pageInfo);
        }
        return PageResult.of(pageInfo, pageInfo.getList());
    }

    /**
     * 获取本店订单详情
     * @param orderId
     * @return
     */
    @Override
    public OrderDetail getOrderDetail(Long orderId) {
        Integer userId = BaseContext.getCurrentId();
        User user = userMapper.selectById(userId);
        if (user == null || user.getUserType() != 2) {
            throw new RuntimeException("当前用户不存在或者当前用户不是店铺的管理员，没有该权限进行相关操作");
        }
        Merchant merchant = merchantMapper.selectMerchantByUserId(userId.longValue());
        if (merchant == null) {
            throw new RuntimeException("当前用户没有绑定店铺，没有该权限进行相关操作");
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

//    /**
//     * 判断当前用户是否是商店的管理员
//     * @param userId
//     * @return
//     */
//    public Merchant isMerchantPermisson(Long userId) {
//        Merchant merchant = merchantMapper.selectMerchantByUserId(userId);
//        if (merchant == null) {
//            return null;
//        }
//        return merchant;
//    }
}
