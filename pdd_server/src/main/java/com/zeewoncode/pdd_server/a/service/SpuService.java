package com.zeewoncode.pdd_server.a.service;

import com.zeewoncode.dto.SpuStatusDTO;
import com.zeewoncode.entity.SpuCard;
import com.zeewoncode.result.PageResult;
import com.zeewoncode.result.Result;

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


    /**
     * 强制下架 / 恢复（平台兜底）
     * @param spuId
     * @param dto
     * @return
     */
    Result PlatformForceControl(Long spuId, SpuStatusDTO dto);
}
