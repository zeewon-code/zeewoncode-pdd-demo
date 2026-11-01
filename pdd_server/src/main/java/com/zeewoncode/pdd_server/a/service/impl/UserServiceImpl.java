package com.zeewoncode.pdd_server.a.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zeewoncode.context.BaseContext;
import com.zeewoncode.entity.User;
import com.zeewoncode.pdd_server.a.service.UserService;
import com.zeewoncode.pdd_server.mapper.UserMapper;
import com.zeewoncode.result.PageResult;
import com.zeewoncode.result.Result;
import com.zeewoncode.vo.UserVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;

    public UserServiceImpl(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

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
    public PageResult<UserVO> getUsersList(Integer userType, Integer status, String phoneKeyword, String nicknameKeyword, Integer page, Integer size) {
        Long userId = BaseContext.getCurrentId().longValue();
        // 判断当前用户是否为管理员
        if (!isAdmin(userId)) {
            return PageResult.empty(new PageInfo());
        }
        log.info("【分页入参】page={}, size={}", page, size);
        PageHelper.startPage(page, size);
        List<UserVO> userVOS = userMapper.selectUsers(userType, status, phoneKeyword, nicknameKeyword);
        log.info("【返回类型】{}", userVOS.getClass().getName());
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
        Long id = BaseContext.getCurrentId().longValue();
        if (!(isAdmin(id))) {
            return Result.error("无权限访问");
        }
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
    @Override
    public Result updateUserStatus(Long userId, Integer status) {
        Long id = BaseContext.getCurrentId().longValue();
        if (!(isAdmin(id))) {
            return Result.error("无权限访问");
        }
        User user = userMapper.selectById(userId.intValue());
        if (user == null) {
            return Result.error("该用户信息不存在");
        }
        user.setStatus(status);
        int i = userMapper.updateById(user);
        return i > 0 ? Result.success() : Result.error("更新用户状态失败");
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
