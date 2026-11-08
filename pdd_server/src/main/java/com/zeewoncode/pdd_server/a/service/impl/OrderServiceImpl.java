package com.zeewoncode.pdd_server.a.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zeewoncode.context.BaseContext;
import com.zeewoncode.entity.OrderCard;
import com.zeewoncode.entity.User;
import com.zeewoncode.pdd_server.a.service.OrderService;
import com.zeewoncode.pdd_server.mapper.OrderMapper;
import com.zeewoncode.pdd_server.mapper.UserMapper;
import com.zeewoncode.result.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private OrderMapper orderMapper;


    /**
     * 全平台订单查询
     * @param merchantId
     * @param status
     * @param startTime
     * @param endTime
     * @param page
     * @param size
     * @return
     */
    @Override
    public PageResult<OrderCard> selectOrders(Long merchantId, Integer status, LocalDateTime startTime,
                                              LocalDateTime endTime, Integer page, Integer size) {
        Long userId = BaseContext.getCurrentId().longValue();
        if (!isAdmin(userId)) {
            throw new RuntimeException("没有此权限进行查看");
        }
        // 1.启动分页
        PageHelper.startPage(page, size);
        // 2.查询订单列表
        List<OrderCard> orderList = orderMapper.selectOrderList(null, status, merchantId, startTime, endTime);
        // 3.包装分页结果
        PageInfo<OrderCard> pageInfo = new PageInfo<>(orderList);
        if (orderList.isEmpty()) {
            return PageResult.empty(pageInfo);
        }
        return PageResult.of(pageInfo, orderList);
    }

    // 校验当前用户是否为管理员
    private boolean isAdmin(Long userId) {
        User user = userMapper.selectById(userId.intValue());
        if (user == null || user.getUserType() != 9
                || user.getStatus() != 1 || user.getDeletedFlag() != 0) {
            return false;
        }
        return true;
    }
}
