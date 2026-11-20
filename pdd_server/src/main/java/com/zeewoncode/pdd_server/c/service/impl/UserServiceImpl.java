package com.zeewoncode.pdd_server.c.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.zeewoncode.constant.DeletedConstant;
import com.zeewoncode.constant.ErrorCode;
import com.zeewoncode.exception.BaseException;
import com.zeewoncode.constant.StatusConstant;
import com.zeewoncode.constant.UserTypeConstant;
import com.zeewoncode.pdd_server.c.service.VerificationCodeService;
import com.zeewoncode.vo.LoginVO;
import com.zeewoncode.vo.UserVO;
import com.zeewoncode.entity.User;
import com.zeewoncode.pdd_server.mapper.UserMapper;
import com.zeewoncode.pdd_server.c.service.UserService;
import com.zeewoncode.dto.ChangePasswordDTO;
import com.zeewoncode.dto.LoginDTO;
import com.zeewoncode.dto.RegisterDTO;
import com.zeewoncode.dto.UpdateProfileDTO;
import com.zeewoncode.utils.JwtUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

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
    @Autowired
    private VerificationCodeService verificationCodeService;


    /**
     * 用户注册
     * @param registerDTO
     * @return
     */
    @Override
    public void register(RegisterDTO registerDTO) {
        // 2. 判断该用户是否已存在
        User userInDB = userMapper.selectByPhoneAndUser(registerDTO.getPhone());
        if (userInDB != null ) {
            throw new BaseException(ErrorCode.USER_ALREADY_EXIST);
        }
        if (!verificationCodeService.verify(registerDTO.getPhone(), registerDTO.getCode())) {
            throw new BaseException(ErrorCode.CODE_ERROR);
        }
        User user = new User();
        BeanUtils.copyProperties(registerDTO, user);

        // 3. 注册用户
        user.setPassword(passwordEncoder.encode(registerDTO.getPassword()));
        user.setStatus(StatusConstant.ENABLE);
        user.setUserType(UserTypeConstant.USER_TYPE_USER);
        user.setDeletedFlag(DeletedConstant.NOT_DELETED);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        user.setAvatar(registerDTO.getAvatar());
        if (userMapper.insert(user) <= 0) {
            throw new BaseException(ErrorCode.USER_REGISTER_FAILED);
        }
    }

    /**
     * 用户登录
     * @param loginDTO
     * @return
     */
    @Override
    public LoginVO login(LoginDTO loginDTO) {
        User dbUser = userMapper.selectByPhoneAndUser(loginDTO.getPhone());
        if (dbUser == null) {
            throw new BaseException(ErrorCode.USER_NOT_EXIST);
        }
        if (dbUser.getDeletedFlag() == DeletedConstant.DELETED) {
            throw new BaseException(ErrorCode.USER_NOT_EXIST);
        }
        if (dbUser.getStatus() == StatusConstant.DISABLE) {
            throw new BaseException(ErrorCode.USER_FROZEN);
        }
        // 校验密码
        boolean ok = passwordEncoder.matches(loginDTO.getPassword(), dbUser.getPassword());
        if (!ok) {
            throw new BaseException(ErrorCode.PASSWORD_ERROR);
        }

        String token = jwtUtil.generateToken(dbUser.getId(), dbUser.getUserType());
        return LoginVO.builder()
                .token(token)
                .user(dbUser)
                .build();
    }

    /**
     * 获取用户信息
     * @return
     */
    @Override
    public UserVO getById(Long userId) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BaseException(ErrorCode.USER_NOT_EXIST);
        }
        return BeanUtil.copyProperties(user, UserVO.class);
    }

    /**
     * 修改个人信息
     * @param updateProfileDTO
     * @param userId
     * @return
     */
    @Override
    public void updateProfile(UpdateProfileDTO updateProfileDTO, Long userId) {
        User userDB = userMapper.selectById(userId);
        if (userDB == null) {
            throw new BaseException(ErrorCode.USER_NOT_EXIST);
        }
        User user = BeanUtil.copyProperties(updateProfileDTO, User.class);
        user.setId(userId);
        user.setUpdatedAt(LocalDateTime.now());
        if (userMapper.updateById(user) <= 0) {
            throw new BaseException(ErrorCode.USER_UPDATE_FAILED);
        }
    }

    /**
     * 修改密码
     * @param changePasswordDTO
     * @param userId
     */
    @Override
    public void changePassword(ChangePasswordDTO changePasswordDTO, Long userId) {
        User userDB = userMapper.selectById(userId);
        if (userDB == null) {
            throw new BaseException(ErrorCode.USER_NOT_EXIST);
        }
        String oldPassword = changePasswordDTO.getOldPassword();
        boolean ok = passwordEncoder.matches(oldPassword, userDB.getPassword());
        if (!ok) {
            throw new BaseException(ErrorCode.PASSWORD_ERROR);
        }
        String newPassword = changePasswordDTO.getNewPassword();
        User user = User.builder()
                .password(passwordEncoder.encode(newPassword))
                .id(userId)
                .updatedAt(LocalDateTime.now())
                .build();
        if (userMapper.updateById(user) <= 0) {
            throw new BaseException(ErrorCode.USER_PASSWORD_UPDATE_FAILED);
        }
    }
}
