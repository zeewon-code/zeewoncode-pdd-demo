package com.zeewoncode.pdd_server.c.service;

import com.zeewoncode.entity.LoginResult;
import com.zeewoncode.entity.User;
import com.zeewoncode.req.ChangePasswordReq;
import com.zeewoncode.req.LoginReq;
import com.zeewoncode.req.RegisterReq;
import com.zeewoncode.req.UpdateProfileReq;
import com.zeewoncode.result.Result;
import com.zeewoncode.vo.UserVO;
import org.apache.poi.ss.formula.functions.T;

/**
 * C端-认证与用户模块
 */
public interface UserService {
    /**
     * 用户注册
     * @param registerReq
     * @return
     */
    Result<T> register(RegisterReq registerReq);

    /**
     * 用户登录
     * @param loginReq
     * @return
     */
    Result<LoginResult> login(LoginReq loginReq);

    /**
     * 获取当前用户信息
     * @return
     */
    User getById(Integer userId);

    /**
     * 修改个人信息
     * @param updateProfileReq
     * @return
     */
    void updateProfile(UpdateProfileReq updateProfileReq, Integer userId);

    /**
     * 修改密码
     * @param changePasswordReq
     * @param userId
     */
    void changePassword(ChangePasswordReq changePasswordReq, Integer userId);
}
