package com.zeewoncode.pdd_server.b.service;

import com.zeewoncode.dto.ShopApplyDTO;
import com.zeewoncode.dto.ShopUpdateDTO;
import com.zeewoncode.vo.BDashboard;
import com.zeewoncode.vo.MerchantVO;
import com.zeewoncode.vo.ShopApplyVO;

public interface MerchantService {

    /**
     * 申请开店
     * @param shopApplyDTO
     * @param userId
     * @return
     */
    ShopApplyVO applyShop(ShopApplyDTO shopApplyDTO, Long userId);

    /**
     * 获取我的店铺信息
     * @param userId
     * @return
     */
    MerchantVO getMyShop(Long userId);

    /**
     * 修改店铺信息
     * @param shopUpdateDTO
     * @param userId
     */
    void updateShop(ShopUpdateDTO shopUpdateDTO, Long userId);

    /**
     * 修改店铺状态
     * @param status
     * @param userId
     */
    void updateShopStatus(Integer status, Long userId);

    /**
     * 商家经营看板
     * @param userId
     * @return
     */
    BDashboard getBDashboard(Long userId);

}
