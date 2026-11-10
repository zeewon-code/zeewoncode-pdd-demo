package com.zeewoncode.pdd_server.b.service;

import com.zeewoncode.dto.*;
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
    PageResult<SpuCard> listSpuCards(Integer page, Integer size, String keyword,
                                     Long categoryId, Integer status, Integer userId);

    /**
     * 新建商品
     * @param dto
     * @return
     */
    SpuCreateVO addSpu(SpuCreateDTO dto, Integer userId);

    /**
     * 商品详情
     * @param id
     * @return
     */
    SpuDetail getSpuDetail(Long id, Integer userId);

    /**
     * 编辑商品
     * @param id
     * @param dto
     */
    void editSpu(Long id, SpuEditDTO dto, Integer userId);

    /**
     * 软删除商品
     * @param id
     */
    void softDeleteSpu(Long id, Integer userId);

    /**
     * 上架/下架商品
     * @param id
     * @param dto
     */
    void listAndDelist(Long id, SpuStatusDTO dto, Integer userId);


}
