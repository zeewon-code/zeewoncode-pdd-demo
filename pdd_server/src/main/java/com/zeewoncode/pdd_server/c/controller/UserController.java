package com.zeewoncode.pdd_server.c.controller;

import cn.hutool.core.bean.BeanUtil;
import com.zeewoncode.context.BaseContext;
import com.zeewoncode.vo.LoginVO;
import com.zeewoncode.entity.User;
import com.zeewoncode.pdd_server.c.service.UserService;
import com.zeewoncode.dto.ChangePasswordDTO;
import com.zeewoncode.dto.LoginDTO;
import com.zeewoncode.dto.RegisterDTO;
import com.zeewoncode.dto.UpdateProfileDTO;
import com.zeewoncode.result.Result;
import com.zeewoncode.vo.UserVO;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * C端-认证与用户模块
 */
@RestController
@RequestMapping("/api/")
@Slf4j
public class UserController {

    @Autowired
    private com.zeewoncode.pdd_server.c.service.UserService userService;

    /**
     * 用户注册
     */
    @PostMapping("auth/register")
    public Result register(@RequestBody RegisterDTO registerDTO) {
        log.info("用户注册: {}", registerDTO);
        Result<T> result = userService.register(registerDTO);
        return result;
    }

    /**
     * 用户登录
     * @param loginDTO
     * @return
     */
    @PostMapping("auth/login")
    public Result<LoginVO> login(@RequestBody LoginDTO loginDTO) {
        log.info("用户登录: {}", loginDTO);
        Result<LoginVO> result = userService.login(loginDTO);
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
     * @param updateProfileDTO
     * @return
     */
    @PutMapping("users/me")
    public Result updateProfile(@RequestBody UpdateProfileDTO updateProfileDTO) {
        Integer userId = BaseContext.getCurrentId();
        log.info("修改个人信息: {},{}", updateProfileDTO, userId);
        userService.updateProfile(updateProfileDTO, userId);
        return Result.success();
    }

    /**
     * 修改密码
     * @param changePasswordDTO
     * @return
     */
    @PutMapping("users/me/password")
    public Result changePassword(@RequestBody ChangePasswordDTO changePasswordDTO) {
        Integer userId = BaseContext.getCurrentId();
        log.info("修改密码: {},{}", changePasswordDTO, userId);
        userService.changePassword(changePasswordDTO, userId);
        return Result.success();
    }
}
