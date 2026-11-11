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
     * @param userId
     * @return
     */
    PageResult<SpuCard> listSpuCards(Integer page, Integer size, String keyword,
                                     Long categoryId, Integer status, Long userId);

    /**
     * 新建商品
     * @param dto
     * @param userId
     * @return
     */
    SpuCreateVO addSpu(SpuCreateDTO dto, Long userId);

    /**
     * 商品详情
     * @param id
     * @param userId
     * @return
     */
    SpuDetail getSpuDetail(Long id, Long userId);

    /**
     * 编辑商品
     * @param id
     * @param dto
     * @param userId
     */
    void editSpu(Long id, SpuEditDTO dto, Long userId);

    /**
     * 软删除商品
     * @param id
     * @param userId
     */
    void softDeleteSpu(Long id, Long userId);

    /**
     * 上架/下架商品
     * @param id
     * @param dto
     * @param userId
     */
    void listAndDelist(Long id, SpuStatusDTO dto, Long userId);


}
