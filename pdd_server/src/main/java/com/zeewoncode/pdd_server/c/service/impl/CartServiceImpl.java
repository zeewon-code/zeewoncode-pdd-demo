package com.zeewoncode.pdd_server.c.service.impl;

import com.zeewoncode.context.BaseContext;
import com.zeewoncode.entity.*;
import com.zeewoncode.pdd_server.c.service.CartService;
import com.zeewoncode.pdd_server.mapper.*;
import com.zeewoncode.req.CartAddReq;
import com.zeewoncode.req.CartQuantityReq;
import com.zeewoncode.req.CartSelectReq;
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
        List<CartItem> cartItemList = cartMapper.selectCartItemByUserId(userId.longValue());
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

    /**
     * 加入购物车（同 sku 数量累加）
     * @param req
     */
    @Override
    public void addCart(CartAddReq req) {
        // 1. 对sku表的stock,status和deleted_flag进行校验
        Sku sku = skuMapper.selectSkuById(req.getSkuId());
        if (sku == null || sku.getStock() <= 0 || sku.getStatus() != 1 || sku.getDeletedFlag() != 0) {
            throw new RuntimeException("该商品规格已售罄或已下架");
        }
        // 2. 对spu表的status和deleted_flag进行校验
        Spu spuDetail = spuMapper.getSpuById(sku.getSpuId());
        if (spuDetail.getStatus() != 1 || spuDetail.getDeletedFlag() != 0) {
            throw new RuntimeException("该商品已下架");
        }
        // 3. 对购物车表进行操作，加入购物车（同 sku 数量累加）
        // 3.1 查询购物车表，判断该用户是否已经将该商品规格加入过购物车
        Long userId = BaseContext.getCurrentId().longValue();
        Long skuId = req.getSkuId();
        List<CartItem> cartItemList = cartMapper.selectCartItemByUserIdAndSkuId(userId, skuId);
        // 3.2 已存在则更新数量
        if (cartItemList != null && cartItemList.size() == 1) {
            CartItem cartItem = cartItemList.get(0);
            cartItem.setQuantity(cartItem.getQuantity() + req.getQuantity());
            cartMapper.updateCartItem(cartItem, userId);
        } else {
            // 3.3 不存在则插入新记录
            // 填充spuId
            Integer spuId = skuMapper.selectSkuById(skuId).getSpuId();
            CartItem cartItem = CartItem.builder()
                    .userId(userId)
                    .skuId(req.getSkuId())
                    .spuId(spuId.longValue())
                    .quantity(req.getQuantity())
                    .selected(1)
                    .build();
            cartMapper.insert(cartItem);
        }
    }

    /**
     * 更新购物车数量
     * @param id
     * @param req
     */
    @Override
    public void updateCartQuantity(Long id, CartQuantityReq req) {
        cartMapper.updateCartQuantity(id, req.getQuantity());
    }

    /**
     * 勾选 / 取消勾选购物车（支持批量）
     * @param req
     */
    @Override
    public void selectCart(CartSelectReq req) {
        List<Integer> ids = req.getIds();
        Boolean isSelected = req.getSelected();
        Integer selected = isSelected != null && isSelected ? 1 : 0;
        cartMapper.updateCartSelectByIds(ids, selected);
    }

    /**
     * 删除购物车
     * @param id
     */
    @Override
    public void deleteCart(Long id) {
        cartMapper.deleteCartById(id);
    }
}
