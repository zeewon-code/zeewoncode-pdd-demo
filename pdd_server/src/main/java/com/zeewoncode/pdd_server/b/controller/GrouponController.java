package com.zeewoncode.pdd_server.b.controller;

import com.zeewoncode.dto.GrouponCreateDTO;
import com.zeewoncode.dto.SpuStatusDTO;
import com.zeewoncode.pdd_server.security.LoginUser;
import com.zeewoncode.result.PageResult;
import com.zeewoncode.result.Result;
import com.zeewoncode.vo.GrouponAdminItemVO;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

/**
 * B端-拼团活动
 */
@RestController
@RequestMapping("/api/b/groupons")
@Slf4j
public class GrouponController {

    @Autowired
    private com.zeewoncode.pdd_server.b.service.GrouponService grouponService;

    /**
     * 店家获取店铺拼团列表
     * @param status
     * @param page
     * @param size
     * @return
     */
    @GetMapping
    public Result<PageResult<GrouponAdminItemVO>> getGrouponsList(
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false, defaultValue = "1") Integer page,
            @RequestParam(required = false, defaultValue = "10") Integer size,
            @AuthenticationPrincipal LoginUser loginUser
            ) {
        log.info("status:{}, page:{}, size:{}", status, page, size);
        PageResult<GrouponAdminItemVO> result = grouponService.getGrouponsList(status, page, size, loginUser.getUserId());
        return Result.success(result);
    }

    /**
     * 店家创建拼团活动
     * @param grouponCreateDTO
     * @return
     */
    @PostMapping
    public Result createGroupon(@Valid @RequestBody GrouponCreateDTO grouponCreateDTO, @AuthenticationPrincipal LoginUser loginUser) {
        log.info("店家创建拼团活动：{}", grouponCreateDTO);
        return grouponService.createGroupon(grouponCreateDTO, loginUser.getUserId());
    }

    /**
     * 店铺获取拼团活动详情
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public Result<GrouponAdminItemVO> getGrouponDetail(@PathVariable Long id, @AuthenticationPrincipal LoginUser loginUser) {
        log.info("店铺获取拼团活动详情：{}", id);
        return grouponService.getGrouponDetail(id, loginUser.getUserId());
    }

    /**
     * 启用 / 停用拼团活动
     * @param id
     * @param dto
     * @return
     */
    @PutMapping("/{id}/status")
    public Result updateGrouponDefineStatus(
            @PathVariable Long id,
            @Valid @RequestBody SpuStatusDTO dto,
            @AuthenticationPrincipal LoginUser loginUser
    ) {
        log.info("启用 / 停用拼团活动: {}, {}", id, dto);
        return grouponService.updateGrouponDefineStatus(id, dto, loginUser.getUserId());
    }
}
