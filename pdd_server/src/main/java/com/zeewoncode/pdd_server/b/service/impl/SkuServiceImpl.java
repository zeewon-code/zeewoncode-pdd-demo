package com.zeewoncode.pdd_server.b.service.impl;

import com.zeewoncode.constant.ErrorCode;
import com.zeewoncode.exception.BaseException;
import com.zeewoncode.dto.SkuDTO;
import com.zeewoncode.dto.SkuUpdateDTO;
import com.zeewoncode.dto.SpuStatusDTO;
import com.zeewoncode.dto.StockDTO;
import com.zeewoncode.entity.*;
import com.zeewoncode.pdd_server.b.service.SkuService;
import com.zeewoncode.pdd_server.mapper.*;
import com.zeewoncode.utils.InfoValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
public class SkuServiceImpl implements SkuService {

    @Autowired
    private SkuMapper skuMapper;
    @Autowired
    private SpuMapper spuMapper;
    @Autowired
    private MerchantMapper merchantMapper;
    @Autowired
    private GrouponMapper grouponMapper;
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private ShipmentMapper shipmentMapper;

    /**
     * 新增商品规格
     * @param id
     * @param dto
     */
    @Override
    public void addSku(Long id, SkuDTO dto, Long userId) {
        // 查当前用户所在的店铺
        Merchant merchant = merchantMapper.selectMerchantByUserId(userId);
        // 查出该商品信息
        Spu spu = spuMapper.getSpuById(id);
        if (!merchant.getId().equals(spu.getMerchantId())) {
            throw new BaseException(ErrorCode.CURRENT_USER_NOT_PERMISSION);
        }
        if (spu == null) throw new BaseException(ErrorCode.SPU_NOT_EXIST);
        List<Sku> skuList = skuMapper.selectSkuListBySpuId(id);
        for (Sku skuDb : skuList) {
            if (skuDb.getSpecs().equals(dto.getSpecs())) {
                throw new BaseException(ErrorCode.SKU_SAME_SPECS_ALREADY);
            }
        }
        if (dto.getImage() != null) InfoValidator.validateAvatarUrl(dto.getImage());
        Sku sku = Sku.builder()
                .spuId(id)
                .specs(dto.getSpecs())
                .price(dto.getPrice().setScale(2, RoundingMode.HALF_UP))
                .stock(dto.getStock())
                .image(dto.getImage())
                .status(0)
                .deletedFlag(0)
                .build();
        if (skuMapper.insert(sku) == 0) {
            throw new BaseException(ErrorCode.SKU_ADD_FAILED);
        }
    }

    /**
     * 编辑商品规格
     * @param dto
     */
    @Override
    public void editSku(Long skuId, SkuUpdateDTO dto, Long userId) {
        if (!haveSkuOperation(skuId, userId))
            throw new BaseException(ErrorCode.CURRENT_USER_NOT_PERMISSION);
        Long spuId = skuMapper.selectById(skuId).getSpuId();
        if (!haveSkuOperation(spuId, userId)) throw new BaseException(ErrorCode.CURRENT_USER_NOT_PERMISSION);
        if (spuMapper.getSpuById(spuId) == null) throw new BaseException(ErrorCode.SPU_NOT_EXIST);
        if (dto.getImage() != null) InfoValidator.validateAvatarUrl(dto.getImage());
        List<Sku> skuList = skuMapper.selectSkuListBySpuId(spuId);
        for (Sku skuDb : skuList) {
            if (skuDb.getSpecs().equals(dto.getSpecs()) && skuDb.getId().longValue() != skuId) {
                throw new BaseException(ErrorCode.SKU_SAME_SPECS_ALREADY);
            }
        }
        if (dto.getPrice() != null && dto.getPrice().compareTo(BigDecimal.ZERO) > 0) {
            throw new BaseException(ErrorCode.SKU_PRICE_ERROR);
        }
        Sku sku = Sku.builder()
                .id(skuId)
                .price(dto.getPrice().setScale(2, RoundingMode.HALF_UP))
                .image(dto.getImage())
                .specs(dto.getSpecs())
                .build();
        if (skuMapper.update(sku) == 0) {
            throw new BaseException(ErrorCode.SKU_UPDATE_FAILED);
        }
    }

    /**
     * 删除商品规格
     * @param id
     */
    @Override
    public void deleteSku(Long id, Long userId) {
        if (!haveSkuOperation(id, userId)) {
            throw new BaseException(ErrorCode.CURRENT_USER_NOT_PERMISSION);
        }
        Sku skuDb = skuMapper.selectById(id);
        // 判断当前该sku是否禁售了, 禁售了之后才能去删除
        if (skuDb.getStatus() == 1) {
            throw new BaseException(ErrorCode.SKU_IS_SALE);
        }
        if (skuMapper.deleteSku(id) == 0) {
            throw new BaseException(ErrorCode.SKU_DELETE_FAILED);
        }
    }

    /**
     * 更新商品库存
     * @param id
     * @param dto
     */
    @Override
    public void updateStock(Long id, StockDTO dto, Long userId) {
        if (!haveSkuOperation(id, userId))
            throw new BaseException(ErrorCode.CURRENT_USER_NOT_PERMISSION);
        Sku sku = Sku.builder().id(id).stock(dto.getStock()).build();
        if (skuMapper.update(sku) == 0) {
            throw new BaseException(ErrorCode.SKU_UPDATE_FAILED);
        }
    }

    /**
     * 商品规格可售/禁售
     * @param id
     * @param dto
     */
    @Override
    @Transactional
    public void updateSkuStatus(Long id, SpuStatusDTO dto, Long userId) {
        if (!haveSkuOperation(id, userId))
            throw new BaseException(ErrorCode.CURRENT_USER_NOT_PERMISSION);
        Sku sku = Sku.builder().id(id).status(dto.getStatus()).build();
        if (skuMapper.update(sku) == 0) {
            throw new BaseException(ErrorCode.SKU_UPDATE_FAILED);
        }
        // 该商品规格的拼团活动（设置停用）及修改实例(拼团中)状态，以及修改（拼团中）拼团订单状态及退款
        // 该商品规格的拼团活动（设置停用）
        GrouponDefine define = grouponMapper.selectGrouponDefineBySkuId(id);
        if (define != null) {
            Long defineId = define.getId();
            grouponMapper.updateGrouponDefineStatus(define.getId(), 0);
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

    /**
     * 判断当前用户是否有资格去进行对商品规格进行操作
     * @param userId
     * @param skuId
     * @return
     */
    public Boolean haveSkuOperation(Long userId, Long skuId) {
        // 查当前用户所在的店铺
        Merchant merchant = merchantMapper.selectMerchantByUserId(userId);
        // 查出该商品规格信息
        Sku sku = skuMapper.selectById(skuId);
        if (merchant == null || sku == null || sku.getDeletedFlag() == 1) {
            throw new BaseException(ErrorCode.SKU_NOT_EXIST, "商品规格不存在或店铺信息不存在");
        }
        // 查出该商品信息
        Spu spu = spuMapper.getSpuById(sku.getSpuId());
        // 商品信息的店铺id得跟当前用户的店铺对应
        if (!merchant.getId().equals(spu.getMerchantId())) {
            return false;
        }
        return true;
    }


}
