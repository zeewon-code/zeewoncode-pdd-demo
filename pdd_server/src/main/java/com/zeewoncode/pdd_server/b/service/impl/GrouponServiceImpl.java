package com.zeewoncode.pdd_server.b.service.impl;

import com.github.pagehelper.PageInfo;
import com.zeewoncode.context.BaseContext;
import com.zeewoncode.entity.GrouponDefine;
import com.zeewoncode.entity.Merchant;
import com.zeewoncode.entity.Spu;
import com.zeewoncode.entity.User;
import com.zeewoncode.pdd_server.b.service.GrouponService;
import com.zeewoncode.pdd_server.mapper.GrouponMapper;
import com.zeewoncode.pdd_server.mapper.MerchantMapper;
import com.zeewoncode.pdd_server.mapper.SpuMapper;
import com.zeewoncode.pdd_server.mapper.UserMapper;
import com.zeewoncode.pdd_server.mapper.po.GrouponInstancesCount;
import com.zeewoncode.result.PageResult;
import com.zeewoncode.utils.SafeCollectors;
import com.zeewoncode.vo.GrouponAdminItemVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
        List<Long> grouponInstanceIds = grouponMapper.selectGrouponInstanceIdsByDefineIds(defineIdsList);
        List<GrouponInstancesCount> grouponInstancesCounts = grouponMapper.selectDefineCountByInstanceIds(grouponInstanceIds);
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
    };



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
