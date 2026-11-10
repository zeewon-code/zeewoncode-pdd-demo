package com.zeewoncode.pdd_server.a.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zeewoncode.entity.Merchant;
import com.zeewoncode.entity.User;
import com.zeewoncode.pdd_server.a.service.UserService;
import com.zeewoncode.pdd_server.mapper.MerchantMapper;
import com.zeewoncode.pdd_server.mapper.UserMapper;
import com.zeewoncode.result.PageResult;
import com.zeewoncode.result.Result;
import com.zeewoncode.vo.UserVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private MerchantMapper merchantMapper;

    /**
     * 获取用户列表
     * @param userType
     * @param status
     * @param phoneKeyword
     * @param nicknameKeyword
     * @param page
     * @param size
     * @return
     */
    @Override
    public PageResult<UserVO> getUsersList(Integer userType, Integer status, String phoneKeyword,
                                           String nicknameKeyword, Integer page, Integer size) {
        PageHelper.startPage(page, size);
        List<UserVO> userVOS = userMapper.selectUsers(userType, status, phoneKeyword, nicknameKeyword);
        PageInfo<UserVO> userVOPageInfo = new PageInfo<>(userVOS);
        if (userVOS == null || userVOS.isEmpty()) {
            return PageResult.empty(userVOPageInfo);
        }
        return PageResult.of(userVOPageInfo, userVOS);
    }

    /**
     * 获取用户信息
     * @param userId
     * @return
     */
    @Override
    public Result<UserVO> getUserInfo(Long userId) {
        User user = userMapper.selectById(userId.intValue());
        if (user == null) {
            return Result.error("该用户信息不存在");
        }
        UserVO userVO = BeanUtil.copyProperties(user, UserVO.class);
        return Result.success(userVO);
    }

    /**
     * 冻结 / 解冻用户
     * @param userId
     * @param status
     * @return
     */
    @Transactional
    @Override
    public Result updateUserStatus(Long userId, Integer status) {
        User user = userMapper.selectById(userId.intValue());
        if (user == null) {
            return Result.error("该用户信息不存在");
        }
        user.setStatus(status);
        int i = userMapper.updateById(user);
        // 如果用户类型为商户，则更新店铺营业状态
        if (user.getUserType() == 2) {
            Merchant merchant = Merchant.builder().userId(userId).status(0).build();
            merchantMapper.update(merchant);
        }
        return i > 0 ? Result.success() : Result.error("更新用户状态失败");
    }

}
