package com.zeewoncode.pdd_server.c.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.zeewoncode.constant.DeletedConstant;
import com.zeewoncode.constant.StatusConstant;
import com.zeewoncode.constant.UserTypeConstant;
import com.zeewoncode.entity.LoginResult;
import com.zeewoncode.entity.User;
import com.zeewoncode.pdd_server.mapper.UserMapper;
import com.zeewoncode.pdd_server.c.service.UserService;
import com.zeewoncode.req.ChangePasswordReq;
import com.zeewoncode.req.LoginReq;
import com.zeewoncode.req.RegisterReq;
import com.zeewoncode.req.UpdateProfileReq;
import com.zeewoncode.result.Result;
import com.zeewoncode.utils.JwtUtil;
import com.zeewoncode.vo.UserVO;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

import static com.zeewoncode.constant.MessageConstant.*;

/**
 * C端-认证与用户模块
 */
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private JwtUtil jwtUtil;


    /**
     * 用户注册
     * @param registerReq
     * @return
     */
    @Override
    public Result<T> register(RegisterReq registerReq) {
        // 1. 参数校验
        if (registerReq == null) {
            return Result.error(PARAM_ERROR);
        }
        if (registerReq.getCode() == null || !registerReq.getCode().equals("8888")) {
            return Result.error(CODE_ERROR);
        }
        if (registerReq.getPassword() == null || registerReq.getPassword().isBlank()) {
            return Result.error(PASSWORD_ERROR);
        }
        if (registerReq.getNickname().contains(" ")) {
            return Result.error(NICKNAME_NOT_NULL);
        }
        if (registerReq.getPhone().length() != 11) {
            return Result.error(PHONE_ERROR);
        }
        User user = new User();
        BeanUtils.copyProperties(registerReq, user);
        // 2. 判断该用户是否已存在
        User userInDB = userMapper.selectByPhoneAndUser(registerReq.getPhone());
        if (userInDB != null ) {
            return Result.error(USER_ALREADY_EXIST);
        }

        // 3. 注册用户
        user.setPassword(passwordEncoder.encode(registerReq.getPassword()));
        user.setStatus(StatusConstant.ENABLE);
        user.setUserType(UserTypeConstant.USER_TYPE_USER);
        user.setDeletedFlag(DeletedConstant.NOT_DELETED);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        userMapper.insert(user);
        return Result.success();
    }

    /**
     * 用户登录
     * @param loginReq
     * @return
     */
    @Override
    public Result<LoginResult> login(LoginReq loginReq) {
        if (loginReq.getPassword() == null || loginReq.getPassword().isBlank()) {
            return Result.error(PASSWORD_ERROR);
        }
        if (loginReq.getPhone() == null || loginReq.getPhone().isBlank()) {
            return Result.error(PHONE_ERROR);
        }
        User dbUser = userMapper.selectByPhoneAndUser(loginReq.getPhone());
        if (dbUser == null) {
            return Result.error(USER_NOT_EXIST);
        }
        if (dbUser.getDeletedFlag() == DeletedConstant.DELETED) {
            return Result.error(USER_NOT_EXIST);
        }
        if (dbUser.getStatus() == StatusConstant.DISABLE) {
            return Result.error(USER_FROZEN);
        }
        // 校验密码
        boolean ok = passwordEncoder.matches(loginReq.getPassword(), dbUser.getPassword());
        if (!ok) {
            return Result.error(PASSWORD_ERROR);
        }

        String token = jwtUtil.generateToken(dbUser.getId(), dbUser.getUserType());
        return Result.success(LoginResult.builder()
                .token(token)
                .user(dbUser)
                .build());
    }

    /**
     * 获取用户信息
     * @return
     */
    @Override
    public User getById(Integer userId) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            return null;
        }
        return user;
    }

    /**
     * 修改个人信息
     * @param updateProfileReq
     * @param userId
     * @return
     */
    @Override
    public void updateProfile(UpdateProfileReq updateProfileReq, Integer userId) {
        User userDB = userMapper.selectById(userId);
        if (userDB == null) {
            throw new RuntimeException(USER_NOT_EXIST);
        }
        String nickName = updateProfileReq.getNickName().trim();
        if (nickName.isBlank() || nickName.length() > 20) {
            throw new RuntimeException(NICKNAME_ERROR);
        }
        String avatar = updateProfileReq.getAvatar();
        if (avatar != null && !avatar.isBlank()) {
            if (!avatar.startsWith("http://") && !avatar.startsWith("https://")) {
                throw new RuntimeException(AVATAR_ERROR);
            }
        }
        User user = BeanUtil.copyProperties(updateProfileReq, User.class);
        user.setId(userId);
        user.setUpdatedAt(LocalDateTime.now());
        userMapper.updateById(user);

    }

    /**
     * 修改密码
     * @param changePasswordReq
     * @param userId
     */
    @Override
    public void changePassword(ChangePasswordReq changePasswordReq, Integer userId) {
        User userDB = userMapper.selectById(userId);
        if (userDB == null) {
            throw new RuntimeException(USER_NOT_EXIST);
        }
        String oldPassword = changePasswordReq.getOldPassword().trim();
        boolean ok = passwordEncoder.matches(oldPassword, userDB.getPassword());
        if (!ok) {
            throw new RuntimeException(PASSWORD_ERROR);
        }
        String newPassword = changePasswordReq.getNewPassword().trim();
        // 1. 非空校验
        if(newPassword.isBlank()){
            throw new RuntimeException("密码不能为空");
        }
        // 2. 长度校验
        if(newPassword.length() <6 || newPassword.length()>16){
            throw new RuntimeException("密码长度必须6~16位");
        }
        // 3. 禁止包含空格
        if(newPassword.contains(" ")){
            throw new RuntimeException("密码不能包含空格");
        }
        User user = User.builder()
                .password(passwordEncoder.encode(newPassword))
                .id(userId)
                .updatedAt(LocalDateTime.now())
                .build();
        userMapper.updateById(user);
    }
}
