package com.zeewoncode.pdd_server.a.service;

import com.zeewoncode.dto.ShopApplyDTO;
import com.zeewoncode.vo.ShopApplyVO;

public interface ShopService {

    /**
     * 申请开店
     * @param shopApplyDTO
     * @return
     */
    ShopApplyVO applyShop(ShopApplyDTO shopApplyDTO);
}
