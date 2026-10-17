package com.zeewoncode.pdd_server.c.service.impl;

import com.zeewoncode.context.BaseContext;
import com.zeewoncode.dto.GrouponInitiateDTO;
import com.zeewoncode.dto.OrderItemDTO;
import com.zeewoncode.entity.*;
import com.zeewoncode.pdd_server.c.service.GrouponsService;
import com.zeewoncode.pdd_server.mapper.*;
import com.zeewoncode.utils.SnowflakeIdGenerator;
import com.zeewoncode.vo.GrouponInitiateVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Service
public class GrouponsServiceImpl implements GrouponsService {

    @Autowired
    private GrouponMapper grouponMapper;
    @Autowired
    private SnowflakeIdGenerator snowflakeIdGenerator;
    @Autowired
    private SpuMapper spuMapper;
    @Autowired
    private AddressesMapper addressesMapper;
    @Autowired
    private SkuMapper skuMapper;
    @Autowired
    private OrderMapper orderMapper;

    /**
     * 拼团发起（团长下单）
     * @param grouponInitiateDTO
     * @return
     */
    @Override
    @Transactional
    public GrouponInitiateVO initiateGroupon(GrouponInitiateDTO grouponInitiateDTO) {
        Integer defineId = grouponInitiateDTO.getDefineId();
        Long userId = BaseContext.getCurrentId().longValue();
        LocalDateTime now = LocalDateTime.now();
        // 校验该拼团活动是否存在
        GrouponDefine define = grouponMapper.selectGrouponDefineById(defineId);
        if (define == null) {
            throw new RuntimeException("拼团活动不存在");
        }
        if (define.getStartTime().isAfter(now)) {
            throw new RuntimeException("拼团活动尚未开始");
        }
        if (define.getEndTime().isBefore(now)) {
            throw new RuntimeException("拼团活动已结束");
        }
        if (define.getStatus() != 1) {
            throw new RuntimeException("拼团活动已下架");
        }
        List<OrderItemDTO> list = new ArrayList<>();
        list.add(OrderItemDTO.builder()
                .skuId(define.getSkuId())
                .quantity(grouponInitiateDTO.getQuantity())
                .build());
        boolean lock = skuMapper.lockStock(list);
        if (!lock) {
            throw new RuntimeException("库存不足");
        }
        // 创建拼团实例
        // targetCount拼团人数
        Integer targetCount = define.getTargetCount();
        GrouponInstance grouponInstance = GrouponInstance.builder()
                .defineId(defineId.longValue())
                .leaderUserId(userId)
                .currentCount(1)
                .targetCount(targetCount)
                .status(0)
                .expireAt(now.plusHours(define.getExpireHours()).truncatedTo(ChronoUnit.SECONDS))
                .build();
        grouponMapper.insertGrouponInstance(grouponInstance);
        // 按拼团价下单并关联GrouponInstanceId
        // 插入订单表orders和order_item表
        // 插入orders表
        Order order = new Order();
        order.setUserId(userId);
        order.setOrderNo(String.valueOf(snowflakeIdGenerator.nextId()));
        Long merchantId = spuMapper.getSpuById(define.getSpuId()).getMerchantId();
        order.setMerchantId(merchantId);
        order.setAddressId(grouponInitiateDTO.getAddressId().longValue());
        UserAddress userAddress = addressesMapper.selectById(grouponInitiateDTO.getAddressId());
        order.setReceiverName(userAddress.getReceiverName());
        order.setReceiverPhone(userAddress.getReceiverPhone());
        order.setReceiverAddress(userAddress.getProvince() + userAddress.getCity() +
                userAddress.getDistrict() + userAddress.getDetail());
        order.setPayableAmount(define.getGroupPrice().multiply(BigDecimal.valueOf(grouponInitiateDTO.getQuantity())).setScale(2, RoundingMode.HALF_UP));
        Double price = skuMapper.selectSkuById(define.getSkuId().longValue()).getPrice();
        order.setTotalAmount(BigDecimal.valueOf(price).multiply(BigDecimal.valueOf(grouponInitiateDTO.getQuantity())).setScale(2, RoundingMode.HALF_UP));
        BigDecimal discountAmount = new BigDecimal(price).subtract(define.getGroupPrice()).setScale(2, RoundingMode.HALF_UP);
        order.setDiscountAmount(discountAmount.multiply(BigDecimal.valueOf(grouponInitiateDTO.getQuantity())).setScale(2, RoundingMode.HALF_UP));
        order.setGrouponInstanceId(grouponInstance.getId());
        order.setStatus(0);
        orderMapper.insert(order);
        // 插入order_item表
        OrderItem orderItem = new OrderItem();
        orderItem.setOrderId(order.getId());
        orderItem.setSkuId(define.getSkuId().longValue());
        orderItem.setSpuId(define.getSpuId().longValue());
        Spu spuById = spuMapper.getSpuById(define.getSpuId());
        orderItem.setTitle(spuById.getTitle());
        Sku sku = skuMapper.selectSkuById(define.getSkuId().longValue());
        orderItem.setSpecs(sku.getSpecs());
        orderItem.setImage(sku.getImage());
        orderItem.setPrice(define.getGroupPrice());
        orderItem.setQuantity(grouponInitiateDTO.getQuantity());
        orderItem.setAmount(orderItem.getPrice().multiply(BigDecimal.valueOf(orderItem.getQuantity()))
                .setScale(2, RoundingMode.HALF_UP));
        orderMapper.insertOrderItems(List.of(orderItem));
        GrouponInitiateVO grouponInitiateVO = new GrouponInitiateVO();
        BeanUtils.copyProperties(grouponInstance, grouponInitiateVO);
        grouponInitiateVO.setInstanceId(grouponInstance.getId());
        grouponInitiateVO.setOrderNo(order.getOrderNo());
        grouponInitiateVO.setGroupPrice(define.getGroupPrice());
        grouponInitiateVO.setPayableAmount(order.getPayableAmount());
        return grouponInitiateVO;
    }
}
