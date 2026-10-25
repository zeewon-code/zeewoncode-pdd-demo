package com.zeewoncode.pdd_server.b.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zeewoncode.context.BaseContext;
import com.zeewoncode.entity.*;
import com.zeewoncode.pdd_server.b.service.SpuService;
import com.zeewoncode.pdd_server.mapper.GrouponMapper;
import com.zeewoncode.pdd_server.mapper.MerchantMapper;
import com.zeewoncode.pdd_server.mapper.SkuMapper;
import com.zeewoncode.pdd_server.mapper.SpuMapper;
import com.zeewoncode.result.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class SpuServiceImpl implements SpuService {

    @Autowired
    private MerchantMapper merchantMapper;
    @Autowired
    private SpuMapper spuMapper;
    @Autowired
    private SkuMapper skuMapper;
    @Autowired
    private GrouponMapper grouponMapper;

    /**
     * 我的商品列表
     * @param pageNum
     * @param pageSize
     * @param keyword
     * @param categoryId
     * @param status
     * @return
     */
    @Override
    public PageResult<SpuCard> listSpuCards(Integer pageNum, Integer pageSize, String keyword, Long categoryId, Integer status) {
        Long userId = BaseContext.getCurrentId().longValue();
        Merchant merchant = merchantMapper.selectMerchantByUserId(userId);
        if (merchant == null) {
            throw new RuntimeException("没有此权限进行查看");
        }
        Long merchantId = merchant.getId();
        PageHelper.startPage(pageNum, pageSize);
        List<Spu> spuList = spuMapper.selectSpuByMerchantId(merchantId, keyword, categoryId, status);
        PageInfo<Spu> info = new PageInfo<>(spuList);
        if (spuList == null || spuList.size() == 0) {
            return PageResult.empty(info);
        }
        // 批量查表sku,groupon_define
        // 差minPrice,maxPrice,groupPrice,hasGroupon字段
        // sku
        List<Long> spuIds = distinctValues(spuList, Spu::getId);
        List<SpuCard> skuList = skuMapper.selectSkuMinPriceAndMaxPriceBySpuIds(spuIds);
        Map<Integer, SpuCard> skuMap = toMap(skuList, SpuCard::getId);
        // groupon_define
        List<GrouponDefine> grouponDefineList = grouponMapper.selectGrouponDefineListBySpuIds(spuIds);
        Map<Integer, GrouponDefine> grouponDefineMap = toMap(grouponDefineList, GrouponDefine::getSpuId);
        List<SpuCard> spuCardList = spuList.stream().map(spu -> {
            Integer id = spu.getId().intValue();
            SpuCard spuCard = new SpuCard();
            spuCard.setId(id);
            spuCard.setTitle(spu.getTitle());
            spuCard.setMainImage(spu.getMainImage());
            spuCard.setMinPrice(skuMap.get(id).getMinPrice());
            spuCard.setMaxPrice(skuMap.get(id).getMaxPrice());
            spuCard.setSales(spu.getSales());
            GrouponDefine define = grouponDefineMap.get(id);
            spuCard.setGroupPrice(define != null ? define.getGroupPrice().doubleValue() : null);
            spuCard.setHasGroupon(define != null ? true : false);
            return spuCard;
        }).filter(Objects::nonNull).collect(Collectors.toList());
        return PageResult.of(info, spuCardList);
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
