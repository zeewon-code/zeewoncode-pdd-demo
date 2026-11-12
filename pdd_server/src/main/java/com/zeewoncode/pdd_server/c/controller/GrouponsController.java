package com.zeewoncode.pdd_server.c.controller;

import com.zeewoncode.dto.GrouponCancelDTO;
import com.zeewoncode.dto.GrouponInitiateDTO;
import com.zeewoncode.dto.GrouponJoinDTO;
import com.zeewoncode.pdd_server.security.LoginUser;
import com.zeewoncode.result.PageResult;
import com.zeewoncode.result.Result;
import com.zeewoncode.vo.GrouponInitiateVO;
import com.zeewoncode.vo.GrouponInstanceVO;
import com.zeewoncode.vo.GrouponJoinVO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

/**
 * C端-拼团模块
 */
@RestController
@RequestMapping("/api/groupons")
@Slf4j
public class GrouponsController {

    @Autowired
    private com.zeewoncode.pdd_server.c.service.GrouponsService grouponsService;


    /**
     * 拼团发起（团长下单）
     * @param grouponInitiateDTO
     * @return
     */
    @PostMapping("/initiate")
    public Result<GrouponInitiateVO> initiateGroupon(@RequestBody GrouponInitiateDTO grouponInitiateDTO, @AuthenticationPrincipal LoginUser loginUser) {
        log.info("拼团发起:{}", grouponInitiateDTO);
        GrouponInitiateVO grouponInitiateVO = grouponsService.initiateGroupon(grouponInitiateDTO, loginUser.getUserId());
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
            @RequestBody @Valid GrouponJoinDTO grouponJoinDTO,
            @AuthenticationPrincipal LoginUser loginUser
    ) {
        log.info("拼团加入:{},{}", instanceId,grouponJoinDTO);
        GrouponJoinVO grouponJoinVO = grouponsService.joinGroupon(instanceId, grouponJoinDTO, loginUser.getUserId());
        return Result.success(grouponJoinVO);
    }

    /**
     * 获取我的拼团列表
     * @return
     */
    @GetMapping("/mine")
    public Result<PageResult<GrouponInstanceVO>> getGrouponInstanceList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @AuthenticationPrincipal LoginUser loginUser) {
        log.info("获取我的拼团列表");
        PageResult<GrouponInstanceVO> pageResult = grouponsService.getGrouponInstanceList(page, size, loginUser.getUserId());
        return Result.success(pageResult);
    }

    /**
     * 取消拼团(团长取消拼团)
     * @param dto
     * @return
     */
    @PostMapping("/leader/cancel")
    public Result cancelGrouponByLeader(@RequestBody GrouponCancelDTO dto){
        log.info("团长取消拼团:{}", dto);
        grouponsService.cancelGrouponByLeader(dto);
        return Result.success();
    }

    /**
     * 团员取消拼团
     * @return
     */
    @PostMapping("/cancel")
    public Result cancelGroupon(@RequestBody GrouponCancelDTO dto, @AuthenticationPrincipal LoginUser loginUser) {
        log.info("团员取消拼团：{}", dto);
        grouponsService.cancelGroupon(dto, loginUser.getUserId());
        return Result.success();
    }

    /**
     * 商品进行中的拼团（凑人页）
     * @param id
     * @param page
     * @param size
     * @return
     */
    @GetMapping("/{id}/spu")
    public Result<PageResult<GrouponInstanceVO>> getGrouponInstanceInSpu(
            @PathVariable Long id,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @AuthenticationPrincipal LoginUser loginUser
    ) {
        log.info("商品进行中的拼团:{}", id);
        PageResult<GrouponInstanceVO> pageResult = grouponsService.getGrouponInstanceInSpu(id, page, size, loginUser.getUserId());
        return Result.success(pageResult);
    }

    /**
     * 获取拼团详情
     * @param instanceId
     * @return
     */
    @GetMapping("/{instanceId}")
    public Result<GrouponInstanceVO> getGrouponInstanceDetail(@PathVariable Long instanceId) {
        log.info("获取拼团详情:{}", instanceId);
        GrouponInstanceVO vo = grouponsService.getGrouponInstanceDetail(instanceId);
        return Result.success(vo);
    }

}
