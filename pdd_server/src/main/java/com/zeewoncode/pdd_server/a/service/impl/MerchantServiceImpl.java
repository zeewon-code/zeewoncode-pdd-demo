package com.zeewoncode.pdd_server.a.service.impl;

import com.zeewoncode.context.BaseContext;
import com.zeewoncode.dto.ShopApplyDTO;
import com.zeewoncode.entity.Merchant;
import com.zeewoncode.entity.User;
import com.zeewoncode.pdd_server.a.service.MerchantService;
import com.zeewoncode.pdd_server.mapper.MerchantMapper;
import com.zeewoncode.pdd_server.mapper.UserMapper;
import com.zeewoncode.utils.ShopValidator;
import com.zeewoncode.vo.MerchantVO;
import com.zeewoncode.vo.ShopApplyVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;


@Service
public class MerchantServiceImpl implements MerchantService {

    @Autowired
    private ShopValidator shopValidator;
    @Autowired
    private MerchantMapper merchantMapper;
    @Autowired
    private UserMapper userMapper;


    /**
     * 申请开店
     * @param shopApplyDTO
     * @return
     */
    @Transactional
    @Override
    public ShopApplyVO applyShop(ShopApplyDTO shopApplyDTO) {
        //建 merchant(status=1) → user.user_type 升级为 2。
        // 先判断当前用户有没有开店资格
        Long userId = BaseContext.getCurrentId().longValue();
        User userDb = userMapper.selectById(userId.intValue());
        if (userDb.getUserType() != 1) {
            throw new RuntimeException("当前用户已经开店了或者该用户是管理员身份");
        }
        // 对 shopApplyDTO 进行校验
        shopValidator.validate(shopApplyDTO.getShopName(), shopApplyDTO.getLogo());
        Merchant merchantDb = merchantMapper.selectMerchantByShopName(shopApplyDTO.getShopName());
        if (merchantDb != null) {
            throw new RuntimeException("店铺名称已存在");
        }
        Merchant merchant = new Merchant();
        BeanUtils.copyProperties(shopApplyDTO, merchant);
        merchant.setStatus(1);
        merchant.setUserId(userId);
        merchant.setCreatedAt(LocalDateTime.now());
        merchant.setDeletedFlag(0);
        int i = merchantMapper.insert(merchant);
        if (i <= 0 ) {
            throw new RuntimeException("申请开店异常，请稍后重试");
        }
        User user = User.builder().id(userId.intValue()).userType(2).build();
        if (userMapper.updateById(user) <= 0) {
            throw new RuntimeException("更新用户信息异常，请稍后重试");
        }
        return ShopApplyVO.builder()
                .merchantId(merchant.getId())
                .shopName(merchant.getShopName())
                .status(merchant.getStatus())
                .build();
    }


    /**
     * 获取我的店铺信息
     * @return
     */
    @Override
    public MerchantVO getMyShop() {
        Long userId = BaseContext.getCurrentId().longValue();
        Merchant merchant = merchantMapper.selectMerchantByUserId(userId);
        if (merchant == null) {
            throw new RuntimeException("当前用户没有店铺信息");
        }
        return MerchantVO.builder()
                .id(merchant.getId())
                .shopName(merchant.getShopName())
                .logo(merchant.getLogo())
                .status(merchant.getStatus())
                .userId(merchant.getUserId())
                .build();
    }
}
