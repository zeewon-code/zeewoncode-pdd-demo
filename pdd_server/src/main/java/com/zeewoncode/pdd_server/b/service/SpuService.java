package com.zeewoncode.pdd_server.b.service;

import com.zeewoncode.dto.SpuCreateDTO;
import com.zeewoncode.dto.SpuEditDTO;
import com.zeewoncode.entity.SpuCard;
import com.zeewoncode.entity.SpuDetail;
import com.zeewoncode.result.PageResult;
import com.zeewoncode.vo.SpuCreateVO;

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

    /**
     * 新建商品
     * @param dto
     * @return
     */
    SpuCreateVO addSpu(SpuCreateDTO dto);

    /**
     * 商品详情
     * @param id
     * @return
     */
    SpuDetail getSpuDetail(Long id);

    /**
     * 编辑商品
     * @param id
     * @param dto
     */
    void editSpu(Long id, SpuEditDTO dto);
}
