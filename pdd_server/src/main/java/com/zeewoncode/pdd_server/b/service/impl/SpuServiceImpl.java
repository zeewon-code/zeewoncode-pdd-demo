package com.zeewoncode.pdd_server.b.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zeewoncode.context.BaseContext;
import com.zeewoncode.dto.SkuDTO;
import com.zeewoncode.dto.SpuCreateDTO;
import com.zeewoncode.entity.*;
import com.zeewoncode.pdd_server.b.service.SpuService;
import com.zeewoncode.pdd_server.mapper.*;
import com.zeewoncode.result.PageResult;
import com.zeewoncode.utils.InfoValidator;
import com.zeewoncode.utils.SafeCollectors;
import com.zeewoncode.vo.SpuCreateVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
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
    @Autowired
    private CategoryMapper categoryMapper;

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
        List<Long> spuIds = SafeCollectors.distinctValues(spuList, Spu::getId);
        List<SpuCard> skuList = skuMapper.selectSkuMinPriceAndMaxPriceBySpuIds(spuIds);
        Map<Integer, SpuCard> skuMap = SafeCollectors.toMap(skuList, SpuCard::getId);
        // groupon_define
        List<GrouponDefine> grouponDefineList = grouponMapper.selectGrouponDefineListBySpuIds(spuIds);
        Map<Integer, GrouponDefine> grouponDefineMap = SafeCollectors.toMap(grouponDefineList, GrouponDefine::getSpuId);
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

    /**
     * 新建商品
     * @param dto
     * @return
     */
    @Override
    @Transactional
    public SpuCreateVO addSpu(SpuCreateDTO dto) {
        Long userId = BaseContext.getCurrentId().longValue();
        // 判断是否有资格新建商品
        Merchant merchant = merchantMapper.selectMerchantByUserId(userId);
        if (merchant == null) {
            throw new RuntimeException("没有此权限进行新建商品");
        }
        // 对信息进行校验
        InfoValidator.validateName(dto.getTitle());
        if (dto.getSubtitle() != null) InfoValidator.validateName(dto.getSubtitle());
        if (dto.getMainImage() != null) InfoValidator.validateAvatarUrl(dto.getMainImage());
        if (dto.getImages() != null) InfoValidator.validateAvatarUrlList(dto.getImages());
        if (categoryMapper.selectCategoryById(dto.getCategoryId()) == null) {
            throw new RuntimeException("新建商品失败, 该分类不存在");
        }
        // 涉及到的数据表，spu,sku
        List<String> images = dto.getImages();
        Spu spu = Spu.builder()
                .title(dto.getTitle())
                .subtitle(dto.getSubtitle())
                .mainImage(dto.getMainImage())
                .images(images != null ? images.toString() : null)
                .merchantId(merchant.getId())
                .categoryId(dto.getCategoryId())
                .details(dto.getDetails())
                .status(0)
                .deletedFlag(0)
                .build();
        if (spuMapper.insert(spu) == 0) {
            throw new RuntimeException("新建商品失败, 请检查信息是否正常");
        }
        List<Sku> skuList = new ArrayList<>();
        for (SkuDTO skuDTO : dto.getSkus()) {
            Sku sku = new Sku();
            BeanUtils.copyProperties(skuDTO, sku);
            sku.setSpuId(spu.getId().intValue());
            sku.setLockedStock(0);
            sku.setStatus(0);
            skuList.add(sku);
        }
        // 收集所有出现过的非空字段
        Set<String> activeFields = new LinkedHashSet<>();
        for (Sku sku : skuList) {
            if (sku.getSpuId() != null) activeFields.add("spu_id");
            if (sku.getSpecs() != null && !sku.getSpecs().isEmpty()) activeFields.add("specs");
            if (sku.getPrice() != null) activeFields.add("price");
            if (sku.getStock() != null) activeFields.add("stock");
            if (sku.getImage() != null && !sku.getImage().isEmpty()) activeFields.add("image");
        }
        if (skuMapper.insertBatch(skuList, new ArrayList<>(activeFields)) == 0) {
            throw new RuntimeException("新建商品失败, 请检查信息是否正常");
        }
        return SpuCreateVO.builder().spuId(spu.getId()).build();
    }

    /**
     * 获取商品详情
     * @param id
     * @return
     */
    @Override
    public SpuDetail getSpuDetail(Long id) {
        SpuDetail spuDetail = spuMapper.getSpuDetailById(id.intValue());
        List<Spu> spus = spuMapper.selectSpuByIds(new ArrayList<>(List.of(id.intValue())));
        String images = spus.get(0).getImages();
        if (images != null) {
            List<String> imagesList = Arrays.stream(images.split(","))
                    .filter(item -> !item.isEmpty())
                    .collect(Collectors.toList());
            spuDetail.setImages(imagesList);
        }
        spuDetail.setShopName(merchantMapper.selectMerchantById(spuDetail.getMerchantId()).getShopName());
        if (spuDetail == null) {
            throw new RuntimeException("该商品详情不存在");
        }
        List<Sku> skus = skuMapper.selectSkuListBySpuId(id.intValue());
        if (skus == null) {
            throw new RuntimeException("该商品规格不存在");
        }
        List<GrouponDefine> defines = grouponMapper.selectGrouponDefineListBySpuId(id.intValue());
        SpuDetail detailVO = new SpuDetail();
        BeanUtils.copyProperties(spuDetail, detailVO);
        detailVO.setSkus(skus);
        detailVO.setGroupons(defines);
        return detailVO;
    }


}
