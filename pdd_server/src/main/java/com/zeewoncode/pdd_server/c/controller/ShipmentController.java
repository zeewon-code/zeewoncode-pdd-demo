package com.zeewoncode.pdd_server.c.controller;

import com.zeewoncode.pdd_server.c.service.ShipmentService;
import com.zeewoncode.result.Result;
import com.zeewoncode.vo.ShipmentVO;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * C端-物流模块
 */
@RestController
@RequestMapping("/api/shipments")
@Slf4j
public class ShipmentController {

    @Autowired
    private com.zeewoncode.pdd_server.c.service.ShipmentService shipmentService;


    /**
     * 获取物流轨迹
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public Result<ShipmentVO> getShipment(@PathVariable Long id) {
        log.info("获取物流轨迹:{}",id);
        ShipmentVO vo = shipmentService.getShipment(id);
        return Result.success(vo);
    }

    /**
     * 确认收货
     * @param id
     * @return
     */
    @PostMapping("/{id}/receive")
    public Result receive(@PathVariable Long id) {
        log.info("确认收货:{}",id);
        shipmentService.receive(id);
        return Result.success();
    }

}
