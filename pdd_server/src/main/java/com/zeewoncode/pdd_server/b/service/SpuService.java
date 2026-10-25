package com.zeewoncode.pdd_server.b.service;

import com.zeewoncode.entity.SpuCard;
import com.zeewoncode.result.PageResult;

public interface SpuService {
    /**
     * 我的商品列表
     * @param page
     * @param size
     * @param keyword
     * @param categoryId
     * @param status
     * @return
     */
    PageResult<SpuCard> listSpuCards(Integer page, Integer size, String keyword, Long categoryId, Integer status);
}
