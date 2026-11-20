package com.zeewoncode.pdd_server.b.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zeewoncode.constant.ErrorCode;
import com.zeewoncode.exception.BaseException;
import com.zeewoncode.dto.GrouponCreateDTO;
import com.zeewoncode.dto.SpuStatusDTO;
import com.zeewoncode.entity.*;
import com.zeewoncode.pdd_server.b.service.GrouponService;
import com.zeewoncode.pdd_server.mapper.*;
import com.zeewoncode.pdd_server.mapper.po.GrouponInstancesCount;
import com.zeewoncode.result.PageResult;
import com.zeewoncode.utils.SafeCollectors;
import com.zeewoncode.vo.GrouponAdminItemVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class GrouponServiceImpl implements GrouponService {

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
    public PageResult<GrouponAdminItemVO> getGrouponsList(Integer status, Integer page, Integer size, Long userId) {
        // id,spuId,skuId,groupPrice,targetCount,status,startTime,endTime (groupon_define)
        // title (spu)
        // runningCount successCount failCount (groupon_instance)
        // 2.查询merchantId
        Merchant merchant = merchantMapper.selectMerchantByUserId(userId);
        Long merchantId = merchant.getId();
        // 3.查询店铺旗下的商品id
        List<Long> spuIds = spuMapper.selectIdByMerchantId(merchantId);
        // 4.查询商品拼团信息
        PageHelper.startPage(page, size);
        List<GrouponDefine> grouponDefines = grouponMapper.selectGrouponDefinesBySpuIdAndStatus(spuIds, status);
        PageInfo<GrouponDefine> pageInfo = new PageInfo<>(grouponDefines);
        if (grouponDefines == null) {
            return PageResult.empty(pageInfo);
        }
        // 查询title (spu)
        List<Long> spuIdList = SafeCollectors.distinctValues(grouponDefines, GrouponDefine::getSpuId);
        List<Spu> spus = spuMapper.selectSpuByIds(spuIdList);
        Map<Long, Spu> spuMap = spuIdList.isEmpty()
                ? Collections.emptyMap()
                : SafeCollectors.toMap(spus, Spu::getId);
        // runningCount successCount failCount (groupon_instance)
        List<Long> defineIds = SafeCollectors.distinctValues(grouponDefines, GrouponDefine::getId);
        List<GrouponInstancesCount> grouponInstancesCounts = grouponMapper.selectDefineCountByDefineIds(defineIds);
        Map<Long, GrouponInstancesCount> countMap = new HashMap<>();
        if (!CollectionUtils.isEmpty(grouponInstancesCounts)) {
            countMap.putAll(SafeCollectors.toMap(grouponInstancesCounts, GrouponInstancesCount::getDefineId));
        }
        for (Long defineId : defineIds) {
            countMap.putIfAbsent(defineId, new GrouponInstancesCount(defineId, 0, 0, 0));
        }
        // 拼接GrouponAdminItemVO
        List<GrouponAdminItemVO> vos = grouponDefines.stream().map(grouponDefine -> {
            GrouponAdminItemVO grouponAdminItemVO = GrouponAdminItemVO.builder()
                    .id(grouponDefine.getId())
                    .spuId(grouponDefine.getSpuId())
                    .skuId(grouponDefine.getSkuId())
                    .groupPrice(grouponDefine.getGroupPrice().setScale(2, RoundingMode.HALF_UP))
                    .targetCount(grouponDefine.getTargetCount())
                    .status(grouponDefine.getStatus())
                    .startTime(grouponDefine.getStartTime())
                    .endTime(grouponDefine.getEndTime())
                    .title(spuMap.get(grouponDefine.getSpuId()).getTitle())
                    .runningCount(countMap.get(grouponDefine.getId()).getRunningCount() != null ? countMap.get(grouponDefine.getId()).getRunningCount() : 0)
                    .successCount(countMap.get(grouponDefine.getId()).getSuccessCount() != null ? countMap.get(grouponDefine.getId()).getSuccessCount() : 0)
                    .failCount(countMap.get(grouponDefine.getId()).getFailCount() != null ? countMap.get(grouponDefine.getId()).getFailCount() : 0)
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
    public void createGroupon(GrouponCreateDTO grouponCreateDTO, Long userId) {
        // 校验参数
        Sku sku = skuMapper.selectSkuById(grouponCreateDTO.getSkuId());
        if (sku == null) {
            throw new BaseException(ErrorCode.SPU_NOT_EXIST);
        }
        // 校验sku属于当前店铺
        Spu spuById = spuMapper.getSpuById(sku.getSpuId());
        Merchant merchant = merchantMapper.selectMerchantByUserId(userId);
        if (!spuById.getMerchantId().equals(merchant.getId())) {
            throw new BaseException(ErrorCode.SKU_INFO_ERROR);
        }
        // groupPrice 必须小于等于 sku本来的价格
        if ((grouponCreateDTO.getGroupPrice().compareTo(sku.getPrice())) > 0){
            throw new BaseException(ErrorCode.GROUPON_PRICE_ERROR);
        }
        // 校验startTime,endTime
        LocalDateTime startTime = grouponCreateDTO.getStartTime();
        LocalDateTime endTime = grouponCreateDTO.getEndTime();
        Integer expireHours = grouponCreateDTO.getExpireHours();
        if (!(startTime.isAfter(LocalDateTime.now()) && startTime.isBefore(endTime)
                && endTime.isAfter(startTime.plusHours(expireHours)))){
            throw new BaseException(ErrorCode.GROUPON_DEFINE_TIME_ERROR, "拼团活动时间错误或成团时间错误");
        }
        // 创建拼团活动
        GrouponDefine define = GrouponDefine.builder()
                .skuId(grouponCreateDTO.getSkuId())
                .spuId(sku.getSpuId())
                .groupPrice(grouponCreateDTO.getGroupPrice().setScale(2, RoundingMode.HALF_UP))
                .targetCount(grouponCreateDTO.getTargetCount())
                .expireHours(grouponCreateDTO.getExpireHours())
                .startTime(grouponCreateDTO.getStartTime())
                .endTime(grouponCreateDTO.getEndTime())
                .status(1)
                .createdAt(LocalDateTime.now())
                .build();
        if (grouponMapper.insertGrouponDefine(define) == 0) {
            throw new BaseException(ErrorCode.GROUPON_DEFINE_CREATE_FAILED);
        }
    }

    /**
     * 获取拼团活动详情
     * @param defineId
     * @return
     */
    @Override
    public GrouponAdminItemVO getGrouponDetail(Long defineId, Long userId) {
        // 2.校验该sku是否属于当前店铺
        GrouponDefine define = grouponMapper.selectGrouponDefineById(defineId);
        Spu spuById = spuMapper.getSpuById(define.getSpuId());
        Merchant merchant = merchantMapper.selectMerchantByUserId(userId);
        if (!spuById.getMerchantId().equals(merchant.getId())) {
            throw new BaseException(ErrorCode.SKU_INFO_ERROR);
        }
        // 3.查询拼团详情
        GrouponDefine grouponDefine = grouponMapper.selectGrouponDefineById(defineId);
        if (grouponDefine == null) {
            throw new BaseException(ErrorCode.GROUPON_DEFINE_NOT_EXIST);
        }
        // title, runningCount, successCount, failCount
        List<GrouponInstancesCount> grouponInstancesCounts = grouponMapper.selectDefineCountByDefineIds(List.of(defineId));
        GrouponInstancesCount grouponInstancesCount = grouponInstancesCounts.get(0);
        GrouponAdminItemVO grouponAdminItemVO = GrouponAdminItemVO.builder()
                .id(grouponDefine.getId())
                .spuId(grouponDefine.getSpuId())
                .skuId(grouponDefine.getSkuId())
                .groupPrice(grouponDefine.getGroupPrice().setScale(2, RoundingMode.HALF_UP))
                .targetCount(grouponDefine.getTargetCount())
                .status(grouponDefine.getStatus())
                .startTime(grouponDefine.getStartTime())
                .endTime(grouponDefine.getEndTime())
                .title(spuById.getTitle())
                .runningCount(grouponInstancesCount.getRunningCount())
                .successCount(grouponInstancesCount.getSuccessCount())
                .failCount(grouponInstancesCount.getFailCount())
                .build();
        return grouponAdminItemVO;
    }

    /**
     * 启用 / 停用拼团活动
     * @param defineId
     * @param dto
     * @return
     */
    @Override
    @Transactional
    public void updateGrouponDefineStatus(Long defineId, SpuStatusDTO dto, Long userId) {
        // 2.校验该sku是否属于当前店铺
        GrouponDefine define = grouponMapper.selectGrouponDefineById(defineId);
        Spu spuById = spuMapper.getSpuById(define.getSpuId());
        Merchant merchant = merchantMapper.selectMerchantByUserId(userId);
        if (!spuById.getMerchantId().equals(merchant.getId())) {
            throw new BaseException(ErrorCode.SKU_INFO_ERROR);
        }
        // 3.查询拼团活动是否存在
        GrouponDefine grouponDefine = grouponMapper.selectGrouponDefineById(defineId);
        if (grouponDefine == null) {
            throw new BaseException(ErrorCode.GROUPON_DEFINE_NOT_EXIST);
        }
        if (grouponDefine.getStatus().equals(dto.getStatus())) {
            throw new BaseException(ErrorCode.GROUPON_DEFINE_STATUS_UPDATE_FAILED,
                    "当前拼团活动状态已为" + (dto.getStatus().equals(1) ? "启用" : "停用"));
        }
        // 4.更新拼团活动状态
        if (grouponMapper.updateGrouponDefineStatus(defineId, dto.getStatus()) == 0) {
            throw new BaseException(ErrorCode.GROUPON_DEFINE_STATUS_UPDATE_FAILED);
        }
        if (dto.getStatus().equals(0)) {
            List<GrouponInstance> instanceList = grouponMapper.selectGrouponInstanceByDefineIdAndStatus(defineId, 0);
            List<Long> instanceIds = instanceList.stream().map(GrouponInstance::getId).toList();
            // 修改实例(拼团中)状态
            // 修改（拼团中）拼团订单状态及退款
            if (!instanceIds.isEmpty()) {
                grouponMapper.updateGrouponInstanceStatusByInstanceIds(instanceIds, 3);
                // 修改订单状态status为4（已取消）和cancelType为3（拼团失败）
                if (orderMapper.updateOrderStatusAndCancelTypeByInstanceIds(instanceIds, 4, 3) == 0) {
                    throw new BaseException(ErrorCode.ORDER_UPDATE_FAILED);
                }
                // 修改订单对应的未发货物流信息状态为4（status = 4）
                List<Long> orderIds = orderMapper.selectOrderIdsByInstanceIds(instanceIds);
                if (shipmentMapper.updateShipmentStatusByOrderIds(orderIds, 4) == 0) {
                    throw new BaseException(ErrorCode.SHIPMENT_UPDATE_FAILED);
                }
            }
        }
    }

}
