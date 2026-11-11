package com.zeewoncode.pdd_server.a.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zeewoncode.entity.GrouponDefine;
import com.zeewoncode.entity.Spu;
import com.zeewoncode.pdd_server.a.service.GrouponService;
import com.zeewoncode.pdd_server.mapper.GrouponMapper;
import com.zeewoncode.pdd_server.mapper.SpuMapper;
import com.zeewoncode.pdd_server.mapper.po.GrouponInstancesCount;
import com.zeewoncode.result.PageResult;
import com.zeewoncode.utils.SafeCollectors;
import com.zeewoncode.vo.GrouponAdminItemVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class GrouponServiceImpl implements GrouponService {

    @Autowired
    private SpuMapper spuMapper;
    @Autowired
    private GrouponMapper grouponMapper;

    /**
     * 获取拼团活动监控列表
     * @param page
     * @param size
     * @return
     */
    @Override
    public PageResult<GrouponAdminItemVO> listGroupon(Integer page, Integer size) {
        // 3.查询商品id
        List<Long> spuIds = spuMapper.selectIds(null, 1, null);
        // 4.查询商品拼团信息
        PageHelper.startPage(page, size);
        List<GrouponDefine> grouponDefines = grouponMapper.selectGrouponDefinesBySpuIdAndStatus(spuIds, null);
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
                    .groupPrice(grouponDefine.getGroupPrice())
                    .targetCount(grouponDefine.getTargetCount())
                    .status(grouponDefine.getStatus())
                    .startTime(grouponDefine.getStartTime())
                    .endTime(grouponDefine.getEndTime())
                    .title(spuMap.get(grouponDefine.getSpuId()).getTitle())
                    .runningCount(countMap.get(grouponDefine.getId()).getRunningCount() != null
                            ? countMap.get(grouponDefine.getId()).getRunningCount() : 0)
                    .successCount(countMap.get(grouponDefine.getId()).getSuccessCount() != null
                            ? countMap.get(grouponDefine.getId()).getSuccessCount() : 0)
                    .failCount(countMap.get(grouponDefine.getId()).getFailCount() != null
                            ? countMap.get(grouponDefine.getId()).getFailCount() : 0)
                    .build();
            return grouponAdminItemVO;
        }).filter(Objects::nonNull).collect(Collectors.toList());
        return PageResult.of(pageInfo, vos);
    }

}
