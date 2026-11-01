package com.zeewoncode.pdd_server.a.service;

import com.zeewoncode.result.PageResult;
import com.zeewoncode.result.Result;
import com.zeewoncode.vo.UserVO;

public interface UserService {

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
    PageResult<UserVO> getUsersList(Integer userType, Integer status, String phoneKeyword,
                                    String nicknameKeyword, Integer page, Integer size);

    /**
     * 获取用户信息
     * @param userId
     * @return
     */
    Result<UserVO> getUserInfo(Long userId);
}
