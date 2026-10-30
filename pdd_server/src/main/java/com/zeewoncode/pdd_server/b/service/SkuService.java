package com.zeewoncode.pdd_server.b.service;


import com.zeewoncode.dto.SkuDTO;
import com.zeewoncode.dto.SkuUpdateDTO;
import com.zeewoncode.dto.StockDTO;

public interface SkuService {

    void addSku(Long id, SkuDTO dto);

    void editSku(Long id, SkuUpdateDTO dto);

    void deleteSku(Long id);

    void updateStock(Long id, StockDTO dto);
}
