package com.zeewoncode.pdd_server.a.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zeewoncode.context.BaseContext;
import com.zeewoncode.dto.SpuStatusDTO;
import com.zeewoncode.entity.Spu;
import com.zeewoncode.entity.SpuCard;
import com.zeewoncode.entity.User;
import com.zeewoncode.pdd_server.a.service.SpuService;
import com.zeewoncode.pdd_server.mapper.GrouponMapper;
import com.zeewoncode.pdd_server.mapper.SkuMapper;
import com.zeewoncode.pdd_server.mapper.SpuMapper;
import com.zeewoncode.pdd_server.mapper.UserMapper;
import com.zeewoncode.pdd_server.mapper.po.GrouponInstancesCount;
import com.zeewoncode.result.PageResult;
import com.zeewoncode.result.Result;
import com.zeewoncode.utils.SafeCollectors;
import io.swagger.models.auth.In;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class SpuServiceImpl implements SpuService {

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private SpuMapper spuMapper;
    @Autowired
    private SkuMapper skuMapper;
    @Autowired
    private GrouponMapper grouponMapper;

    /**
     * 获取商品监督列表
     * @param merchantId
     * @param status
     * @param categoryId
     * @param page
     * @param size
     * @return
     */
    @Override
    public PageResult<SpuCard> listAllSpuCards(Long merchantId, Integer status,
                                               Long categoryId, Integer page, Integer size) {
        // spu:id , title, mainImage, sales
        // sku:minPrice, maxPrice
        // groupon_define: groupPrice, hasGroupon=true(status=1)
        Long userId = BaseContext.getCurrentId().longValue();
        if (!isAdmin(userId)) {
            throw new RuntimeException("没有此权限进行查看");
        }
        PageHelper.startPage(page, size);
        List<Long> spuIds = spuMapper.selectIds(merchantId, status, categoryId);
        PageInfo<Long> info = new PageInfo<>(spuIds);
        if (spuIds == null || spuIds.size() == 0) {
            return PageResult.empty(info);
        }
        // 通过spu表获取id, title, mainImage, sales
        List<Spu> spus = spuMapper.selectSpuByIds(spuIds);
        Map<Long, Spu> spuMap = SafeCollectors.toMap(spus, Spu::getId);
        // 通过spuIds在sku表获取minPrice, maxPrice
        List<SpuCard> skuPriceList = skuMapper.selectSkuMinPriceAndMaxPriceBySpuIds(spuIds);
        Map<Long, SpuCard> skuMap = SafeCollectors.toMap(skuPriceList, spuCard -> spuCard.getId().longValue());
        // 通过spuIds在groupon_define表获取groupPrice, hasGroupon
        List<SpuCard> grouponCards = grouponMapper.selectPriceBySpuIds(spuIds);
        Map<Long, SpuCard> grouponMap = SafeCollectors.toMap(grouponCards, SpuCard -> SpuCard.getId().longValue());
        List<SpuCard> spuCardsList = spuIds.stream().map(spuId -> {
            Spu spu = spuMap.get(spuId);
            if (spu == null) {
                return null;
            }
            SpuCard spuCard = new SpuCard();
            spuCard.setId(spu.getId().intValue());
            spuCard.setTitle(spu.getTitle());
            spuCard.setMainImage(spu.getMainImage());
            spuCard.setSales(spu.getSales());
            SpuCard defaultCard = new SpuCard(spuId.intValue(), "", "", 0.0, 0.0, 0, 0.0, false);
            SpuCard skuCard = skuMap.getOrDefault(spuId, defaultCard);
            SpuCard grouponCard = grouponMap.getOrDefault(spuId, defaultCard);
            if (skuCard != null) {
                spuCard.setMinPrice(skuCard.getMinPrice());
                spuCard.setMaxPrice(skuCard.getMaxPrice());
            }
            if (grouponCard != null) {
                spuCard.setGroupPrice(grouponCard.getGroupPrice());
                spuCard.setHasGroupon(grouponCard.getHasGroupon());
            }
            return spuCard;
        }).collect(Collectors.toList());
        return PageResult.of(info, spuCardsList);
    }

    /**
     * 强制下架 / 恢复
     * @param spuId
     * @param dto
     * @return
     */
    @Override
    public Result PlatformForceControl(Long spuId, SpuStatusDTO dto) {
        Long userId = BaseContext.getCurrentId().longValue();
        if (!isAdmin(userId)) {
            throw new RuntimeException("没有此权限进行查看");
        }
        Spu spuById = spuMapper.getSpuById(spuId);
        if ( spuById == null || spuById.getDeletedFlag() != 0) {
            return Result.error("商品不存在");
        }
        Spu spu = Spu.builder().id(spuId).status(dto.getStatus()).build();
        int i = spuMapper.update(spu);
        if (i == 0) {
            return Result.error("商品状态更新失败，请稍后重试");
        }
        return Result.success("商品状态更新成功");
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
