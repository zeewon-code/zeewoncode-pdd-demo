package com.zeewoncode.pdd_server.c.service;

import com.zeewoncode.entity.SpuCard;
import com.zeewoncode.entity.SpuDetail;
import com.zeewoncode.req.SpuListQueryReq;
import com.zeewoncode.result.PageResult;
import com.zeewoncode.vo.CategoryNodeVO;

import java.util.List;
/**
 *  C端-商品浏览模块
 */
public interface SpuService {
    /**
     * 获取商品分类树
     * @return
     */
    List<CategoryNodeVO> getCategoryTree();

    /**
     * 获取商品列表
     * @param req
     * @return
     */
    PageResult<SpuCard> getSpuList(SpuListQueryReq req);

    /**
     * 获取商品详情
     * @param id
     * @return
     */
    SpuDetail getSpuDetail(Integer id);
}
