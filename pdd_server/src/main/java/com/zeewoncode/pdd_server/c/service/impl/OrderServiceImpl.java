package com.zeewoncode.pdd_server.c.service.impl;

import com.fasterxml.jackson.databind.ser.Serializers;
import com.zeewoncode.context.BaseContext;
import com.zeewoncode.entity.*;
import com.zeewoncode.pdd_server.c.service.OrderService;
import com.zeewoncode.pdd_server.mapper.*;
import com.zeewoncode.req.OrderCreateReq;
import com.zeewoncode.req.OrderItemReq;
import com.zeewoncode.utils.SnowflakeIdGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private SkuMapper skuMapper;
    @Autowired
    private SnowflakeIdGenerator snowflakeIdGenerator;
    @Autowired
    private SpuMapper spuMapper;
    @Autowired
    private AddressesMapper addressesMapper;
    @Autowired
    private CouponMapper couponMapper;
    @Autowired
    private CartMapper cartMapper;

    /**
     * 创建订单
     * @param req
     * @return
     */
//    @Transactional
    @Override
    public OrderCreateResult createOrder(OrderCreateReq req) {
        Integer userId = BaseContext.getCurrentId();
        // 1.预占库存
        boolean isLockStock = skuMapper.lockStock(req.getItems());
        if (!isLockStock) {
            throw new RuntimeException("该商品库存不足");
        }
        // 2.插入订单表orders
        Order order = new Order();
        order.setOrderNo(String.valueOf(snowflakeIdGenerator.nextId()));
        order.setUserId(userId.longValue());
        // 填充merchantId
        Sku sku = skuMapper.selectSkuById(req.getItems().get(0).getSkuId().longValue());
        Spu spu = spuMapper.getSpuById(sku.getSpuId());
        order.setMerchantId(spu.getMerchantId());
        // 填充addressId
        order.setAddressId(req.getAddressId().longValue());
        // 填充receiverName, receiverPhone, receiverAddress
        UserAddress userAddress = addressesMapper.selectById(req.getAddressId());
        order.setReceiverName(userAddress.getReceiverName());
        order.setReceiverPhone(userAddress.getReceiverPhone());
        String address = userAddress.getProvince() + userAddress.getCity() + userAddress.getDistrict() + userAddress.getDetail();
        order.setReceiverAddress(address);
        Double totalAmount = 0.0;
        // 填充totalAmount
        for (OrderItemReq item : req.getItems()) {
            Long skuId = item.getSkuId().longValue();
            Integer quantity = item.getQuantity();
            Sku skuDB = skuMapper.selectSkuById(skuId);
            Double price = skuDB.getPrice();
            totalAmount += (price * quantity);
        }
        order.setTotalAmount(new BigDecimal(totalAmount.toString()));
        // 填充discountAmount, payableAmount
        // 判断是否有使用优惠券
        if (req.getUserCouponId() != null) {
            Integer couponId = req.getUserCouponId();
            Coupon coupon = couponMapper.selectById(couponId);
            if (coupon.getType() == 1) {
                // 优惠券类型为满减券
                if (totalAmount >= coupon.getConditionAmount()) {
                    order.setDiscountAmount(new BigDecimal(coupon.getDiscountAmount().toString()));
                } else throw new RuntimeException("优惠券不满足使用条件");
            } else {
                // 优惠券为折扣券
                Double discountAmount = coupon.getDiscountRate() * totalAmount;
                order.setDiscountAmount(new BigDecimal(discountAmount.toString()));
            }
        } else {
            order.setDiscountAmount(BigDecimal.ZERO);
        }
        order.setPayableAmount((order.getTotalAmount().subtract(order.getDiscountAmount())));
        order.setPaymentId(null);
        order.setGrouponInstanceId(null);
        order.setStatus(1);
        order.setCancelType(null);
        order.setPaidAt(null);
        order.setCompletedAt(null);
        orderMapper.insert(order);
        // 3. 插入订单商品项表order_items
        List<OrderItem> orderItems = new ArrayList<OrderItem>();
        for (OrderItemReq orderItem : req.getItems()) {
            OrderItem item = new OrderItem();
            Long orderId = order.getId();
            item.setOrderId(orderId);
            item.setSkuId(orderItem.getSkuId().longValue());
            Sku skuDB = skuMapper.selectSkuById(orderItem.getSkuId().longValue());
            Long spuId = skuDB.getSpuId().longValue();
            item.setSpuId(spuId);
            Spu spuById = spuMapper.getSpuById(spuId.intValue());
            item.setTitle(spuById.getTitle());
            item.setSpecs(skuDB.getSpecs());
            item.setImage(skuDB.getImage());
            item.setPrice(new BigDecimal(skuDB.getPrice().toString()));
            item.setQuantity(orderItem.getQuantity());
            item.setAmount(item.getPrice().multiply(new BigDecimal(item.getQuantity())));
            orderItems.add(item);
        }
        orderMapper.insertOrderItems(orderItems);
        // 4. 删除cart_items表购物车数据
        for (OrderItemReq orderItem : req.getItems()) {
            cartMapper.deleteCartItemsByUserIdAndSkuId(userId, orderItem.getSkuId().longValue());
        }
        // 5. 返回创建订单结果
        OrderCreateResult result = OrderCreateResult.builder()
                .orderId(order.getId().intValue())
                .orderNo(order.getOrderNo())
                .payableAmount(order.getPayableAmount().doubleValue())
                .build();
        return result;
    }
}
