package com.zeewoncode.pdd_server.c.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zeewoncode.context.BaseContext;
import com.zeewoncode.dto.GrouponInitiateDTO;
import com.zeewoncode.dto.GrouponJoinDTO;
import com.zeewoncode.dto.OrderItemDTO;
import com.zeewoncode.entity.*;
import com.zeewoncode.pdd_server.c.service.GrouponsService;
import com.zeewoncode.pdd_server.mapper.*;
import com.zeewoncode.pdd_server.mapper.po.InstanceRef;
import com.zeewoncode.result.PageResult;
import com.zeewoncode.utils.SnowflakeIdGenerator;
import com.zeewoncode.vo.GrouponInitiateVO;
import com.zeewoncode.vo.GrouponInstanceVO;
import com.zeewoncode.vo.GrouponJoinVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.function.support.RouterFunctionMapping;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

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
    @Autowired
    private RouterFunctionMapping routerFunctionMapping;
    @Autowired
    private UserMapper userMapper;

    /**
     * 拼团发起（团长下单）
     * @param grouponInitiateDTO
     * @return
     */
    @Override
    @Transactional
    public GrouponInitiateVO initiateGroupon(GrouponInitiateDTO grouponInitiateDTO) {
        Long defineId = grouponInitiateDTO.getDefineId();
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
        // 判断是否已经用户目前是否已经开起了拼团活动，
        // 如果已经发起该款商品活动且同商品拼团未结束；则不允许再发起新的同款商品的拼团活动
        if (grouponMapper.selectGrouponInstanceByDefineIdAndLeaderUserId(defineId, userId) != null) {
            throw new RuntimeException("您已经发起过该商品的拼团活动");
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
        Integer quantity = grouponInitiateDTO.getQuantity();
        Long addressId = grouponInitiateDTO.getAddressId();
        Order order = insertOrder(userId, define, addressId, quantity, grouponInstance);
        insertOrderItem(order, define, quantity);
        GrouponInitiateVO grouponInitiateVO = new GrouponInitiateVO();
        BeanUtils.copyProperties(grouponInstance, grouponInitiateVO);
        grouponInitiateVO.setInstanceId(grouponInstance.getId());
        grouponInitiateVO.setOrderNo(order.getOrderNo());
        grouponInitiateVO.setGroupPrice(define.getGroupPrice());
        grouponInitiateVO.setPayableAmount(order.getPayableAmount());
        return grouponInitiateVO;
    }

    /**
     * 插入订单表
     * @param userId
     * @param define
     * @param addressId
     * @param quantity
     * @param grouponInstance
     */
    public Order insertOrder(Long userId, GrouponDefine define, Long addressId,
                             Integer quantity, GrouponInstance grouponInstance) {
        Order order = new Order();
        order.setUserId(userId);
        order.setOrderNo(String.valueOf(snowflakeIdGenerator.nextId()));
        Long merchantId = spuMapper.getSpuById(define.getSpuId()).getMerchantId();
        order.setMerchantId(merchantId);
        order.setAddressId(addressId);
        UserAddress userAddress = addressesMapper.selectById(addressId);
        order.setReceiverName(userAddress.getReceiverName());
        order.setReceiverPhone(userAddress.getReceiverPhone());
        order.setReceiverAddress(userAddress.getProvince() + userAddress.getCity() +
                userAddress.getDistrict() + userAddress.getDetail());
        order.setPayableAmount(define.getGroupPrice().multiply(BigDecimal.valueOf(quantity)).setScale(2, RoundingMode.HALF_UP));
        Double price = skuMapper.selectSkuById(define.getSkuId().longValue()).getPrice();
        order.setTotalAmount(BigDecimal.valueOf(price).multiply(BigDecimal.valueOf(quantity)).setScale(2, RoundingMode.HALF_UP));
        BigDecimal discountAmount = new BigDecimal(price).subtract(define.getGroupPrice()).setScale(2, RoundingMode.HALF_UP);
        order.setDiscountAmount(discountAmount.multiply(BigDecimal.valueOf(quantity)).setScale(2, RoundingMode.HALF_UP));
        order.setGrouponInstanceId(grouponInstance.getId());
        order.setStatus(0);
        orderMapper.insert(order);
        return order;
    }

    /**
     * 插入订单项表
     * @param order
     * @param define
     * @param quantity
     * @return
     */
    public void insertOrderItem(Order order, GrouponDefine define, Integer quantity) {
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
        orderItem.setQuantity(quantity);
        orderItem.setAmount(orderItem.getPrice().multiply(BigDecimal.valueOf(orderItem.getQuantity()))
                .setScale(2, RoundingMode.HALF_UP));
        orderMapper.insertOrderItems(List.of(orderItem));
    }


    /**
     * 拼团加入（团员下单）
     * @param instanceId
     * @param grouponJoinDTO
     * @return
     */
    @Override
    @Transactional
    public GrouponJoinVO joinGroupon(Long instanceId, GrouponJoinDTO grouponJoinDTO) {
        Long userId = BaseContext.getCurrentId().longValue();
        LocalDateTime now = LocalDateTime.now();
        // 校验该拼团活动是否存在
        GrouponInstance grouponInstance = grouponMapper.selectGrouponInstanceById(instanceId);
        GrouponDefine define = grouponMapper.selectGrouponDefineById(grouponInstance.getDefineId());
        if (grouponInstance == null) {
            throw new RuntimeException("拼团事务不存在");
        }
        if (grouponInstance.getExpireAt().isBefore(now)) {
            throw new RuntimeException("拼团事务已结束");
        }
        if (grouponInstance.getStatus() != 0) {
            throw new RuntimeException("拼团事务已结束");
        }
        // 用户只能占用一个拼团事务中的一个名额
        // 通过拼团实例跟用户id查询订单，判断用户是否已经占用该拼团事务的名额
        if (grouponMapper.selectOrderByUserIdAndInstanceId(userId, instanceId) != null) {
            throw new RuntimeException("您已经参加了该同款商品规格的拼团活动了");
        }
        List<OrderItemDTO> list = new ArrayList<>();
        list.add(OrderItemDTO.builder()
                .skuId(define.getSkuId())
                .quantity(grouponJoinDTO.getQuantity())
                .build());
        boolean lock = skuMapper.lockStock(list);
        if (!lock) {
            throw new RuntimeException("库存不足");
        }
        // 乐观锁解决超员问题， 更新拼团实例当前拼团人数
        boolean isTrue = updateGrouponInstanceCurrentCount(instanceId);
        if (!isTrue) {
            throw new RuntimeException("拼团已满");
        }
        // 团员插入订单表
        Long addressId = grouponJoinDTO.getAddressId();
        Integer quantity = grouponJoinDTO.getQuantity();
        Order order = insertOrder(userId, define, addressId, quantity, grouponInstance);
        insertOrderItem(order, define, quantity);
        GrouponInstance updatedGrouponInstance = grouponMapper.selectGrouponInstanceById(instanceId);
        GrouponJoinVO grouponJoinVO = new GrouponJoinVO();
        grouponJoinVO.setTargetCount(updatedGrouponInstance.getTargetCount());
        grouponJoinVO.setGroupStatus(updatedGrouponInstance.getStatus());
        grouponJoinVO.setCurrentCount(updatedGrouponInstance.getCurrentCount());
        grouponJoinVO.setInstanceId(instanceId);
        grouponJoinVO.setOrderNo(order.getOrderNo());
        return grouponJoinVO;
    }

    /**
     * 乐观锁防超员
     * @param instanceId
     * @return
     */
    public boolean updateGrouponInstanceCurrentCount(Long instanceId) {
        int rows = grouponMapper.updateGrouponInstanceCurrentCount(instanceId);
        // 影响行数>0 表示参团成功
        return rows > 0;
    }

    // 涉及表：groupon_instance, user, spu, groupon_define, orders
    /**
     * 获取我的拼团实例列表
     * @return
     */
    @Override
    public PageResult<GrouponInstanceVO> getGrouponInstanceList(Integer pageNum, Integer pageSize) {
        Long userId = BaseContext.getCurrentId().longValue();
        PageHelper.startPage(pageNum, pageSize);
        // ===== 1. 只对「我的实例集合」分页 =====
        List<InstanceRef> refs = grouponMapper.selectMyGrouponRefs(userId);
        PageInfo<InstanceRef> pageInfo = new PageInfo<>(refs);
        if (refs.isEmpty()) {
            return PageResult.empty(pageInfo);
        }
        //2. 批量补查 5 张表=====
        // groupon_instance
        List<Long> instanceIds = distinctValues(refs, InstanceRef::getInstanceId);
        List<GrouponInstance> instances = grouponMapper.selectGrouponInstanceByIds(instanceIds);
        Map<Long, GrouponInstance> instanceMap = toMap(instances, GrouponInstance::getId);
        // groupon_define
        List<Long> defineIds = distinctValues(instances, GrouponInstance::getDefineId);
        Map<Integer, GrouponDefine> defineMap = defineIds.isEmpty()
                ? Collections.emptyMap()
                : toMap(grouponMapper.selectGrouponDefineByIds(defineIds), GrouponDefine::getId);
        // spu
        List<Integer> spuIds = distinctValues(defineMap.values(), GrouponDefine::getSpuId);
        Map<Long, Spu> spuMap = spuIds.isEmpty()
                ? Collections.emptyMap()
                : toMap(spuMapper.selectSpuByIds(spuIds), Spu::getId);
        // user
        List<Long> leaderUserIds = distinctValues(instances, GrouponInstance::getLeaderUserId);
        Map<Integer, User> userMap = leaderUserIds.isEmpty()
                ? Collections.emptyMap()
                : toMap(userMapper.selectUserByIds(leaderUserIds), User::getId);
        // orders
//        一次把「团长订单」和「我自己的订单」都捞出来
        List<Order> orders = orderMapper.selectOrderByInstanceIdsAndUserIds(instanceIds, userId, leaderUserIds);
        Map<Long, Map<Long, Order>> orderMap = orders.stream()
                .collect(Collectors.groupingBy(Order::getGrouponInstanceId,
                        Collectors.toMap(Order::getUserId, order -> order, (o1, o2) -> o1)));
        // 组装List<GrouponInstanceVO>
        List<GrouponInstanceVO> voList = refs.stream().map(ref -> {
            GrouponInstance grouponInstance = instanceMap.get(ref.getInstanceId());
            if (grouponInstance == null) return null;
            GrouponInstanceVO vo = new GrouponInstanceVO();
            vo.setInstanceId(grouponInstance.getId());
            vo.setDefineId(grouponInstance.getDefineId());
            vo.setLeaderUserId(grouponInstance.getLeaderUserId());
            vo.setCurrentCount(grouponInstance.getCurrentCount());
            vo.setTargetCount(grouponInstance.getTargetCount());
            vo.setStatus(grouponInstance.getStatus());
            vo.setExpireAt(grouponInstance.getExpireAt());
            vo.setRole(ref.getRole());
            GrouponDefine define = defineMap.get(grouponInstance.getDefineId().intValue());
            if (define != null) {
                vo.setGroupPrice(define.getGroupPrice() != null ? define.getGroupPrice().setScale(2, RoundingMode.HALF_UP) : null);
                Spu spu = spuMap.get(define.getSpuId().longValue());
                if (spu != null) vo.setSpuTitle(spu.getTitle());
            }

            // 团长
            User leaderUser = userMap.get(grouponInstance.getLeaderUserId().intValue());
            if (leaderUser != null) {
                vo.setLeaderNickname(leaderUser.getNickname());
            }
            Map<Long, Order> instOrders = orderMap.getOrDefault(grouponInstance.getId(), Collections.emptyMap());
            Long targetUserId = (ref.getRole() == 1) ? grouponInstance.getLeaderUserId() : userId;
            Order targetOrder = instOrders.get(targetUserId);
            if (targetOrder != null) {
                vo.setOrderNo(targetOrder.getOrderNo());
            }
            // remainCount
            if (vo.getTargetCount() != null && vo.getCurrentCount() != null) {
                vo.setRemainCount(Math.max(0, vo.getTargetCount() - vo.getCurrentCount()));
            }
            return vo;
        }).filter(Objects::nonNull).collect(Collectors.toList());
        return PageResult.of(pageInfo, voList);
    }

    //安全的 List 转 Map
    private <E, K> Map<K, E> toMap(List<E> list, Function<E, K> keyFn) {
        if (list == null || list.isEmpty()) return Collections.emptyMap();
        return list.stream().collect(Collectors.toMap(keyFn, Function.identity(), (a, b) -> a));
    }

    //安全的字段提取 + 去重
    private <E, V> List<V> distinctValues(Collection<E> src, Function<E, V> fn) {
        if (src == null || src.isEmpty()) return Collections.emptyList();
        return src.stream().map(fn).filter(Objects::nonNull).distinct().collect(Collectors.toList());
    }


}
