package com.zeewoncode.pdd_server.a.service;

import com.zeewoncode.result.PageResult;
import com.zeewoncode.vo.MerchantVO;

public interface MerchantService {

    /**
     * 获取店铺列表
     * @param status
     * @param keyword
     * @param page
     * @param size
     * @return
     */
    PageResult<MerchantVO> getMerchantsList(Integer status, String keyword, Integer page, Integer size);

    /**
     * 获取店铺详情
     * @param id
     * @return
     */
    MerchantVO getMerchantDetail(Long id);
}
