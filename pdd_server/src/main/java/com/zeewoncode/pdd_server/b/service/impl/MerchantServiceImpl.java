package com.zeewoncode.pdd_server.b.service.impl;

import com.zeewoncode.constant.ErrorCode;
import com.zeewoncode.exception.BaseException;
import com.zeewoncode.dto.ShopApplyDTO;
import com.zeewoncode.dto.ShopUpdateDTO;
import com.zeewoncode.entity.Merchant;
import com.zeewoncode.entity.Order;
import com.zeewoncode.entity.User;
import com.zeewoncode.pdd_server.b.service.MerchantService;
import com.zeewoncode.pdd_server.mapper.*;
import com.zeewoncode.utils.InfoValidator;
import com.zeewoncode.vo.BDashboard;
import com.zeewoncode.vo.LowStockSkus;
import com.zeewoncode.vo.MerchantVO;
import com.zeewoncode.vo.ShopApplyVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;


@Service
public class MerchantServiceImpl implements MerchantService {

    @Autowired
    private MerchantMapper merchantMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private SpuMapper spuMapper;
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private SkuMapper skuMapper;


    /**
     * 申请开店
     * @param shopApplyDTO
     * @return
     */
    @Transactional
    @Override
    public ShopApplyVO applyShop(ShopApplyDTO shopApplyDTO, Long userId) {
        //建 merchant(status=1) → user.user_type 升级为 2。
        // 先判断当前用户有没有开店资格
        User userDb = userMapper.selectById(userId);
        if (userDb.getUserType() != 1)
            throw new BaseException(ErrorCode.CURRENT_USER_IDENTITY_NOT_USER);
        // 对 shopApplyDTO 进行校验
        InfoValidator.validateName(shopApplyDTO.getShopName());
        InfoValidator.validateAvatarUrl(shopApplyDTO.getLogo());
        Merchant merchantDb = merchantMapper.selectMerchantByShopName(shopApplyDTO.getShopName());
        if (merchantDb != null)
            throw new BaseException(ErrorCode.MERCHANT_NAME_EXIST_ALREADY);
        Merchant merchant = new Merchant();
        BeanUtils.copyProperties(shopApplyDTO, merchant);
        merchant.setStatus(1);
        merchant.setUserId(userId);
        merchant.setCreatedAt(LocalDateTime.now());
        merchant.setDeletedFlag(0);
        if (merchantMapper.insert(merchant) != 1)
            throw new BaseException(ErrorCode.MERCHANT_CREATE_FAILED);
        User user = User.builder().id(userId).userType(2).build();
        if (userMapper.updateById(user) != 1)
            throw new BaseException(ErrorCode.USER_UPDATE_FAILED);
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
    public MerchantVO getMyShop(Long userId) {
        Merchant merchant = merchantMapper.selectMerchantByUserId(userId);
        if (merchant == null)
            throw new BaseException(ErrorCode.MERCHANT_NOT_EXIST);
        return MerchantVO.builder()
                .id(merchant.getId())
                .shopName(merchant.getShopName())
                .logo(merchant.getLogo())
                .status(merchant.getStatus())
                .userId(merchant.getUserId())
                .build();
    }

    /**
     * 修改店铺信息
     * @param shopUpdateDTO
     */
    @Override
    public void updateShop(ShopUpdateDTO shopUpdateDTO, Long userId) {
        InfoValidator.validateName(shopUpdateDTO.getShopName());
        InfoValidator.validateAvatarUrl(shopUpdateDTO.getLogo());
        Merchant merchantDb = merchantMapper.selectMerchantByShopName(shopUpdateDTO.getShopName());
        if (merchantDb != null)
            throw new BaseException(ErrorCode.MERCHANT_NAME_EXIST_ALREADY);
        Merchant merchant = new Merchant();
        BeanUtils.copyProperties(shopUpdateDTO, merchant);
        merchant.setUserId(userId);
        if (merchantMapper.update(merchant) != 1)
            throw new BaseException(ErrorCode.MERCHANT_UPDATE_FAILED);
    }

    /**
     * 修改店铺状态
     * @param status
     */
    @Override
    public void updateShopStatus(Integer status, Long userId) {
        Merchant merchant = Merchant.builder().userId(userId).status(status).build();
        if (merchantMapper.update(merchant) != 1)
            throw new BaseException(ErrorCode.MERCHANT_UPDATE_FAILED);
    }

    /**
     * 商家经营看板
     * @return
     */
    @Override
    public BDashboard getBDashboard(Long userId) {
        Merchant merchant = merchantMapper.selectMerchantByUserId(userId);
        Long merchantId = merchant.getId();
        List<Order> orders = orderMapper.selectOrderByMerchantId(merchantId);
        if (orders == null || orders.isEmpty()) {
            return null;
        }
        LocalDate today = LocalDate.now();
        Long totalOrderCount = (long) orders.size();
        Long pendingShipCount = orders.stream().filter(order -> Integer.valueOf(1).equals(order.getStatus())).count();
        Long todayOrderCount = orders.stream().filter(order -> order.getCreatedAt().toLocalDate().equals(today)).count();
        List<Order> list = orders.stream().filter(order -> Integer.valueOf(1).equals(order.getStatus())
                || Integer.valueOf(2).equals(order.getStatus())
                || Integer.valueOf(3).equals(order.getStatus()))
                .toList();
        BigDecimal totalSales = list.stream()
                .map(Order::getPayableAmount)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal todaySales = list.stream()
                .filter(order -> order.getCreatedAt() != null
                        && order.getCreatedAt().toLocalDate().equals(today))
                .map(Order::getPayableAmount)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        List<Long> spuIds = spuMapper.selectIdsByMerchantId(merchantId);
        // lowStockSkus （低库存stock<=10）(通过orderIds去sku表左连接spu表)
        List<LowStockSkus> lowStockSkus = skuMapper.selectLowStockSkus(spuIds);
        BDashboard dashboard = BDashboard.builder().todaySales(todaySales).todayOrderCount(todayOrderCount)
                .totalSales(totalSales).totalOrderCount(totalOrderCount)
                .pendingShipCount(pendingShipCount)
                .lowStockSkus(lowStockSkus).build();
        return dashboard;
    }

}
