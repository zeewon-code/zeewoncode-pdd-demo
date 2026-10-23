package com.zeewoncode.pdd_server.a.service;

import com.zeewoncode.dto.ShopApplyDTO;
import com.zeewoncode.vo.MerchantVO;
import com.zeewoncode.vo.ShopApplyVO;

public interface MerchantService {

    /**
     * 申请开店
     * @param shopApplyDTO
     * @return
     */
    ShopApplyVO applyShop(ShopApplyDTO shopApplyDTO);

    /**
     * 获取我的店铺信息
     * @return
     */
    MerchantVO getMyShop();
}
