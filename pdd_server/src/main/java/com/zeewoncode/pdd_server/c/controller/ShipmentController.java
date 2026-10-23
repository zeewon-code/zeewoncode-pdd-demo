package com.zeewoncode.pdd_server.c.controller;

import com.zeewoncode.pdd_server.c.service.ShipmentService;
import com.zeewoncode.result.PageResult;
import com.zeewoncode.result.Result;
import com.zeewoncode.vo.ShipmentVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/shipments")
@Slf4j
public class ShipmentController {

    @Autowired
    private ShipmentService shipmentService;


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

}
