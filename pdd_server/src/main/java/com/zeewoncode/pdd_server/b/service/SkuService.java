package com.zeewoncode.pdd_server.b.service;


import com.zeewoncode.dto.SkuDTO;
import com.zeewoncode.dto.SkuUpdateDTO;
import com.zeewoncode.dto.SpuStatusDTO;
import com.zeewoncode.dto.StockDTO;

public interface SkuService {

    void addSku(Long id, SkuDTO dto, Integer userId);

    void editSku(Long skuId, SkuUpdateDTO dto, Integer userId);

    void deleteSku(Long id, Integer userId);

    void updateStock(Long id, StockDTO dto, Integer userId);

    void updateSkuStatus(Long id, SpuStatusDTO dto, Integer userId);
}
