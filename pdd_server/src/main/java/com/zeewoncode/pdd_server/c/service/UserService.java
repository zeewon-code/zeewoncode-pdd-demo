package com.zeewoncode.pdd_server.c.service;

import com.zeewoncode.vo.LoginVO;
import com.zeewoncode.entity.User;
import com.zeewoncode.dto.ChangePasswordDTO;
import com.zeewoncode.dto.LoginDTO;
import com.zeewoncode.dto.RegisterDTO;
import com.zeewoncode.dto.UpdateProfileDTO;
import com.zeewoncode.result.Result;

/**
 * C端-认证与用户模块
 */
public interface UserService {
    /**
     * 用户注册
     * @param registerDTO
     * @return
     */
    Result register(RegisterDTO registerDTO);

    /**
     * 用户登录
     * @param loginDTO
     * @return
     */
    Result<LoginVO> login(LoginDTO loginDTO);

    /**
     * 获取当前用户信息
     * @param userId
     * @return
     */
    User getById(Long userId);

    /**
     * 修改个人信息
     * @param updateProfileDTO
     * @param userId
     * @return
     */
    void updateProfile(UpdateProfileDTO updateProfileDTO, Long userId);

    /**
     * 修改密码
     * @param changePasswordDTO
     * @param userId
     * @return
     */
    void changePassword(ChangePasswordDTO changePasswordDTO, Long userId);
}
