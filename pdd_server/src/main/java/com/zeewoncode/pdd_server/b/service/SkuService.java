package com.zeewoncode.pdd_server.b.service;


import com.zeewoncode.dto.SkuDTO;
import com.zeewoncode.dto.SkuUpdateDTO;
import com.zeewoncode.dto.SpuStatusDTO;
import com.zeewoncode.dto.StockDTO;

public interface SkuService {

    /**
     * 新增商品规格
     * @param id
     * @param dto
     * @param userId
     */
    void addSku(Long id, SkuDTO dto, Long userId);

    /**
     * 修改商品规格
     * @param skuId
     * @param dto
     * @param userId
     */
    void editSku(Long skuId, SkuUpdateDTO dto, Long userId);

    /**
     * 删除商品规格
     * @param id
     * @param userId
     */
    void deleteSku(Long id, Long userId);

    /**
     * 修改商品规格库存
     * @param id
     * @param dto
     * @param userId
     */
    void updateStock(Long id, StockDTO dto, Long userId);

    /**
     * 修改商品规格状态
     * @param id
     * @param dto
     * @param userId
     */
    void updateSkuStatus(Long id, SpuStatusDTO dto, Long userId);
}
