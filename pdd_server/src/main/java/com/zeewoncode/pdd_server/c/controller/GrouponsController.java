package com.zeewoncode.pdd_server.c.controller;

import com.zeewoncode.dto.GrouponInitiateDTO;
import com.zeewoncode.dto.GrouponJoinDTO;
import com.zeewoncode.pdd_server.c.service.GrouponsService;
import com.zeewoncode.result.PageResult;
import com.zeewoncode.result.Result;
import com.zeewoncode.vo.GrouponInitiateVO;
import com.zeewoncode.vo.GrouponInstanceVO;
import com.zeewoncode.vo.GrouponJoinVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

/**
 * C端-拼团模块
 */
@RestController
@RequestMapping("/api/groupons")
@Slf4j
public class GrouponsController {

    @Autowired
    private GrouponsService grouponsService;


    /**
     * 拼团发起（团长下单）
     * @param grouponInitiateDTO
     * @return
     */
    @PostMapping("/initiate")
    public Result<GrouponInitiateVO> initiateGroupon(@RequestBody GrouponInitiateDTO grouponInitiateDTO) {
        log.info("拼团发起:{}", grouponInitiateDTO);
        GrouponInitiateVO grouponInitiateVO = grouponsService.initiateGroupon(grouponInitiateDTO);
        return Result.success(grouponInitiateVO);
    }

    /**
     * 拼团加入（团员下单）,进入他人团
     * @param instanceId
     * @param grouponJoinDTO
     * @return
     */
    @PostMapping("/{instanceId}/join")
    public Result<GrouponJoinVO> joinGroupon(
            @PathVariable @NotNull(message = "拼团实例ID不能为空") Long instanceId,
            @RequestBody @Valid GrouponJoinDTO grouponJoinDTO
    ) {
        log.info("拼团加入:{},{}", instanceId,grouponJoinDTO);
        GrouponJoinVO grouponJoinVO = grouponsService.joinGroupon(instanceId, grouponJoinDTO);
        return Result.success(grouponJoinVO);
    }

    /**
     * 获取我的拼团列表
     * @return
     */
    @GetMapping("/mine")
    public Result<PageResult<GrouponInstanceVO>> getGrouponInstanceList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size ) {
        log.info("获取我的拼团列表");
        PageResult<GrouponInstanceVO> pageResult = grouponsService.getGrouponInstanceList(page, size);
        return Result.success(pageResult);
    }
}
