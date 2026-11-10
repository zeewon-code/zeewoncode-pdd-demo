package com.zeewoncode.pdd_server.b.service.impl;

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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;


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
    public ShopApplyVO applyShop(ShopApplyDTO shopApplyDTO, Integer userId) {
        //建 merchant(status=1) → user.user_type 升级为 2。
        // 先判断当前用户有没有开店资格
        User userDb = userMapper.selectById(userId);
        if (userDb.getUserType() != 1) {
            throw new RuntimeException("当前用户已经开店了或者该用户是管理员身份");
        }
        // 对 shopApplyDTO 进行校验
        InfoValidator.validateName(shopApplyDTO.getShopName());
        InfoValidator.validateAvatarUrl(shopApplyDTO.getLogo());
        Merchant merchantDb = merchantMapper.selectMerchantByShopName(shopApplyDTO.getShopName());
        if (merchantDb != null) {
            throw new RuntimeException("店铺名称已存在");
        }
        Merchant merchant = new Merchant();
        BeanUtils.copyProperties(shopApplyDTO, merchant);
        merchant.setStatus(1);
        merchant.setUserId(userId.longValue());
        merchant.setCreatedAt(LocalDateTime.now());
        merchant.setDeletedFlag(0);
        int i = merchantMapper.insert(merchant);
        if (i <= 0 ) {
            throw new RuntimeException("申请开店异常，请稍后重试");
        }
        User user = User.builder().id(userId).userType(2).build();
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
    public MerchantVO getMyShop(Integer userId) {
        if (!haveMerchantOperation(userId)) {
            throw new RuntimeException("当前用户没有权限进行相关操作");
        }
        Merchant merchant = merchantMapper.selectMerchantByUserId(userId.longValue());
        if (merchant == null) {
            throw new RuntimeException("店铺信息异常，请稍后重试");
        }
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
    public void updateShop(ShopUpdateDTO shopUpdateDTO, Integer userId) {
        if (!haveMerchantOperation(userId)) {
            throw new RuntimeException("当前用户没有权限进行相关操作");
        }
        InfoValidator.validateName(shopUpdateDTO.getShopName());
        InfoValidator.validateAvatarUrl(shopUpdateDTO.getLogo());
        Merchant merchantDb = merchantMapper.selectMerchantByShopName(shopUpdateDTO.getShopName());
        if (merchantDb != null) {
            throw new RuntimeException("店铺名称已存在");
        }
        Merchant merchant = new Merchant();
        BeanUtils.copyProperties(shopUpdateDTO, merchant);
        merchant.setUserId(userId.longValue());
        if (merchantMapper.update(merchant) == 0) {
            throw new RuntimeException("修改店铺信息异常，请稍后重试");
        }
    }

    /**
     * 修改店铺状态
     * @param status
     */
    @Override
    public void updateShopStatus(Integer status, Integer userId) {
        if (!haveMerchantOperation(userId)) {
            throw new RuntimeException("当前用户没有权限进行相关操作");
        }
        // 根据当前用户id查询店铺信息
        Merchant merchantDb = merchantMapper.selectMerchantByUserId(userId.longValue());
        if (merchantDb.getStatus().equals(status)) {
            throw new RuntimeException("店铺状态已为指定状态");
        }
        Merchant merchant = Merchant.builder().userId(userId.longValue()).status(status).build();
        int i = merchantMapper.update(merchant);
        if (i <= 0) {
            throw new RuntimeException("修改店铺状态异常，请稍后重试");
        }
    }

    /**
     * 商家经营看板
     * @return
     */
    @Override
    public BDashboard getBDashboard(Integer userId) {
        if (!haveMerchantOperation(userId)) {
            throw new RuntimeException("当前用户没有权限进行相关操作");
        }
        Merchant merchant = merchantMapper.selectMerchantByUserId(userId.longValue());
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
        Double totalSales = list.stream()
                .mapToDouble(order -> order.getPayableAmount().doubleValue()).sum();
        Double todaySales = list.stream()
                .filter(order -> order.getCreatedAt().toLocalDate().equals(today))
                .mapToDouble(order -> order.getPayableAmount().doubleValue()).sum();
        List<Long> spuIds = spuMapper.selectIdsByMerchantId(merchantId);
        // lowStockSkus （低库存stock<=10）(通过orderIds去sku表左连接spu表)
        List<LowStockSkus> lowStockSkus = skuMapper.selectLowStockSkus(spuIds);
        BDashboard dashboard = BDashboard.builder().todaySales(todaySales).todayOrderCount(todayOrderCount)
                .totalSales(totalSales).totalOrderCount(totalOrderCount)
                .pendingShipCount(pendingShipCount)
                .lowStockSkus(lowStockSkus).build();
        return dashboard;
    }

    /**
     * 校验当前用户是否有资格进行相关操作
     * @return
     */
    public Boolean haveMerchantOperation(Integer userId) {
        Merchant merchant = merchantMapper.selectMerchantByUserId(userId.longValue());
        if (merchant == null || merchant.getDeletedFlag() != 0) {
            return false;
        }
        return true;
    }
}
