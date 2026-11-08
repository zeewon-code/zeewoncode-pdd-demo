package com.zeewoncode.pdd_server.a.controller;

import com.zeewoncode.pdd_server.a.service.DashboardService;
import com.zeewoncode.result.Result;
import com.zeewoncode.vo.ADashboard;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * A端-平台看板
 */
@RestController
@RequestMapping("/api/a/dashboard")
@Slf4j
public class DashboardController {
    @Autowired
    private DashboardService dashboardService;

    /**
     * 获取平台看板数据
     * @return
     */
    @GetMapping
    public Result<ADashboard> getDashboardData() {
        log.info("获取平台看板数据");
        ADashboard data = dashboardService.getDashboardData();
        return Result.success(data);
    }
}
