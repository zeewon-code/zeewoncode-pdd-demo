package com.zeewoncode.pdd_server.b.controller;

import com.zeewoncode.pdd_server.b.service.GrouponService;
import com.zeewoncode.result.PageResult;
import com.zeewoncode.vo.GrouponAdminItemVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * B端-拼团活动
 */
@RestController
@RequestMapping("/api/b/groupons")
@Slf4j
public class GrouponController {

    @Autowired
    private GrouponService grouponService;

    /**
     * 店家获取店铺拼团列表
     * @param status
     * @param page
     * @param size
     * @return
     */
    @GetMapping
    public PageResult<GrouponAdminItemVO> getGrouponsList(
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false, defaultValue = "1") Integer page,
            @RequestParam(required = false, defaultValue = "10") Integer size
    ) {
        log.info("status:{}, page:{}, size:{}", status, page, size);
        return grouponService.getGrouponsList(status, page, size);
    }
}
