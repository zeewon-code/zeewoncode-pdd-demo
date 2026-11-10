package com.zeewoncode.pdd_server.c.controller;

import cn.hutool.core.bean.BeanUtil;
import com.zeewoncode.constant.MessageConstant;
import com.zeewoncode.pdd_server.c.service.VerificationCodeService;
import com.zeewoncode.pdd_server.security.LoginUser;
import com.zeewoncode.vo.LoginVO;
import com.zeewoncode.entity.User;
import com.zeewoncode.dto.ChangePasswordDTO;
import com.zeewoncode.dto.LoginDTO;
import com.zeewoncode.dto.RegisterDTO;
import com.zeewoncode.dto.UpdateProfileDTO;
import com.zeewoncode.result.Result;
import com.zeewoncode.vo.UserVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
    @Autowired
    private VerificationCodeService verificationCodeService;

    /**
     * 用户注册
     */
    @PostMapping("auth/register")
    public Result register(@RequestBody RegisterDTO registerDTO) {
        log.info("用户注册: {}", registerDTO);
        Result result = userService.register(registerDTO);
        return result;
    }

    /**
     * 获取注册验证码
     * @param phone
     * @return
     */
    @PostMapping("auth/code")
    public Result<String> getRegisterCode(@RequestParam String phone) {
        log.info("获取注册验证码: {}", phone);
        if (phone == null || phone.isEmpty() || phone.length() != 11) {
            return Result.error(MessageConstant.PHONE_ERROR);
        }
        return Result.success(verificationCodeService.generate(phone));

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
    public Result<UserVO> getUserInfo(@AuthenticationPrincipal LoginUser loginUser) {
        Integer userId = loginUser.getUserId();
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
    public Result updateProfile(@RequestBody UpdateProfileDTO updateProfileDTO, @AuthenticationPrincipal LoginUser loginUser) {
        Integer userId = loginUser.getUserId();
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
    public Result changePassword(@RequestBody ChangePasswordDTO changePasswordDTO, @AuthenticationPrincipal LoginUser loginUser) {
        Integer userId = loginUser.getUserId();
        log.info("修改密码: {},{}", changePasswordDTO, userId);
        userService.changePassword(changePasswordDTO, userId);
        return Result.success();
    }
}
