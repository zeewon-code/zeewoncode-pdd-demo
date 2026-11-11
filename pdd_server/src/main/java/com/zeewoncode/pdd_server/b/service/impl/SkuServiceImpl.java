package com.zeewoncode.pdd_server.b.service.impl;

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
        if (!haveSkuOperation(id, userId)) throw new RuntimeException("没有此权限进行新增商品规格");
        Spu spuById = spuMapper.getSpuById(id);
        if (spuById == null) throw new RuntimeException("该商品不存在");
        List<Sku> skuList = skuMapper.selectSkuListBySpuId(id);
        for (Sku skuDb : skuList) {
            if (skuDb.getSpecs().equals(dto.getSpecs())) {
                throw new RuntimeException("该商品已存在同规格");
            }
        }
        if (dto.getImage() != null) InfoValidator.validateAvatarUrl(dto.getImage());
        Sku sku = Sku.builder()
                .spuId(id)
                .specs(dto.getSpecs())
                .price(dto.getPrice())
                .stock(dto.getStock())
                .image(dto.getImage())
                .status(0)
                .deletedFlag(0)
                .build();
        if (skuMapper.insert(sku) == 0) {
            throw new RuntimeException("新增商品规格失败, 请检查信息是否正常");
        }
    }

    /**
     * 编辑商品规格
     * @param dto
     */
    @Override
    public void editSku(Long skuId, SkuUpdateDTO dto, Long userId) {
        Long spuId = skuMapper.selectById(skuId).getSpuId();
        if (!haveSkuOperation(spuId, userId)) throw new RuntimeException("没有此权限进行编辑商品规格");
        if (spuMapper.getSpuById(spuId) == null) throw new RuntimeException("该商品不存在");
        if (dto.getImage() != null) InfoValidator.validateAvatarUrl(dto.getImage());
        List<Sku> skuList = skuMapper.selectSkuListBySpuId(spuId);
        for (Sku skuDb : skuList) {
            if (skuDb.getSpecs().equals(dto.getSpecs()) && skuDb.getId().longValue() != skuId) {
                throw new RuntimeException("该商品已存在同规格");
            }
        }
        if (dto.getPrice() != null && dto.getPrice() <= 0) {
            throw new RuntimeException("价格不能为负数或0");
        }
        Sku sku = Sku.builder()
                .id(skuId)
                .price(dto.getPrice())
                .image(dto.getImage())
                .specs(dto.getSpecs())
                .build();
        if (skuMapper.update(sku) == 0) {
            throw new RuntimeException("编辑商品规格失败, 请稍后重试");
        }
    }

    /**
     * 删除商品规格
     * @param id
     */
    @Override
    @Transactional
    public void deleteSku(Long id, Long userId) {
        Sku skuDb = skuMapper.selectById(id);
        if (skuDb == null || skuDb.getDeletedFlag() == 1) {
            throw new RuntimeException("该商品规格不存在");
        }
        Long spuId = skuDb.getSpuId();
        if (!haveSkuOperation(spuId, userId)) throw new RuntimeException("没有此权限进行编辑商品规格");
        // 判断当前该sku是否禁售了
        if (skuDb.getStatus() == 1) {
            throw new RuntimeException("该商品规格未禁售，无法进行删除操作");
        }
        if (skuMapper.deleteSku(id) == 0) {
            throw new RuntimeException("删除商品规格失败, 请稍后重试");
        }
    }

    /**
     * 更新商品库存
     * @param id
     * @param dto
     */
    @Override
    public void updateStock(Long id, StockDTO dto, Long userId) {
        Sku skuDb = skuMapper.selectById(id);
        if (skuDb == null) {
            throw new RuntimeException("该商品规格不存在");
        }
        Long spuId = skuDb.getSpuId();
        if (!haveSkuOperation(spuId, userId)) throw new RuntimeException("没有此权限进行更新商品库存");
        Sku sku = Sku.builder().id(id).stock(dto.getStock()).build();
        if (skuMapper.update(sku) == 0) {
            throw new RuntimeException("更新商品库存失败, 请稍后重试");
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
        Sku skuById = skuMapper.selectSkuById(id);
        Long spuId = skuById.getSpuId();
        if (!haveSkuOperation(spuId, userId)) throw new RuntimeException("没有此权限进行商品规格状态的更新");
        Sku skuDb = skuMapper.selectById(id);
        if (skuDb == null) {
            throw new RuntimeException("该商品规格不存在");
        }
        Sku sku = Sku.builder().id(id).status(dto.getStatus()).build();
        if (skuMapper.update(sku) == 0) {
            throw new RuntimeException("商品规格状态更新失败, 请稍后重试");
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
                    throw new RuntimeException("订单状态更新失败, 请稍后重试");
                }
                // 修改订单对应的未发货物流信息状态为4（status = 4）
                List<Long> orderIds = orderMapper.selectOrderIdsByInstanceIds(instanceIds);
                if (shipmentMapper.updateShipmentStatusByOrderIds(orderIds, 4) == 0) {
                    throw new RuntimeException("物流信息状态更新失败, 请稍后重试");
                }
            }
        }
    }

    /**
     * 判断当前用户是否有权限进行商品操作
     * @param spuId
     * @return
     */
    public Boolean haveSkuOperation(Long spuId, Long userId) {
        Merchant merchant = merchantMapper.selectMerchantByUserId(userId);
        if (merchant == null || merchant.getDeletedFlag() == 0) {
            return false;
        }
        Spu spuById = spuMapper.getSpuById(spuId);
        if (!merchant.getId().equals(spuById.getMerchantId())) {
            return false;
        }
        return true;
    }

}
