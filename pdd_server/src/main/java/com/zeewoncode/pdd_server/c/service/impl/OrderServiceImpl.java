package com.zeewoncode.pdd_server.c.service.impl;


import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zeewoncode.constant.ErrorCode;
import com.zeewoncode.exception.BaseException;
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
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

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
    public OrderCreateVO createOrder(OrderCreateDTO req, Long userId) {
        // 1.预占库存
        int isLockStock = skuMapper.lockStock(req.getItems());
        int size = req.getItems().size();
        if (isLockStock != size) {
            throw new BaseException(ErrorCode.SKU_NOT_ENOUGH);
        }
        // 判断下单的sku属于的spu是否存在
        for (OrderItemDTO item : req.getItems()) {
            Long skuId = item.getSkuId();
            Sku sku = skuMapper.selectSkuById(skuId);
            Spu spuById = spuMapper.getSpuById(sku.getSpuId());
            if (spuById.getDeletedFlag() == 1) {
                throw new BaseException(ErrorCode.SPU_NOT_SALE);
            }
        }
        // 2.插入订单表orders
        Order order = new Order();
        order.setOrderNo(String.valueOf(snowflakeIdGenerator.nextId()));
        order.setUserId(userId);
        // 填充merchantId
        Sku sku = skuMapper.selectSkuById(req.getItems().get(0).getSkuId());
        Spu spu = spuMapper.getSpuById(sku.getSpuId());
        order.setMerchantId(spu.getMerchantId());
        // 填充addressId
        order.setAddressId(req.getAddressId());
        // 填充receiverName, receiverPhone, receiverAddress
        UserAddress userAddress = addressesMapper.selectById(req.getAddressId());
        order.setReceiverName(userAddress.getReceiverName());
        order.setReceiverPhone(userAddress.getReceiverPhone());
        String address = userAddress.getProvince() + userAddress.getCity() + userAddress.getDistrict() + userAddress.getDetail();
        order.setReceiverAddress(address);
        BigDecimal totalAmount = BigDecimal.ONE;
        // 填充totalAmount
        for (OrderItemDTO item : req.getItems()) {
            Long skuId = item.getSkuId();
            Integer quantity = item.getQuantity();
            Sku skuDB = skuMapper.selectSkuById(skuId);
            BigDecimal price = skuDB.getPrice();
            totalAmount = totalAmount.add(price.multiply(BigDecimal.valueOf(quantity)))
                    .setScale(2, RoundingMode.HALF_UP);
        }
        order.setTotalAmount(totalAmount);
        // 填充discountAmount, payableAmount
        // 判断用户是否有使用优惠券的资格
        if (req.getUserCouponId() != null) {
            Long couponId = req.getUserCouponId();
            UserCoupon userCoupon = couponMapper.selectUserCouponById(couponId);
            Coupon coupon = couponMapper.selectById(userCoupon.getCouponId(), LocalDateTime.now());
            if (coupon.getType() == 1) {
                // 优惠券类型为满减券
                if (totalAmount.compareTo(coupon.getConditionAmount()) >= 0) {
                    order.setDiscountAmount(coupon.getDiscountAmount().setScale(2, RoundingMode.HALF_UP));
                } else throw new BaseException(ErrorCode.COUPON_CONDITION_NOT_MEET);
            } else {
                // 优惠券为折扣券
                BigDecimal discountAmount = BigDecimal.ONE.subtract(coupon.getDiscountRate())
                        .multiply(totalAmount).setScale(2, RoundingMode.HALF_UP);
                order.setDiscountAmount(discountAmount);
            }
        } else {
            order.setDiscountAmount(BigDecimal.ZERO);
        }
        order.setPayableAmount((order.getTotalAmount().subtract(order.getDiscountAmount()))
                .setScale(2, RoundingMode.HALF_UP));
        order.setPaymentId(null);
        order.setGrouponInstanceId(null);
        order.setStatus(0);
        order.setCancelType(null);
        order.setPaidAt(null);
        order.setCompletedAt(null);
        if (orderMapper.insert(order) != 1) {
            throw new BaseException(ErrorCode.ORDER_CREATE_FAILED);
        }
        if (order.getDiscountAmount() != null && order.getDiscountAmount().compareTo(BigDecimal.ZERO) > 0) {
            // 修改优惠券的状态
            UserCoupon userCouponDb = UserCoupon.builder()
                    .orderId(order.getId())
                    .status(1)
                    .usedAt(LocalDateTime.now())
                    .id(req.getUserCouponId())
                    .build();
            if (couponMapper.updateUserCoupon(userCouponDb) != 1) {
                throw new BaseException(ErrorCode.COUPON_USE_FAILED);
            }
        }

        // 3. 插入订单商品项表order_items
        List<OrderItem> orderItems = new ArrayList<OrderItem>();
        for (OrderItemDTO orderItem : req.getItems()) {
            OrderItem item = new OrderItem();
            Long orderId = order.getId();
            item.setOrderId(orderId);
            item.setSkuId(orderItem.getSkuId());
            Sku skuDB = skuMapper.selectSkuById(orderItem.getSkuId());
            Long spuId = skuDB.getSpuId();
            item.setSpuId(spuId);
            Spu spuById = spuMapper.getSpuById(spuId);
            item.setTitle(spuById.getTitle());
            item.setSpecs(skuDB.getSpecs());
            item.setImage(skuDB.getImage());
            item.setPrice(skuDB.getPrice());
            item.setQuantity(orderItem.getQuantity());
            item.setAmount(item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity()))
                    .setScale(2, RoundingMode.HALF_UP));
            orderItems.add(item);
        }
        if (orderMapper.insertOrderItems(orderItems) != size) {
            throw new BaseException(ErrorCode.ORDER_ITEMS_CREATE_FAILED);
        }
        List<Long> skuIds = req.getItems().stream().map(OrderItemDTO::getSkuId).toList();
        if (cartMapper.deleteCartItemsByUserIdAndSkuIds(userId, skuIds) != skuIds.size()) {
            throw new BaseException(ErrorCode.CART_SKU_DELETE_FAILED);
        }
        // 5. 返回创建订单结果
        // 返回支付截止时间
        LocalDateTime expireDateTime = LocalDateTime.now().plusMinutes(PAY_TIMEOUT_MINUTES);
        OrderCreateVO result = OrderCreateVO.builder()
                .orderId(order.getId().intValue())
                .orderNo(order.getOrderNo())
                .payableAmount(order.getPayableAmount().setScale(2, RoundingMode.HALF_UP))
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
        BigDecimal totalAmount = BigDecimal.ZERO;
        for (OrderItemDTO item : orderPreviewDTO.getItems()) {
            Long skuId = item.getSkuId();
            Integer quantity = item.getQuantity();
            Sku sku = skuMapper.selectSkuById(skuId);
            totalAmount = totalAmount.add(sku.getPrice().setScale(2, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(quantity)));
        }
        BigDecimal payableAmount = totalAmount;
        // 填充totalAmount字段
        orderPreviewVO.setTotalAmount(totalAmount);
        // 2.计算优惠总额 discountAmount
        if (orderPreviewDTO.getUserCouponId() != null) {
            // 判断用户的优惠券是否过期，如果过期则不能使用
            Long userCouponId = orderPreviewDTO.getUserCouponId();
            UserCoupon userCoupon = couponMapper.selectUserCouponById(userCouponId);
            if (userCoupon.getStatus() == 1) {
                throw new BaseException(ErrorCode.COUPON_USED);
            } else if (userCoupon.getStatus() == 2) {
                throw new BaseException(ErrorCode.COUPON_EXPIRED);
            }
            Coupon coupon = couponMapper.selectById(userCoupon.getCouponId(), LocalDateTime.now());
            if (coupon.getType() == 1) {
                // 优惠券类型为满减券
                BigDecimal conditionAmount = coupon.getConditionAmount().setScale(2, RoundingMode.HALF_UP);
                BigDecimal discountAmount = coupon.getDiscountAmount().setScale(2, RoundingMode.HALF_UP);
                if (totalAmount.compareTo(conditionAmount) >= 0) {
                    payableAmount = payableAmount.subtract(discountAmount).setScale(2, RoundingMode.HALF_UP);
                    // 填充discountAmount字段
                    orderPreviewVO.setDiscountAmount(discountAmount);
                }
            } else {
                // 优惠券为折扣券
                BigDecimal discountRate = coupon.getDiscountRate();
                BigDecimal discountAmount = (BigDecimal.ONE.subtract(discountRate)).multiply(totalAmount)
                        .setScale(2, RoundingMode.HALF_UP);
                payableAmount = totalAmount.subtract(discountAmount)
                        .setScale(2, RoundingMode.HALF_UP);
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
     * @param orderId
     */
    @Override
    @Transactional
    public void cancelOrderPendingPayMent(Long orderId, Long userId) {
        Order order = orderMapper.selectOrderById(orderId);
        if (order == null) {
            throw new BaseException(ErrorCode.ORDER_NOT_EXIST);
        }
        // 1.校验订单状态status，是否=0待付款
        if (order.getStatus() != 0) {
            throw new BaseException(ErrorCode.ORDER_NOT_CANCEL);
        }
        // 2.更新订单状态status=4，cancel_type=1
        Order orderDb = Order.builder().id(orderId).cancelType(1).status(4).build();
        if (orderMapper.update(orderDb) != 1) {
            throw new BaseException(ErrorCode.ORDER_CANCEL_FAILED);
        }
        // 3.释放预占库存
        List<OrderItem> orderItems = orderMapper.selectItemsByOrderId(orderId);
        for (OrderItem orderItem : orderItems) {
            skuMapper.releaseLockStock(orderItem.getSkuId(), orderItem.getQuantity());
        }
        // 4.退还优惠券(将优惠券状态改为未使用，status=0, used_at=null, order_id = null)
        // 判断是否使用了优惠券
        if (order.getDiscountAmount() != null && order.getDiscountAmount().compareTo(BigDecimal.ZERO) > 0) {
            UserCoupon userCoupon = UserCoupon.builder().userId(userId).orderId(orderId).status(0).usedAt(null).build();
            if (couponMapper.updateUserCouponStatusAndUsedAt(userCoupon) != 1) {
                throw new BaseException(ErrorCode.COUPON_RETURN_FAILED);
            }
        }
        // 5.删掉order_item表的数据
        if (orderMapper.deleteByOrderId(orderId) != orderItems.size()) {
            throw new BaseException(ErrorCode.ORDER_ITEMS_DELETE_FAILED);
        }
    }

    /**
     * 获取订单支付倒计时
     * @param id
     * @return
     */
    @Override
    public CountdownVO getCountdown(Long id) {
        Order order = orderMapper.selectOrderById(id);
        if (order == null) {
            throw new BaseException(ErrorCode.ORDER_NOT_EXIST);
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
    @Transactional
    public PayVO orderPay(Long id, Long userId) {
        // 1.检查待付款时间是否超时
        // 超时
        Order order = orderMapper.selectOrderById(id);
        if (order == null) {
            throw new BaseException(ErrorCode.ORDER_NOT_EXIST);
        }
        if (LocalDateTime.now().isAfter(order.getCreatedAt().plusMinutes(PAY_TIMEOUT_MINUTES))) {
            Order build = Order.builder().id(id).status(5).build();
            orderMapper.update(build);
            if (order.getDiscountAmount() != null
                    && order.getDiscountAmount().compareTo(BigDecimal.ZERO) > 0) {
                UserCoupon userCoupon = UserCoupon.builder()
                        .userId(userId)
                        .orderId(id)
                        .status(0)
                        .usedAt(null)
                        .build();
                couponMapper.updateUserCouponStatusAndUsedAt(userCoupon);
            }
            throw new BaseException(ErrorCode.ORDER_PAY_TIME_OUT);
        }
        // 2.不超时
        // 3.往payment表插入数据，支付记录
        BigDecimal payableAmount = order.getPayableAmount().setScale(2, RoundingMode.HALF_UP);
        Payment payment = Payment.builder()
                .paymentNo("P" + (snowflakeIdGenerator.nextId()))
                .orderId(id)
                .userId(userId)
                .amount(payableAmount)
                .channel("MOCK模拟")
                .status(1)
                .paidAt(LocalDateTime.now())
                .createdAt(LocalDateTime.now())
                .build();
        if (paymentMapper.insert(payment) != 1) {
            throw new BaseException(ErrorCode.PAYMENT_CREATE_FAILED);
        }
        // 4.订单状态修改为1 （待发货）
        Order build = Order.builder().id(id).status(1).userId(userId).build();
        if (orderMapper.update(build) != 1) {
            throw new BaseException(ErrorCode.ORDER_STATUS_UPDATE_FAILED);
        }
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
    public PageResult<OrderCard> getOrderList(Integer status, Integer page, Integer size, Long userId) {
        // 1.启动分页
        PageHelper.startPage(page, size, true);
        // 2.查询订单列表
        List<OrderCard> orderList = orderMapper.selectOrderList(userId, status, null, null, null);
        // 3.包装分页结果
        PageInfo<OrderCard> pageInfo = new PageInfo<>(orderList);
        if (orderList.isEmpty()) {
            PageResult.empty(pageInfo);
        }
        return PageResult.of(pageInfo, orderList);
    }

    /**
     * 获取订单详情
     * @param id
     * @return
     */
    @Override
    public OrderDetail getOrderDetail(Long id, Long userId) {
        OrderDetail orderDetail = orderMapper.selectOrderDetail(userId, id);
        // 1.补充receiver类字段
        Receiver receiver = orderMapper.selectReceiverByOrderId(id);
        // 2.补充ShipVO类字段
        Shipment shipment = shipmentMapper.selectShipmentByOrderId(id);
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
     * 查询订单物流与轨迹
     * @param orderId
     * @param userId
     * @return
     */
    @Override
    @Transactional
    public ShipmentVO getShipment(Long orderId, Long userId) {
        // 查询订单是否存在
        Order order = orderMapper.selectOrderById(orderId);
        if (order == null || !order.getUserId().equals(userId)) {
            throw new BaseException(ErrorCode.ORDER_NOT_EXIST);
        }
        ShipmentVO vo = new ShipmentVO();
        vo.setOrderId(orderId);
        vo.setOrderNo(order.getOrderNo());
        // 查物流单
        Shipment shipment = shipmentMapper.selectShipmentByOrderId(orderId);
        if (shipment == null) {
            // 物流不存在，尚未发货：返回空结构，前端不需要处理 null
            vo.setStatus(0);
            vo.setStatusText(orderStatusText(order.getStatus()));
            vo.setCanReceive(false);
            vo.setSteps(Collections.emptyList());
            return vo;
        }
        // 3. 基础字段
        vo.setShipmentId(shipment.getId());
        vo.setTrackingNo(shipment.getTrackingNo());
        vo.setCarrier(shipment.getCarrier());
        vo.setStatus(shipment.getStatus());
        vo.setShippedAt(shipment.getShippedAt());
        vo.setReceivedAt(shipment.getReceivedAt());
        vo.setCreatedAt(shipment.getCreatedAt());
        vo.setCanReceive(isReceivable(shipment.getStatus(), order.getStatus()));

        // 4. 轨迹
        List<ShipmentTrack> tracks = shipmentMapper.selectTracksByShipmentId(shipment.getId());
        List<ShipmentStep> steps = tracks.stream()
                .map(t -> ShipmentStep.builder()
                        .time(t.getHappenedAt())
                        .desc(t.getDescription())
                        .status(t.getStatus())
                        .build())
                .collect(Collectors.toList());
        // 5. 没有轨迹记录时，用物流单的时间戳补两条
        if (steps.isEmpty()) {
            steps = buildFallbackSteps(shipment);
        }
        vo.setSteps(steps);
        // 6. 设置状态文案
        vo.setStatusText(resolveStatusText(shipment, tracks));
        return vo;
    }

    /**
     * 确认收货
     * @param orderId
     * @param userId
     */
    @Override
    @Transactional
    public void receiveOrder(Long orderId, Long userId) {
        // 校验订单属于当前用户且 status=2待收货
        Order order = orderMapper.selectOrderById(orderId);
        if (order == null || !order.getUserId().equals(userId)) {
            throw new BaseException(ErrorCode.ORDER_NOT_EXIST);
        }
        if (order.getStatus() != 2) {
            throw new BaseException(ErrorCode.ORDER_STATUS_ERROR);
        }
        Shipment shipment = shipmentMapper.selectShipmentByOrderId(orderId);
        // 条件更新物流单 status IN (1,2) -> 3已签收
        if (shipmentMapper.updateShipmentStatusByOrderId(shipment.getId(), 3, LocalDateTime.now()) != 1) {
            throw new BaseException(ErrorCode.SHIPMENT_STATUS_ERROR);
        }
        ShipmentTrack track = ShipmentTrack.builder()
                .shipmentId(shipment.getId())
                .status(4)
                .description("快件已签收，感谢使用")
                .happenedAt(LocalDateTime.now())
                .build();
        // 写入物流轨迹（已签收）
        if (shipmentMapper.insertTrack(track) != 1) {
            throw new BaseException(ErrorCode.SHIPMENT_ADD_FAILED);
        }
        // 条件更新订单 status=2 -> 3已完成
        Order orderdb = Order.builder().id(orderId).status(3).build();
        if (orderMapper.update(orderdb) != 1) {
            throw new BaseException(ErrorCode.ORDER_STATUS_ERROR);
        }
        // 按 SPU 分组累加 order_item.quantity 到 spu.sales
        //重复调用幂等：已签收时直接返回成功，不重复累加销量。
        List<OrderItem> orderItems = orderMapper.selectItemsByOrderId(orderId);
        for (OrderItem orderItem : orderItems) {
            Spu spu = spuMapper.getSpuById(orderItem.getSpuId());
            if (spu != null) {
                spu.setSales(spu.getSales() + orderItem.getQuantity());
                spuMapper.update(spu);
            }
        }
    }

    // 根据物流单和轨迹，判断物流状态文案
    private String resolveStatusText(Shipment shipment, List<ShipmentTrack> tracks) {
        if (!Integer.valueOf(2).equals(shipment.getStatus())) {
            return shipmentStatusText(shipment.getStatus());
        }
        Integer maxStage = tracks.stream()
                .map(ShipmentTrack::getStatus)
                .max(Integer::compareTo)
                .orElse(null);
        return maxStage != null && maxStage >= 3 ? "派送中" : "运输中";
    }

    // 判断是否可收货
    private boolean isReceivable(Integer shipmentStatus, Integer orderStatus) {
        return orderStatus != null && orderStatus == 2
                && shipmentStatus != null
                && (shipmentStatus == 1 || shipmentStatus == 2);
    }

    // 物流状态文案
    private String shipmentStatusText(Integer status) {
        if (status == null) {
            return "待发货";
        }
        return switch (status) {
            case 1 -> "已发货";
            case 2 -> "配送中";
            case 3 -> "已签收";
            case 4 -> "已取消";
            default -> "待发货";
        };
    }

    // 订单状态文案
    private String orderStatusText(Integer status) {
        if (status == null) {
            return "待发货";
        }
        return switch (status) {
            case 0 -> "待付款";
            case 1 -> "待发货";
            case 4 -> "已取消";
            default -> "待发货";
        };
    }

    private List<ShipmentStep> buildFallbackSteps(Shipment shipment) {
        List<ShipmentStep> steps = new ArrayList<>();
        if (shipment.getReceivedAt() != null) {
            steps.add(ShipmentStep.builder()
                    .time(shipment.getReceivedAt())
                    .status(3)
                    .desc("快件已签收")
                    .build());
        }
        if (shipment.getShippedAt() != null) {
            steps.add(ShipmentStep.builder()
                    .time(shipment.getShippedAt())
                    .status(1)
                    .desc("商家已发货")
                    .build());
        }
        return steps;
    }
}
