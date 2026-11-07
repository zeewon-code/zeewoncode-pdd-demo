package com.zeewoncode.pdd_server.a.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zeewoncode.context.BaseContext;
import com.zeewoncode.entity.User;
import com.zeewoncode.pdd_server.a.service.CouponService;
import com.zeewoncode.pdd_server.mapper.CouponMapper;
import com.zeewoncode.pdd_server.mapper.UserMapper;
import com.zeewoncode.result.PageResult;
import com.zeewoncode.vo.CouponVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CouponServiceImpl implements CouponService {

    @Autowired
    private CouponMapper couponMapper;
    @Autowired
    private UserMapper userMapper;

     /**
     * 列出优惠券列表（含已领和未领）
     * @param page
     * @param size
     * @return
     */
    @Override
    public PageResult<CouponVO> listCoupons(Integer page, Integer size) {
        Long userId = BaseContext.getCurrentId().longValue();
        if (!isAdmin(userId)) {
            throw new RuntimeException("没有此权限进行查看");
        }
        PageHelper.startPage(page, size);
        List<CouponVO> couponVOList = couponMapper.selectCoupons();
        PageInfo<CouponVO> couponVOPageInfo = new PageInfo<>(couponVOList);
        if (couponVOList.isEmpty()) {
            return PageResult.empty(couponVOPageInfo);
        }
        return PageResult.of(couponVOPageInfo, couponVOList);
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
