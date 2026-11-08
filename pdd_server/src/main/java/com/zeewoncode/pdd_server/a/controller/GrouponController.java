package com.zeewoncode.pdd_server.a.controller;

import com.zeewoncode.result.PageResult;
import com.zeewoncode.vo.GrouponAdminItemVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * A端-拼团监管
 */
@RestController
@RequestMapping("/api/a/groupons")
@Slf4j
public class GrouponController {

    @Autowired
    private com.zeewoncode.pdd_server.a.service.GrouponService grouponService;

    /**
     * 拼团活动监控列表
     * @param page
     * @param size
     * @return
     */
    @GetMapping
    public PageResult<GrouponAdminItemVO> listGroupon(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size
    ) {
        log.info("获取拼团活动监控列表");
        return grouponService.listGroupon(page, size);
    }
}
