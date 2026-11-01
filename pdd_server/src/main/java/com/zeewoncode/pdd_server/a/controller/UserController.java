package com.zeewoncode.pdd_server.a.controller;


import com.zeewoncode.dto.StatusDTO;
import com.zeewoncode.result.PageResult;
import com.zeewoncode.result.Result;
import com.zeewoncode.vo.UserVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * A端-账号管理
 */
@Slf4j
@RestController
@RequestMapping("/api/a/users")
public class UserController {

    @Autowired
    private com.zeewoncode.pdd_server.a.service.UserService userService;


    /**
     * 用户列表
     * @param userType
     * @param status
     * @param phoneKeyword
     * @param nicknameKeyword
     * @param page
     * @param size
     * @return
     */
    @GetMapping
    public PageResult<UserVO> getUsersList(
            @RequestParam(required = false) Integer userType, // 1买家 2商家 9管理员
            @RequestParam(required = false) Integer status, // 1正常 0冻结
            @RequestParam(required = false) String phoneKeyword, // 手机号模糊搜索
            @RequestParam(required = false) String nicknameKeyword, // 昵称模糊搜索
            @RequestParam(required = false, defaultValue = "1") Integer page,
            @RequestParam(required = false, defaultValue = "10") Integer size
    ) {
        log.info("查询用户列表：userType:{}, status:{}, phoneKeyword:{}, nicknameKeyword:{}, page:{}, size:{}",
                userType, status, phoneKeyword, nicknameKeyword, page, size);
        return userService.getUsersList(userType, status, phoneKeyword, nicknameKeyword, page, size);
    }

    /**
     * 用户详情
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public Result<UserVO> getUserInfo(@PathVariable Long id) {
        log.info("查询用户详情：userId:{}", id);
        return userService.getUserInfo(id);
    }

    /**
     * 冻结 / 解冻用户
     * @param id
     * @param dto
     * @return
     */
    @PutMapping("/{id}")
    public Result updateUserStatus(
            @PathVariable Long id,
            @RequestBody StatusDTO dto
    ) {
        log.info("冻结 / 解冻用户：userId:{}, status:{}", id, dto.getStatus());
        return userService.updateUserStatus(id, dto.getStatus());
    }
}
