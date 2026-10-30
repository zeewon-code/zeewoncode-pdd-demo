package com.zeewoncode.pdd_server.c.service.impl;


import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zeewoncode.context.BaseContext;
import com.zeewoncode.entity.*;
import com.zeewoncode.pdd_server.c.service.OrderService;
import com.zeewoncode.pdd_server.mapper.*;
import com.zeewoncode.dto.OrderCreateDTO;
import com.zeewoncode.dto.OrderItemDTO;
import com.zeewoncode.dto.OrderPreviewDTO;
import com.zeewoncode.result.PageResult;
import com.zeewoncode.utils.SnowflakeIdGenerator;
import com.zeewoncode.vo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
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
    @Autowired
    private PaymentMapper paymentMapper;
    @Autowired
    private ShipmentMapper shipmentMapper;

    //定义超时时长为 15 分钟
    private static final long PAY_TIMEOUT_MINUTES = 15;

    /**
     * 创建订单
     * @param req
     * @return
     */
    @Transactional
    @Override
    public OrderCreateVO createOrder(OrderCreateDTO req) {
        Integer userId = BaseContext.getCurrentId();
        // 1.预占库存
        int isLockStock = skuMapper.lockStock(req.getItems());
        int size = req.getItems().size();
        if (isLockStock != size) {
            throw new RuntimeException("库存不足");
        }
        // 判断下单的sku属于的spu是否存在
        for (OrderItemDTO item : req.getItems()) {
            Long skuId = item.getSkuId().longValue();
            Sku sku = skuMapper.selectSkuById(skuId);
            Spu spuById = spuMapper.getSpuById(sku.getSpuId().longValue());
            if (spuById.getDeletedFlag() == 1) {
                throw new RuntimeException("该商品已下架");
            }
        }
        // 2.插入订单表orders
        Order order = new Order();
        order.setOrderNo(String.valueOf(snowflakeIdGenerator.nextId()));
        order.setUserId(userId.longValue());
        // 填充merchantId
        Sku sku = skuMapper.selectSkuById(req.getItems().get(0).getSkuId().longValue());
        Spu spu = spuMapper.getSpuById(sku.getSpuId().longValue());
        order.setMerchantId(spu.getMerchantId());
        // 填充addressId
        order.setAddressId(req.getAddressId().longValue());
        // 填充receiverName, receiverPhone, receiverAddress
        UserAddress userAddress = addressesMapper.selectById(req.getAddressId().longValue());
        order.setReceiverName(userAddress.getReceiverName());
        order.setReceiverPhone(userAddress.getReceiverPhone());
        String address = userAddress.getProvince() + userAddress.getCity() + userAddress.getDistrict() + userAddress.getDetail();
        order.setReceiverAddress(address);
        Double totalAmount = 0.0;
        // 填充totalAmount
        for (OrderItemDTO item : req.getItems()) {
            Long skuId = item.getSkuId().longValue();
            Integer quantity = item.getQuantity();
            Sku skuDB = skuMapper.selectSkuById(skuId);
            Double price = skuDB.getPrice();
            totalAmount += (price * quantity);
        }
        order.setTotalAmount(new BigDecimal(totalAmount.toString()));
        // 填充discountAmount, payableAmount
        // 判断用户是否有使用优惠券的资格
        if (req.getUserCouponId() != null) {
            Long couponId = req.getUserCouponId().longValue();
            UserCoupon userCoupon = couponMapper.selectUserCouponById(couponId);
            Coupon coupon = couponMapper.selectById(userCoupon.getCouponId(), LocalDateTime.now());
            if (coupon.getType() == 1) {
                // 优惠券类型为满减券
                if (totalAmount >= coupon.getConditionAmount()) {
                    order.setDiscountAmount(new BigDecimal(coupon.getDiscountAmount().toString()));
                } else throw new RuntimeException("优惠券不满足使用条件");
            } else {
                // 优惠券为折扣券
                Double discountAmount = (1-coupon.getDiscountRate()) * totalAmount;
                order.setDiscountAmount(new BigDecimal(discountAmount.toString()));
            }
        } else {
            order.setDiscountAmount(BigDecimal.ZERO);
        }
        order.setPayableAmount((order.getTotalAmount().subtract(order.getDiscountAmount())));
        order.setPaymentId(null);
        order.setGrouponInstanceId(null);
        order.setStatus(0);
        order.setCancelType(null);
        order.setPaidAt(null);
        order.setCompletedAt(null);
        orderMapper.insert(order);
        if (order.getDiscountAmount() != null && order.getDiscountAmount().compareTo(BigDecimal.ZERO) > 0) {
            // 修改优惠券的状态
            UserCoupon userCouponDb = UserCoupon.builder()
                    .orderId(order.getId())
                    .status(1)
                    .usedAt(LocalDateTime.now())
                    .id(req.getUserCouponId().longValue())
                    .build();
            couponMapper.updateUserCoupon(userCouponDb);
        }

        // 3. 插入订单商品项表order_items
        List<OrderItem> orderItems = new ArrayList<OrderItem>();
        for (OrderItemDTO orderItem : req.getItems()) {
            OrderItem item = new OrderItem();
            Long orderId = order.getId();
            item.setOrderId(orderId);
            item.setSkuId(orderItem.getSkuId().longValue());
            Sku skuDB = skuMapper.selectSkuById(orderItem.getSkuId().longValue());
            Long spuId = skuDB.getSpuId().longValue();
            item.setSpuId(spuId);
            Spu spuById = spuMapper.getSpuById(spuId);
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
        for (OrderItemDTO orderItem : req.getItems()) {
            cartMapper.deleteCartItemsByUserIdAndSkuId(userId, orderItem.getSkuId().longValue());
        }
        // 5. 返回创建订单结果
        // 返回支付截止时间
        LocalDateTime expireDateTime = LocalDateTime.now().plusMinutes(PAY_TIMEOUT_MINUTES);
        OrderCreateVO result = OrderCreateVO.builder()
                .orderId(order.getId().intValue())
                .orderNo(order.getOrderNo())
                .payableAmount(order.getPayableAmount().doubleValue())
                .payExpireTime(expireDateTime)
                .build();
        return result;
    }

    /**
     * 订单结算预览
     * @param orderPreviewDTO
     * @return
     */
    @Override
    public OrderPreviewVO previewOrder(OrderPreviewDTO orderPreviewDTO) {
        // orderPreviewReq中包含 商品规格id和数量的数组，以及用户优惠券id（可能为null）
        // 1、计算订单总金额 totalAmount
        OrderPreviewVO orderPreviewVO = new OrderPreviewVO();
        Double totalAmount = 0.0;
        Double payableAmount = 0.0;
        for (OrderItemDTO item : orderPreviewDTO.getItems()) {
            Long skuId = item.getSkuId().longValue();
            Integer quantity = item.getQuantity();
            Sku sku = skuMapper.selectSkuById(skuId);
            totalAmount += (sku.getPrice() * quantity);
        }
        payableAmount = totalAmount;
        // 填充totalAmount字段
        orderPreviewVO.setTotalAmount(totalAmount);
        // 2.计算优惠总额 discountAmount
        if (orderPreviewDTO.getUserCouponId() != null) {
            // 判断用户的优惠券是否过期，如果过期则不能使用
            Long userCouponId = orderPreviewDTO.getUserCouponId().longValue();
            UserCoupon userCoupon = couponMapper.selectUserCouponById(userCouponId);
            if (userCoupon.getStatus() == 1) {
                throw new RuntimeException("优惠券已使用");
            } else if (userCoupon.getStatus() == 2) {
                throw new RuntimeException("优惠券已过期");
            }
            Coupon coupon = couponMapper.selectById(userCoupon.getCouponId(), LocalDateTime.now());
            if (coupon.getType() == 1) {
                // 优惠券类型为满减券
                Double conditionAmount = coupon.getConditionAmount();
                Double discountAmount = coupon.getDiscountAmount();
                if (totalAmount >= conditionAmount) {
                    payableAmount -= discountAmount;
                    // 填充discountAmount字段
                    orderPreviewVO.setDiscountAmount(discountAmount);
                }
            } else {
                // 优惠券为折扣券
                Double discountRate = coupon.getDiscountRate();
                Double discountAmount = (1-discountRate) * totalAmount;
                payableAmount = totalAmount - discountAmount;
                // 填充discountAmount字段
                orderPreviewVO.setDiscountAmount(discountAmount);
            }
        }
        // 3. 设置实付金额 payableAmount
        orderPreviewVO.setPayableAmount(payableAmount);
        return orderPreviewVO;
    }

    /**
     * 取消待付款订单
     * @param id
     */
    @Override
    @Transactional
    public void cancelOrderPendingPayMent(Integer id) {
        Long orderId = Long.valueOf(id);
        Long userId = BaseContext.getCurrentId().longValue();
        Order order = orderMapper.selectOrderById(orderId);
        if (order == null) {
            throw new RuntimeException("该订单不存在");
        }
        // 1.校验订单状态status，是否=0待付款
        if (order.getStatus() != 0) {
            throw new RuntimeException("该订单不是待付款，订单不能取消");
        }
        // 2.更新订单状态status=4，cancel_type=1
        Order orderDb = Order.builder().id(orderId).cancelType(1).status(4).build();
        orderMapper.update(orderDb);
        // 3.释放预占库存
        List<OrderItem> orderItems = orderMapper.selectItemsByOrderId(orderId);
        for (OrderItem orderItem : orderItems) {
            skuMapper.releaseLockStock(orderItem.getSkuId(), orderItem.getQuantity());
        }
        // 4.退还优惠券(将优惠券状态改为未使用，status=0, used_at=null, order_id = null)
        // 判断是否使用了优惠券
        if (order.getDiscountAmount() != null && order.getDiscountAmount().compareTo(BigDecimal.ZERO) > 0) {
            UserCoupon userCoupon = UserCoupon.builder().userId(userId).orderId(orderId).status(0).usedAt(null).build();
            couponMapper.updateUserCouponStatusAndUsedAt(userCoupon);
            UserCoupon userCoupon1 = couponMapper.selectUserCouponByUserIdAndOrderId(userId, orderId);
            userCoupon1.setOrderId(null);
            couponMapper.updateUserCouponSetOrderIdToNull(userCoupon1);
        }
        // 5.删掉order_item表的数据
        orderMapper.deleteByOrderId(orderId);
    }

    /**
     * 获取订单支付倒计时
     * @param id
     * @return
     */
    @Override
    public CountdownVO getCountdown(Integer id) {
        if (id == null) {
            throw new RuntimeException("订单id不能为空");
        }
        Order order = orderMapper.selectOrderById(id.longValue());
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }
        Integer status = order.getStatus();
        if (status != 0) {
            // status != 0, 截止日期返回null
            CountdownVO result = CountdownVO.builder().status(status).payExpireTime(null).build();
            return result;
        }
        LocalDateTime createdAt = order.getCreatedAt();
        LocalDateTime payExpireTime = createdAt.plusMinutes(PAY_TIMEOUT_MINUTES);
        CountdownVO result = CountdownVO.builder().status(status).payExpireTime(payExpireTime).build();
        return result;
    }

    /**
     * 订单支付
     * @param id
     * @return
     */
    @Override
    public PayVO orderPay(Long id) {
        // 1.检查待付款时间是否超时
        // 超时
        Long userId = BaseContext.getCurrentId().longValue();
        Order order = orderMapper.selectOrderById(id);
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }
        if (LocalDateTime.now().isAfter(order.getCreatedAt().plusMinutes(PAY_TIMEOUT_MINUTES))) {
            Order build = Order.builder().id(id).status(5).build();
            orderMapper.update(build);
            if (order.getDiscountAmount() != null && order.getDiscountAmount().compareTo(BigDecimal.ZERO) > 0) {
                UserCoupon userCoupon = UserCoupon.builder()
                        .userId(BaseContext.getCurrentId().longValue())
                        .orderId(id)
                        .status(0)
                        .usedAt(null)
                        .build();
                couponMapper.updateUserCouponStatusAndUsedAt(userCoupon);
                UserCoupon userCoupon1 = couponMapper.selectUserCouponByUserIdAndOrderId(BaseContext.getCurrentId().longValue(), id);
                userCoupon1.setOrderId(null);
                couponMapper.updateUserCouponSetOrderIdToNull(userCoupon1);
            }
            throw new RuntimeException("订单支付超时");
        }
        // 2.不超时
        // 3.往payment表插入数据，支付记录
        BigDecimal payableAmount = order.getPayableAmount();
        Payment payment = Payment.builder()
                .paymentNo("P" + String.valueOf(snowflakeIdGenerator.nextId()))
                .orderId(id)
                .userId(BaseContext.getCurrentId().longValue())
                .amount(payableAmount)
                .channel("MOCK模拟")
                .status(1)
                .paidAt(LocalDateTime.now())
                .createdAt(LocalDateTime.now())
                .build();
        paymentMapper.insert(payment);
        // 4.订单状态修改为1 （待发货）
        Order build = Order.builder().id(id).status(1).userId(userId).build();
        orderMapper.update(build);
        // 5.扣减库存跟释放预占库存
        List<OrderItem> orderItems = orderMapper.selectItemsByOrderId(id);
        for (OrderItem orderItem : orderItems) {
            skuMapper.reduceStockAndLockStock(orderItem.getSkuId(), orderItem.getQuantity());
        }
        PayVO payVO = PayVO.builder().paymentId(payment.getId())
                .paymentNo(payment.getPaymentNo())
                .paidAmount(payment.getAmount())
                .build();
        return payVO;
    }

    /**
     * 获取订单列表
     * @param status
     * @param page
     * @param size
     * @return
     */
    @Override
    public PageResult<OrderCard> getOrderList(Integer status, Integer page, Integer size) {
        Long userId = BaseContext.getCurrentId().longValue();
        // 1.启动分页
        PageHelper.startPage(page, size, true);
        // 2.查询订单列表
        List<OrderCard> orderList = orderMapper.selectOrderList(userId, status, null);
        // 3.包装分页结果
        PageInfo<OrderCard> pageInfo = new PageInfo<>(orderList);
        PageResult<OrderCard> result = new PageResult<>();
        result.setTotal((int) pageInfo.getTotal());
        result.setSize(pageInfo.getSize());
        result.setPage(pageInfo.getPageNum());
        result.setRecords(pageInfo.getList());
        return result;
    }

    /**
     * 获取订单详情
     * @param id
     * @return
     */
    @Override
    public OrderDetail getOrderDetail(Integer id) {
        Long userId = BaseContext.getCurrentId().longValue();
        OrderDetail orderDetail = orderMapper.selectOrderDetail(userId, id);
        // 1.补充receiver类字段
        Receiver receiver = orderMapper.selectReceiverByOrderId(id);
        // 2.补充ShipVO类字段
        Shipment shipment = shipmentMapper.selectShipmentByOrderId(id);
        ShipVO shipVO = ShipVO.builder().shipmentId(shipment.getId()).trackingNo(shipment.getTrackingNo()).build();
        orderDetail.setShipVO(shipVO);
        orderDetail.setReceiver(receiver);
        return orderDetail;
    }
}
