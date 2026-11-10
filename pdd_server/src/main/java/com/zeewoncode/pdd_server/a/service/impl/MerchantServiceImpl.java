package com.zeewoncode.pdd_server.a.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zeewoncode.entity.Merchant;
import com.zeewoncode.pdd_server.a.service.MerchantService;
import com.zeewoncode.pdd_server.mapper.MerchantMapper;
import com.zeewoncode.result.PageResult;
import com.zeewoncode.vo.MerchantVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MerchantServiceImpl implements MerchantService {

    @Autowired
    private MerchantMapper merchantMapper;


    /**
     * 获取店铺列表
     * @param status
     * @param keyword
     * @param page
     * @param size
     * @return
     */
    @Override
    public PageResult<MerchantVO> getMerchantsList(Integer status, String keyword, Integer page, Integer size) {
        PageHelper.startPage(page, size);
        List<MerchantVO> merchantVOList = merchantMapper.selectList(status, keyword);
        PageInfo<MerchantVO> pageInfo = new PageInfo<>(merchantVOList);
        if (merchantVOList == null || merchantVOList.size() == 0) {
            return PageResult.empty(pageInfo);
        }
        return PageResult.of(pageInfo, merchantVOList);
    }

    /**
     * 获取店铺详情
     * @param id
     * @return
     */
    @Override
    public MerchantVO getMerchantDetail(Long id) {
        Merchant merchant = merchantMapper.selectMerchantById(id);
        if (merchant == null || merchant.getDeletedFlag() != 0) {
            throw new RuntimeException("店铺不存在");
        }
        MerchantVO merchantVO = BeanUtil.copyProperties(merchant, MerchantVO.class);
        return merchantVO;
    }

    /**
     * 关店 / 恢复营业
     * @param merchantId
     * @param status
     */
    @Override
    public void updateMerchantStatus(Long merchantId, Integer status) {
        Merchant merchant = merchantMapper.selectMerchantById(merchantId);
        if (merchant == null || merchant.getDeletedFlag() != 0) {
            throw new RuntimeException("店铺不存在");
        }
        if (merchantMapper.updateMerchantStatus(merchantId, status) == 0) {
            throw new RuntimeException("更新店铺状态失败");
        }
    }

}
