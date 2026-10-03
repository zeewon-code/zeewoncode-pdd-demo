package com.zeewoncode.pdd_server.c.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zeewoncode.entity.*;
import com.zeewoncode.pdd_server.c.service.SpuService;
import com.zeewoncode.pdd_server.mapper.GrouponMapper;
import com.zeewoncode.pdd_server.mapper.MerchantMapper;
import com.zeewoncode.pdd_server.mapper.SkuMapper;
import com.zeewoncode.pdd_server.mapper.SpuMapper;
import com.zeewoncode.req.SpuListQueryReq;
import com.zeewoncode.result.PageResult;
import com.zeewoncode.vo.CategoryNodeVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 *  C端-商品浏览模块
 */
@Service
public class SpuServiceImpl implements SpuService {

    @Autowired
    private SpuMapper spuMapper;
    @Autowired
    private GrouponMapper grouponMapper;
    @Autowired
    private SkuMapper skuMapper;
    @Autowired
    private MerchantMapper merchantMapper;
    /**
     * 获取商品分类树
     * @return
     */
    @Override
    public List<CategoryNodeVO> getCategoryTree() {
        // 1. 将所有的商品分类查询出来，按sort升序
        List<Category> List = spuMapper.selectListOrderByAsc();
        // 2. 转成VO，并建立map方便挂载
        Map<Long, CategoryNodeVO> NodeMap = new LinkedHashMap<>();
        List<CategoryNodeVO> root = new ArrayList<>();
        for(Category c : List) {
            CategoryNodeVO categoryNodeVO = new CategoryNodeVO();
            categoryNodeVO.setId(c.getId());
            categoryNodeVO.setName(c.getName());
            categoryNodeVO.setLevel(c.getLevel());
            categoryNodeVO.setChildren(new ArrayList<>());
            NodeMap.put(c.getId(), categoryNodeVO);
        }
        //3.按 parent_id 组装父子关系
        for (Category c : List) {
            CategoryNodeVO nodeVO = NodeMap.get(c.getId());
            if (c.getParentId() == 0L) {
                // 代表是一级分类
                root.add(nodeVO);
            } else {
                // 代表二级分类
                CategoryNodeVO parent = NodeMap.get(c.getParentId());
                if (parent != null) {
                    parent.getChildren().add(nodeVO);
                }
            }
        }
        return root;
    }

    /**
     * 获取商品列表
     * @param req
     * @return
     */
    @Override
    public PageResult<SpuCard> getSpuList(SpuListQueryReq req) {
        // id是商品表spu的id
        // title是商品表spu的title
        // mainImage是商品表spu的main_image
        // minPrice是商品规格表sku中的同个商品的最低价
        // 即在sku中同个spu_id的price最低价
        // maxPrice相反
        // sales是商品表spu的sales
        // groupPrice是拼团活动定义表中groupon_define中的group_price（可以通过sku_id关联），同规格之间才能进行拼团
        // hasGroupon是拼团活动定义表中的groupon_define中的status决定
        // 1.启动分页
        PageHelper.startPage(req.getPage(), req.getPageSize(),true);
        // 2.执行查询
        List<SpuCard> list = spuMapper.selectSpuList(req);
        // 3.包装分页结果
        PageInfo<SpuCard> spuCardPageInfo = new PageInfo<>(list);
        PageResult<SpuCard> result = new PageResult<>();
        result.setTotal((int) spuCardPageInfo.getTotal());
        result.setRecords(spuCardPageInfo.getList());
        result.setSize(spuCardPageInfo.getSize());
        result.setPage(spuCardPageInfo.getPageNum());
        return result;
    }

    /**
     * 获取商品详情
     * @param id // 商品表spu的id
     * @return
     */
    @Override
    public SpuDetail getSpuDetail(Integer id) {
        // 1.id 是商品表spu的id
        // 2.title 是商品表spu的title
        // 3.subtitle 是商品表spu的subtitle
        // 4.merchantId 是商品表spu的merchant_id
        // 5.shopName 是商家店铺表merchant的shop_name
        // 6.mainImage 是商品表spu的main_image
        // 7.images 是商品表spu的images
        // 8.details 是商品表spu的details
        // 9.sales 是商品表spu的sales
        // 10. skus 是商品规格表sku的集合(spu.id = sku.spu_id)
        // 11. groupons 是拼团活动定义表中groupon_define的集合(spu.id = groupon_define.spu_id)
        // a. 根据spuid查询sku商品规格，封装成skus集合
        List<Sku> skus = skuMapper.selectSkuListBySpuId(id);
        // b. 根据spuid查询拼团活动定义，封装成groupons集合
        List<GrouponDefine> groupons = grouponMapper.selectGrouponDefineListBySpuId(id);
        SpuDetail spuDetail = spuMapper.getSpuDetailById(id);
        Merchant merchant = merchantMapper.selectMerchantById(spuDetail.getMerchantId());
        spuDetail.setShopName(merchant.getShopName());
        spuDetail.setSkus(skus);
        spuDetail.setGroupons(groupons);
        return spuDetail;
    }
}
