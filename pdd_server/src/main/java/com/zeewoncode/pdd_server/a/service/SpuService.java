package com.zeewoncode.pdd_server.a.service;

import com.zeewoncode.entity.SpuCard;
import com.zeewoncode.result.PageResult;

import java.util.List;

public interface SpuService {

    /**
     * 获取商品监督列表
     * @param merchantId
     * @param status
     * @param categoryId
     * @param page
     * @param size
     * @return
     */
    PageResult<SpuCard> listAllSpuCards(Long merchantId, Integer status, Long categoryId, Integer page, Integer size);




}
