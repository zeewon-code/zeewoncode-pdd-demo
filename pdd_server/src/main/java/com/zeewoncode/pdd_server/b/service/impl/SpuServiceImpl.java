package com.zeewoncode.pdd_server.b.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zeewoncode.constant.ErrorCode;
import com.zeewoncode.exception.BaseException;
import com.zeewoncode.dto.*;
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
import java.math.RoundingMode;
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
    public PageResult<SpuCard> listSpuCards(Integer pageNum, Integer pageSize,
                                            String keyword, Long categoryId,
                                            Integer status, Long userId) {
        Merchant merchant = merchantMapper.selectMerchantByUserId(userId);
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
        Map<Long, SpuCard> skuMap = SafeCollectors.toMap(skuList, SpuCard::getId);
        // groupon_define
        List<GrouponDefine> grouponDefineList = grouponMapper.selectGrouponDefineListBySpuIds(spuIds);
        Map<Long, GrouponDefine> grouponDefineMap = SafeCollectors.toMap(grouponDefineList, GrouponDefine::getSpuId);
        List<SpuCard> spuCardList = spuList.stream().map(spu -> {
            Long spuId = spu.getId();
            SpuCard spuCard = new SpuCard();
            spuCard.setId(spuId);
            spuCard.setTitle(spu.getTitle());
            spuCard.setMainImage(spu.getMainImage());
            spuCard.setMinPrice(skuMap.get(spuId).getMinPrice().setScale(2, RoundingMode.HALF_UP));
            spuCard.setMaxPrice(skuMap.get(spuId).getMaxPrice().setScale(2, RoundingMode.HALF_UP));
            spuCard.setSales(spu.getSales());
            GrouponDefine define = grouponDefineMap.get(spuId);
            spuCard.setGroupPrice(define != null ? define.getGroupPrice().setScale(2, RoundingMode.HALF_UP) : null);
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
    public SpuCreateVO addSpu(SpuCreateDTO dto, Long userId) {
        Merchant merchant = merchantMapper.selectMerchantByUserId(userId);
        // 对信息进行校验
        InfoValidator.validateName(dto.getTitle());
        if (dto.getSubtitle() != null) InfoValidator.validateName(dto.getSubtitle());
        if (dto.getMainImage() != null) InfoValidator.validateAvatarUrl(dto.getMainImage());
        if (dto.getImages() != null) InfoValidator.validateAvatarUrlList(dto.getImages());
        if (categoryMapper.selectCategoryById(dto.getCategoryId()) == null)
            throw new BaseException(ErrorCode.CATEGORY_NOT_EXIST);
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
        if (spuMapper.insert(spu) != 1)
            throw new BaseException(ErrorCode.SPU_ADD_FAILED);
        List<Sku> skuList = new ArrayList<>();
        for (SkuDTO skuDTO : dto.getSkus()) {
            Sku sku = new Sku();
            BeanUtils.copyProperties(skuDTO, sku);
            sku.setSpuId(spu.getId());
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
            throw new BaseException(ErrorCode.SKU_ADD_FAILED);
        }
        return SpuCreateVO.builder().spuId(spu.getId()).build();
    }

    /**
     * 获取商品详情
     * @param id
     * @return
     */
    @Override
    public SpuDetail getSpuDetail(Long id, Long userId) {
       // 校验当前商品是否属于当前店铺
        if (!haveSpuOperation(userId, id)) {
            throw new BaseException(ErrorCode.CURRENT_USER_NOT_PERMISSION);
        }
        SpuDetail spuDetail = spuMapper.getSpuDetailById(id);
        List<Spu> spus = spuMapper.selectSpuByIds(new ArrayList<>(List.of(id)));
        String images = spus.get(0).getImages();
        if (images != null) {
            List<String> imagesList = Arrays.stream(images.split(","))
                    .filter(item -> !item.isEmpty())
                    .collect(Collectors.toList());
            spuDetail.setImages(imagesList);
        }
        spuDetail.setShopName(merchantMapper.selectMerchantById(spuDetail.getMerchantId()).getShopName());
        if (spuDetail == null)
            throw new BaseException(ErrorCode.SPU_DETAIL_NOT_EXIST);
        List<Sku> skus = skuMapper.selectSkuListBySpuId(id);
        if (skus == null)
            throw new BaseException(ErrorCode.SKU_NOT_EXIST);
        List<GrouponDefine> defines = grouponMapper.selectGrouponDefineListBySpuId(id);
        SpuDetail detailVO = new SpuDetail();
        BeanUtils.copyProperties(spuDetail, detailVO);
        detailVO.setSkus(skus);
        detailVO.setGroupons(defines);
        return detailVO;
    }

    /**
     * 编辑商品
     * @param id
     * @param dto
     */
    @Override
    public void editSpu(Long id, SpuEditDTO dto, Long userId) {
        if (!haveSpuOperation(userId, id)) {
            throw new BaseException(ErrorCode.CURRENT_USER_NOT_PERMISSION);
        }
        if (dto.getCategoryId() != null && categoryMapper.selectCategoryById(dto.getCategoryId()) == null)
            throw new BaseException(ErrorCode.CATEGORY_NOT_EXIST);
        if (dto.getTitle() != null) InfoValidator.validateName(dto.getTitle());
        if (dto.getSubtitle() != null) InfoValidator.validateName(dto.getSubtitle());
        if (dto.getMainImage() != null) InfoValidator.validateAvatarUrl(dto.getMainImage());
        String images = null;
        if (dto.getImages() != null && dto.getImages().size() > 0) {
            InfoValidator.validateAvatarUrlList(dto.getImages());
            images = dto.getImages().toString();
        }
        Spu spu = new Spu();
        BeanUtils.copyProperties(dto, spu);
        spu.setImages(images);
        spu.setId(id);
        if (spuMapper.update(spu) == 0) {
            throw new BaseException(ErrorCode.SPU_UPDATE_FAILED);
        }
    }

    /**
     * 软删除商品
     * @param id
     */
    @Override
    @Transactional
    public void softDeleteSpu(Long id, Long userId) {
        if (!haveSpuOperation(userId, id)) {
            throw new BaseException(ErrorCode.CURRENT_USER_NOT_PERMISSION);
        }
        Spu spuById = spuMapper.getSpuById(id);
        // 判断该商品是否已下架再进行删除
        if (spuById.getStatus() == 1) {
            throw new BaseException(ErrorCode.SPU_DELETE_FAILED, "该商品还在上架中，请先下架之后，再进行删除");
        }
        int result = spuMapper.softDeleteSpu(id);
        int resultSku = skuMapper.softDeleteSku(id);
        if (result == 0 || resultSku == 0) {
            throw new BaseException(ErrorCode.SPU_DELETE_FAILED);
        }
    }

    /**
     * 上架/下架商品
     * @param id
     * @param dto
     */
    @Override
    public void listAndDelist(Long id, SpuStatusDTO dto, Long userId) {
        // 校验当前商品是否属于当前店铺
        if (!haveSpuOperation(userId, id)) {
            throw new BaseException(ErrorCode.CURRENT_USER_NOT_PERMISSION);
        }
        Spu spu = spuMapper.getSpuById(id);
        if (spu.getStatus().equals(dto.getStatus())) throw new BaseException(ErrorCode.SPU_SAME_STATUS_ALREADY);
        if (spuMapper.listAndDelist(id, dto.getStatus()) == (0)) throw new BaseException(ErrorCode.SPU_UPDATE_FAILED);
    }

    /**
     * 判断当前用户是否有资格去进行对商品进行操作
     * @param userId
     * @param spuId
     * @return
     */
    public Boolean haveSpuOperation(Long userId, Long spuId) {
        // 查当前用户所在的店铺
        Merchant merchant = merchantMapper.selectMerchantByUserId(userId);
        // 查出该商品信息
        Spu spu = spuMapper.getSpuById(spuId);
        if (merchant == null || spu == null || spu.getDeletedFlag() == 1) {
            throw new BaseException(ErrorCode.SPU_NOT_EXIST, "商品不存在或店铺信息不存在");
        }
        // 商品信息的店铺id 得跟当前用户的店铺对应
        if (!merchant.getId().equals(spu.getMerchantId())) {
            return false;
        }
        return true;
    }

}
