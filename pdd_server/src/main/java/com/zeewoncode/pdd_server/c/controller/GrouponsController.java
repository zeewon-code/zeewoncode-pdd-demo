package com.zeewoncode.pdd_server.c.controller;

import com.zeewoncode.dto.GrouponInitiateDTO;
import com.zeewoncode.pdd_server.c.service.GrouponsService;
import com.zeewoncode.result.Result;
import com.zeewoncode.vo.GrouponInitiateVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
