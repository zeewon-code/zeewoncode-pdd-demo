package com.zeewoncode.pdd_server.b.service.impl;

import com.github.pagehelper.PageInfo;
import com.zeewoncode.context.BaseContext;
import com.zeewoncode.dto.GrouponCreateDTO;
import com.zeewoncode.dto.SpuStatusDTO;
import com.zeewoncode.entity.*;
import com.zeewoncode.pdd_server.b.service.GrouponService;
import com.zeewoncode.pdd_server.mapper.*;
import com.zeewoncode.pdd_server.mapper.po.GrouponInstancesCount;
import com.zeewoncode.result.PageResult;
import com.zeewoncode.result.Result;
import com.zeewoncode.utils.SafeCollectors;
import com.zeewoncode.vo.GrouponAdminItemVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class GrouponServiceImpl implements GrouponService {

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private MerchantMapper merchantMapper;
    @Autowired
    private SpuMapper spuMapper;
    @Autowired
    private GrouponMapper grouponMapper;
    @Autowired
    private SkuMapper skuMapper;
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private ShipmentMapper shipmentMapper;

    /**
     * 店家获取店铺拼团列表
     * @param status
     * @param page
     * @param size
     * @return
     */
    @Override
    public PageResult<GrouponAdminItemVO> getGrouponsList(Integer status, Integer page, Integer size) {
        // id,spuId,skuId,groupPrice,targetCount,status,startTime,endTime (groupon_define)
        // title (spu)
        // runningCount successCount failCount (groupon_instance)
        // 1.判断当前用户是否是商店的管理员
        Long userId = BaseContext.getCurrentId().longValue();
        Integer result = isMerchantAdmin(userId.intValue());
        if (result != 4) {
            throw new RuntimeException("当前用户没有权限进行相关操作");
        }
        // 2.查询merchantId
        Merchant merchant = merchantMapper.selectMerchantByUserId(userId);
        Long merchantId = merchant.getId();
        // 3.查询店铺旗下的商品id
        List<Long> spuIds = spuMapper.selectIdByMerchantId(merchantId);
        // 4.查询商品拼团信息
        List<GrouponDefine> grouponDefines = grouponMapper.selectGrouponDefinesBySpuIdAndStatus(spuIds, status);
        PageInfo<GrouponDefine> pageInfo = new PageInfo<>(grouponDefines);
        if (grouponDefines == null) {
            return PageResult.empty(pageInfo);
        }
        // 查询title (spu)
        List<Integer> spuIdList = SafeCollectors.distinctValues(grouponDefines, GrouponDefine::getSpuId);
        List<Spu> spus = spuMapper.selectSpuByIds(spuIdList);
        Map<Long, Spu> spuMap = spuIdList.isEmpty()
                ? Collections.emptyMap()
                : SafeCollectors.toMap(spus, Spu::getId);
        // runningCount successCount failCount (groupon_instance)
        List<Integer> defineIds = SafeCollectors.distinctValues(grouponDefines, GrouponDefine::getId);
        List<Long> defineIdsList = defineIds.stream().map(Integer::longValue).toList();
        List<GrouponInstancesCount> grouponInstancesCounts = grouponMapper.selectDefineCountByDefineIds(defineIdsList);
        Map<Long, GrouponInstancesCount> countMap = defineIdsList.isEmpty()
                ? Collections.emptyMap()
                : SafeCollectors.toMap(grouponInstancesCounts, GrouponInstancesCount::getDefineId);
        for (Long defineId : defineIdsList) {
            countMap.putIfAbsent(defineId, new GrouponInstancesCount(defineId, 0, 0, 0));
        }
        // 拼接GrouponAdminItemVO
        List<GrouponAdminItemVO> vos = grouponDefines.stream().map(grouponDefine -> {
            GrouponAdminItemVO grouponAdminItemVO = GrouponAdminItemVO.builder()
                    .id(grouponDefine.getId().longValue())
                    .spuId(grouponDefine.getSpuId().longValue())
                    .skuId(grouponDefine.getSkuId().longValue())
                    .groupPrice(grouponDefine.getGroupPrice().doubleValue())
                    .targetCount(grouponDefine.getTargetCount())
                    .status(grouponDefine.getStatus())
                    .startTime(grouponDefine.getStartTime())
                    .endTime(grouponDefine.getEndTime())
                    .title(spuMap.get(grouponDefine.getSpuId().longValue()).getTitle())
                    .runningCount(countMap.get(grouponDefine.getId().longValue()).getRunningCount() != null ? countMap.get(grouponDefine.getId().longValue()).getRunningCount() : 0)
                    .successCount(countMap.get(grouponDefine.getId().longValue()).getSuccessCount() != null ? countMap.get(grouponDefine.getId().longValue()).getSuccessCount() : 0)
                    .failCount(countMap.get(grouponDefine.getId().longValue()).getFailCount() != null ? countMap.get(grouponDefine.getId().longValue()).getFailCount() : 0)
                    .build();
            return grouponAdminItemVO;
        }).filter(Objects::nonNull).collect(Collectors.toList());
        return PageResult.of(pageInfo, vos);
    }

    /**
     * 创建拼团
     * @param grouponCreateDTO
     * @return
     */
    @Override
    public Result createGroupon(GrouponCreateDTO grouponCreateDTO) {
        Long userId = BaseContext.getCurrentId().longValue();
        Integer result = isMerchantAdmin(userId.intValue());
        if (result != 4) {
            throw new RuntimeException("当前用户没有权限进行相关操作");
        }
        // 校验参数
        Sku sku = skuMapper.selectSkuById(grouponCreateDTO.getSkuId());
        if (sku == null) {
            return Result.error("当前商品规格不存在或该商品规格未启售，无法创建拼团活动");
        }
        // 校验sku属于当前店铺
        Spu spuById = spuMapper.getSpuById(sku.getSpuId().longValue());
        Merchant merchant = merchantMapper.selectMerchantByUserId(userId);
        if (!spuById.getMerchantId().equals(merchant.getId())) {
            return Result.error("当前商品规格不属于当前店铺，无法创建拼团活动");
        }
        // groupPrice 必须小于等于 sku本来的价格
        if (grouponCreateDTO.getGroupPrice() >= sku.getPrice()) {
            return Result.error("拼团价格必须小于商品规格价格");
        }
        // 校验startTime,endTime
        LocalDateTime startTime = grouponCreateDTO.getStartTime();
        LocalDateTime endTime = grouponCreateDTO.getEndTime();
        Integer expireHours = grouponCreateDTO.getExpireHours();
        if (!(startTime.isAfter(LocalDateTime.now()) && startTime.isBefore(endTime)
                && endTime.isAfter(startTime.plusHours(expireHours)))){
            return Result.error("成团期限或拼团活动时间设置错误，请重新设置");
        }
        // 创建拼团活动
        GrouponDefine define = GrouponDefine.builder()
                .skuId(grouponCreateDTO.getSkuId().intValue())
                .spuId(sku.getSpuId())
                .groupPrice(new BigDecimal(String.valueOf(grouponCreateDTO.getGroupPrice())))
                .targetCount(grouponCreateDTO.getTargetCount())
                .expireHours(grouponCreateDTO.getExpireHours())
                .startTime(grouponCreateDTO.getStartTime())
                .endTime(grouponCreateDTO.getEndTime())
                .status(1)
                .createdAt(LocalDateTime.now())
                .build();
        int i = grouponMapper.insertGrouponDefine(define);
        if (i == 0) {
            return Result.error("创建拼团活动失败，请稍后重试");
        }
        return Result.success("创建拼团活动成功");
    }

    /**
     * 获取拼团活动详情
     * @param defineId
     * @return
     */
    @Override
    public Result<GrouponAdminItemVO> getGrouponDetail(Long defineId) {
        // 1.判断当前用户是否是店铺的管理员
        Long userId = BaseContext.getCurrentId().longValue();
        Integer result = isMerchantAdmin(userId.intValue());
        if (result != 4) {
            throw new RuntimeException("当前用户没有权限进行相关操作");
        }
        // 2.校验该sku是否属于当前店铺
        GrouponDefine define = grouponMapper.selectGrouponDefineById(defineId);
        Integer spuId = define.getSpuId();
        Spu spuById = spuMapper.getSpuById(define.getSpuId().longValue());
        Merchant merchant = merchantMapper.selectMerchantByUserId(userId);
        if (!spuById.getMerchantId().equals(merchant.getId())) {
            return Result.error("当前商品规格不属于当前店铺，无法查询该拼团活动详情");
        }
        // 3.查询拼团详情
        GrouponDefine grouponDefine = grouponMapper.selectGrouponDefineById(defineId);
        if (grouponDefine == null) {
            return Result.error("当前拼团活动不存在");
        }
        // title, runningCount, successCount, failCount
        List<GrouponInstancesCount> grouponInstancesCounts = grouponMapper.selectDefineCountByDefineIds(List.of(defineId));
        GrouponInstancesCount grouponInstancesCount = grouponInstancesCounts.get(0);
        GrouponAdminItemVO grouponAdminItemVO = GrouponAdminItemVO.builder()
                .id(grouponDefine.getId().longValue())
                .spuId(grouponDefine.getSpuId().longValue())
                .skuId(grouponDefine.getSkuId().longValue())
                .groupPrice(grouponDefine.getGroupPrice().doubleValue())
                .targetCount(grouponDefine.getTargetCount())
                .status(grouponDefine.getStatus())
                .startTime(grouponDefine.getStartTime())
                .endTime(grouponDefine.getEndTime())
                .title(spuById.getTitle())
                .runningCount(grouponInstancesCount.getRunningCount())
                .successCount(grouponInstancesCount.getSuccessCount())
                .failCount(grouponInstancesCount.getFailCount())
                .build();
        return Result.success(grouponAdminItemVO);
    }

    /**
     * 启用 / 停用拼团活动
     * @param defineId
     * @param dto
     * @return
     */
    @Override
    @Transactional
    public Result updateGrouponDefineStatus(Long defineId, SpuStatusDTO dto) {
        // 1.判断当前用户是否是店铺的管理员
        Long userId = BaseContext.getCurrentId().longValue();
        Integer result = isMerchantAdmin(userId.intValue());
        if (result != 4) {
            throw new RuntimeException("当前用户没有权限进行相关操作");
        }
        // 2.校验该sku是否属于当前店铺
        GrouponDefine define = grouponMapper.selectGrouponDefineById(defineId);
        Integer spuId = define.getSpuId();
        Spu spuById = spuMapper.getSpuById(define.getSpuId().longValue());
        Merchant merchant = merchantMapper.selectMerchantByUserId(userId);
        if (!spuById.getMerchantId().equals(merchant.getId())) {
            return Result.error("当前商品规格不属于当前店铺，无法查询该拼团活动详情");
        }
        // 3.查询拼团活动是否存在
        GrouponDefine grouponDefine = grouponMapper.selectGrouponDefineById(defineId);
        if (grouponDefine == null) {
            return Result.error("当前拼团活动不存在");
        }
        if (grouponDefine.getStatus() == dto.getStatus()) {
            return Result.success("当前拼团活动状态已为" + (dto.getStatus() == 1 ? "启用" : "停用"));
        }
        // 4.更新拼团活动状态
        int i = grouponMapper.updateGrouponDefineStatus(defineId, dto.getStatus());
        if (i == 0) {
            return Result.error("拼团活动状态更新失败，请稍后重试");
        }
        if (dto.getStatus() == 0) {
            List<GrouponInstance> instanceList = grouponMapper.selectGrouponInstanceByDefineIdAndStatus(defineId, 0);
            List<Long> instanceIds = instanceList.stream().map(GrouponInstance::getId).toList();
            // 修改实例(拼团中)状态
            // 修改（拼团中）拼团订单状态及退款
            if (!instanceIds.isEmpty()) {
                grouponMapper.updateGrouponInstanceStatusByInstanceIds(instanceIds, 3);
                // 修改订单状态status为4（已取消）和cancelType为3（拼团失败）
                if (orderMapper.updateOrderStatusAndCancelTypeByInstanceIds(instanceIds, 4, 3) == 0) {
                    throw new RuntimeException("订单状态更新失败, 请稍后重试");
                }
                // 修改订单对应的未发货物流信息状态为4（status = 4）
                List<Long> orderIds = orderMapper.selectOrderIdsByInstanceIds(instanceIds);
                if (shipmentMapper.updateShipmentStatusByOrderIds(orderIds, 4) == 0) {
                    throw new RuntimeException("物流信息状态更新失败, 请稍后重试");
                }
            }
        }
        return Result.success("拼团活动状态更新成功");
    }


    /**
     * 校验当前用户是否有资格进行相关操作
     * @return
     */
    public Integer isMerchantAdmin(Integer userId) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            return 1; // 1表示当前用户不存在
        }
        Merchant merchant = merchantMapper.selectMerchantByUserId(userId.longValue());
        if (merchant == null) {
            return 2; // 2表示当前用户没有绑定店铺
        }
        if (user.getUserType() != 2) {
            return 3; // 3表示当前用户不是店铺的管理员
        }
        return 4; // 正常
    }
}
