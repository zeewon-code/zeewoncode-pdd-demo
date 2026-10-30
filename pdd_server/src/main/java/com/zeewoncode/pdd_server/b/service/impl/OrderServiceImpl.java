package com.zeewoncode.pdd_server.b.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zeewoncode.context.BaseContext;
import com.zeewoncode.entity.Merchant;
import com.zeewoncode.entity.OrderCard;
import com.zeewoncode.entity.User;
import com.zeewoncode.pdd_server.b.service.OrderService;
import com.zeewoncode.pdd_server.mapper.MerchantMapper;
import com.zeewoncode.pdd_server.mapper.OrderMapper;
import com.zeewoncode.pdd_server.mapper.UserMapper;
import com.zeewoncode.result.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private MerchantMapper merchantMapper;
    @Autowired
    private UserMapper userMapper;

    /**
     * 获取本店订单列表
     * @param status
     * @param page
     * @param size
     * @return
     */
    @Override
    public PageResult<OrderCard> getOrderList(Integer status, Integer page, Integer size) {
        Integer userId = BaseContext.getCurrentId();
        User user = userMapper.selectById(userId);
        if (user == null || user.getUserType() != 2) {
            throw new RuntimeException("当前用户不存在或者当前用户不是店铺的管理员，没有该权限进行相关操作");
        }
        Merchant merchant = merchantMapper.selectMerchantByUserId(userId.longValue());
        if (merchant == null) {
            throw new RuntimeException("当前用户没有绑定店铺，没有该权限进行相关操作");
        }
        // 1.启动分页
        PageHelper.startPage(page, size, true);
        // 2.查询订单列表
        List<OrderCard> orderList = orderMapper.selectOrderList(null, status, merchant.getId());
        // 3.包装分页结果
        PageInfo<OrderCard> pageInfo = new PageInfo<>(orderList);
        if (pageInfo == null) {
            return PageResult.empty(pageInfo);
        }
        return PageResult.of(pageInfo, pageInfo.getList());
    }

//    /**
//     * 判断当前用户是否是商店的管理员
//     * @param userId
//     * @return
//     */
//    public Merchant isMerchantPermisson(Long userId) {
//        Merchant merchant = merchantMapper.selectMerchantByUserId(userId);
//        if (merchant == null) {
//            return null;
//        }
//        return merchant;
//    }
}
