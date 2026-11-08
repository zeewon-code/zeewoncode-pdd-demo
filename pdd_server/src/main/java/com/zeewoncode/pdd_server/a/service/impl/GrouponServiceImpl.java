package com.zeewoncode.pdd_server.a.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zeewoncode.context.BaseContext;
import com.zeewoncode.entity.GrouponDefine;
import com.zeewoncode.entity.Spu;
import com.zeewoncode.entity.User;
import com.zeewoncode.pdd_server.a.service.GrouponService;
import com.zeewoncode.pdd_server.mapper.GrouponMapper;
import com.zeewoncode.pdd_server.mapper.SpuMapper;
import com.zeewoncode.pdd_server.mapper.UserMapper;
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
    private UserMapper userMapper;
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
        Long userId = BaseContext.getCurrentId().longValue();
        if (!isAdmin(userId)) {
            throw new RuntimeException("没有此权限进行查看");
        }
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
        List<Integer> spuIdList = SafeCollectors.distinctValues(grouponDefines, GrouponDefine::getSpuId);
        List<Spu> spus = spuMapper.selectSpuByIds(spuIdList.stream().map(Long::valueOf).collect(Collectors.toList()));
        Map<Long, Spu> spuMap = spuIdList.isEmpty()
                ? Collections.emptyMap()
                : SafeCollectors.toMap(spus, Spu::getId);
        // runningCount successCount failCount (groupon_instance)
        List<Integer> defineIds = SafeCollectors.distinctValues(grouponDefines, GrouponDefine::getId);
        List<Long> defineIdsList = defineIds.stream().map(Integer::longValue).toList();
        List<GrouponInstancesCount> grouponInstancesCounts = grouponMapper.selectDefineCountByDefineIds(defineIdsList);
        Map<Long, GrouponInstancesCount> countMap = new HashMap<>();
        if (!CollectionUtils.isEmpty(grouponInstancesCounts)) {
            countMap.putAll(SafeCollectors.toMap(grouponInstancesCounts, GrouponInstancesCount::getDefineId));
        }
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
                    .runningCount(countMap.get(grouponDefine.getId().longValue()).getRunningCount() != null
                            ? countMap.get(grouponDefine.getId().longValue()).getRunningCount() : 0)
                    .successCount(countMap.get(grouponDefine.getId().longValue()).getSuccessCount() != null
                            ? countMap.get(grouponDefine.getId().longValue()).getSuccessCount() : 0)
                    .failCount(countMap.get(grouponDefine.getId().longValue()).getFailCount() != null
                            ? countMap.get(grouponDefine.getId().longValue()).getFailCount() : 0)
                    .build();
            return grouponAdminItemVO;
        }).filter(Objects::nonNull).collect(Collectors.toList());
        return PageResult.of(pageInfo, vos);
    }

    // 校验当前用户是否为管理员
    private boolean isAdmin(Long userId) {
        User user = userMapper.selectById(userId.intValue());
        if (user == null || user.getUserType() != 9
                || user.getStatus() != 1 || user.getDeletedFlag() != 0) {
            return false;
        }
        return true;
    }
}
