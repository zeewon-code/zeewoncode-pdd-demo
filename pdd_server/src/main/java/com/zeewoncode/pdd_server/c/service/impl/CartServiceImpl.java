package com.zeewoncode.pdd_server.c.service.impl;

import com.zeewoncode.context.BaseContext;
import com.zeewoncode.entity.*;
import com.zeewoncode.pdd_server.c.service.CartService;
import com.zeewoncode.pdd_server.mapper.*;
import com.zeewoncode.vo.CartItemVO;
import com.zeewoncode.vo.CartResult;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CartServiceImpl implements CartService {

    @Autowired
    private CartMapper cartMapper;
    @Autowired
    private SkuMapper skuMapper;
    @Autowired
    private SpuMapper spuMapper;
    @Autowired
    private MerchantMapper merchantMapper;

    /**
     * 查看购物车列表
     * @return
     */
    @Override
    public CartResult getCartList() {
        Integer userId = BaseContext.getCurrentId();
        CartResult cartResult = new CartResult();
        List<CartItem> cartItemList = cartMapper.selectCartItemByUserId(userId);
        Double totalAmountCopy = 0.0;
        for (CartItem cartItem : cartItemList) {
            Long skuId = cartItem.getSkuId();
            Integer quantity = cartItem.getQuantity();
            Sku sku = skuMapper.selectSkuById(skuId);
            Double price = sku.getPrice();
            totalAmountCopy += (quantity * price);
        }
        cartResult.setTotalAmount(new BigDecimal(totalAmountCopy.toString()));
        Map<Long, List<CartItem>> cartList = cartItemList.stream()
                .collect(Collectors.groupingBy(CartItem::getSpuId));
        List<CartGroup> groups = cartList.entrySet().stream().map(entry -> {
            Long spuId = entry.getKey();
            SpuDetail spuDetail = spuMapper.getSpuDetailById(spuId.intValue());
            Long merchantId = spuDetail.getMerchantId();
            Merchant merchant = merchantMapper.selectMerchantById(merchantId);
            String shopName = merchant.getShopName();
            List<CartItem> cartItems = entry.getValue();
            List<CartItemVO> cartItemVOS = new ArrayList<CartItemVO>();
            // 封装CartItemVO title, specs, image, price
            for (CartItem cartItem : cartItems) {
                // 将cartItem转换为cartItemVO
                CartItemVO cartItemVO = new CartItemVO();
                BeanUtils.copyProperties(cartItem, cartItemVO);
                // 填充 selected 将Integer 转换成 Boolean
                cartItemVO.setSelected(cartItem.getSelected() != null && cartItem.getSelected() != 0);
                // 填充 title
                SpuDetail spuDetailById = spuMapper.getSpuDetailById(cartItem.getSpuId().intValue());
                cartItemVO.setTitle(spuDetailById.getTitle());
                // 填充specs, image, price
                Sku sku = skuMapper.selectSkuById(cartItem.getSkuId());
                cartItemVO.setSpecs(sku.getSpecs());
                cartItemVO.setImage(sku.getImage());
                cartItemVO.setPrice(new BigDecimal(sku.getPrice()));
                cartItemVOS.add(cartItemVO);
            }
            return CartGroup.builder()
                    .merchantId(merchantId)
                    .shopName(shopName)
                    .items(cartItemVOS)
                    .build();
        }).collect(Collectors.toList());
        cartResult.setGroups(groups);
        return cartResult;
    }
}
