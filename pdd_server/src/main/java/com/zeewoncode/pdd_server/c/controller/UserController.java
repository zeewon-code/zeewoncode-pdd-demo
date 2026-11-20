package com.zeewoncode.pdd_server.c.controller;

import com.zeewoncode.pdd_server.c.service.VerificationCodeService;
import com.zeewoncode.pdd_server.security.LoginUser;
import com.zeewoncode.vo.LoginVO;
import com.zeewoncode.dto.ChangePasswordDTO;
import com.zeewoncode.dto.LoginDTO;
import com.zeewoncode.dto.RegisterDTO;
import com.zeewoncode.dto.UpdateProfileDTO;
import com.zeewoncode.result.Result;
import com.zeewoncode.vo.UserVO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
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
    public Result<Void> register(@RequestBody @Valid RegisterDTO registerDTO) {
        log.info("用户注册: {}", registerDTO);
        userService.register(registerDTO);
        return Result.success();
    }

    /**
     * 获取注册验证码
     * @param phone
     * @return
     */
    @PostMapping("auth/code")
    public Result<String> getRegisterCode(
            @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
            @NotBlank(message = "手机号不能为空")
            @RequestParam String phone) {
        log.info("获取注册验证码: {}", phone);
        return Result.success(verificationCodeService.generate(phone));

    }

    /**
     * 用户登录
     * @param loginDTO
     * @return
     */
    @PostMapping("auth/login")
    public Result<LoginVO> login(@RequestBody @Valid LoginDTO loginDTO) {
        log.info("用户登录: {}", loginDTO);
        return Result.success(userService.login(loginDTO));
    }

    /**
     * 获取当前用户信息
     */
    @GetMapping("users/me")
    public Result<UserVO> getUserInfo(@AuthenticationPrincipal LoginUser loginUser) {
        Long userId = loginUser.getUserId();
        log.info("获取当前用户信息: {}", userId);
        return Result.success(userService.getById(userId));
    }

    /**
     * 修改个人信息
     * @param updateProfileDTO
     * @return
     */
    @PutMapping("users/me")
    public Result<Void> updateProfile(@RequestBody @Valid UpdateProfileDTO updateProfileDTO,
                                @AuthenticationPrincipal LoginUser loginUser) {
        Long userId = loginUser.getUserId();
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
    public Result<Void> changePassword(@RequestBody @Valid ChangePasswordDTO changePasswordDTO,
                                 @AuthenticationPrincipal LoginUser loginUser) {
        Long userId = loginUser.getUserId();
        log.info("修改密码: {},{}", changePasswordDTO, userId);
        userService.changePassword(changePasswordDTO, userId);
        return Result.success();
    }
}
