package com.zeewoncode.pdd_server.c.controller;

import cn.hutool.core.bean.BeanUtil;
import com.zeewoncode.context.BaseContext;
import com.zeewoncode.entity.LoginResult;
import com.zeewoncode.entity.User;
import com.zeewoncode.pdd_server.c.service.UserService;
import com.zeewoncode.req.ChangePasswordReq;
import com.zeewoncode.req.LoginReq;
import com.zeewoncode.req.RegisterReq;
import com.zeewoncode.req.UpdateProfileReq;
import com.zeewoncode.result.Result;
import com.zeewoncode.vo.UserVO;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.*;

/**
 * C端-认证与用户模块
 */
@RestController
@RequestMapping("/api/")
@Slf4j
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 用户注册
     */
    @PostMapping("auth/register")
    public Result register(@RequestBody RegisterReq registerReq) {
        log.info("用户注册: {}", registerReq);
        Result<T> result = userService.register(registerReq);
        return result;
    }

    /**
     * 用户登录
     * @param loginReq
     * @return
     */
    @PostMapping("auth/login")
    public Result<LoginResult> login(@RequestBody LoginReq loginReq) {
        log.info("用户登录: {}", loginReq);
        Result<LoginResult> result = userService.login(loginReq);
        return result;
    }

    /**
     * 获取当前用户信息
     */
    @GetMapping("users/me")
    public Result<UserVO> getUserInfo() {
        Integer userId = BaseContext.getCurrentId();
        log.info("获取当前用户信息: {}", userId);
        User user = userService.getById(userId);
        UserVO userVO = BeanUtil.copyProperties(user, UserVO.class);
        return Result.success(userVO);
    }

    /**
     * 修改个人信息
     * @param updateProfileReq
     * @return
     */
    @PutMapping("users/me")
    public Result updateProfile(@RequestBody UpdateProfileReq updateProfileReq) {
        Integer userId = BaseContext.getCurrentId();
        log.info("修改个人信息: {},{}", updateProfileReq, userId);
        userService.updateProfile(updateProfileReq, userId);
        return Result.success();
    }

    /**
     * 修改密码
     * @param changePasswordReq
     * @return
     */
    @PutMapping("users/me/password")
    public Result changePassword(@RequestBody ChangePasswordReq changePasswordReq) {
        Integer userId = BaseContext.getCurrentId();
        log.info("修改密码: {},{}", changePasswordReq, userId);
        userService.changePassword(changePasswordReq, userId);
        return Result.success();
    }
}
