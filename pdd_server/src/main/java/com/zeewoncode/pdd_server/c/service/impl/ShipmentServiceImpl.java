package com.zeewoncode.pdd_server.c.service.impl;

import com.zeewoncode.entity.Shipment;
import com.zeewoncode.pdd_server.c.service.ShipmentService;
import com.zeewoncode.pdd_server.mapper.ShipmentMapper;
import com.zeewoncode.vo.ShipmentStep;
import com.zeewoncode.vo.ShipmentVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Service
public class ShipmentServiceImpl implements ShipmentService {

    @Autowired
    private ShipmentMapper shipmentMapper;

    /**
     * 根据id获取物流信息
     * @param shipmentId
     * @return
     */
    @Override
    public ShipmentVO getShipment(Long shipmentId) {
        // 调用Mapper层根据id获取物流信息
        Shipment shipment = shipmentMapper.selectShipmentById(shipmentId);
        if (shipment == null) {
            throw new RuntimeException("Shipment not found");
        }
        ShipmentVO shipmentVO = new ShipmentVO();
        BeanUtils.copyProperties(shipment, shipmentVO);
        shipmentVO.setShipmentId(shipment.getId());
        List<ShipmentStep> steps = new ArrayList<>();
        steps.add(new ShipmentStep(LocalDateTime.now().plusHours(3).truncatedTo(ChronoUnit.SECONDS), "已收取快件"));
        steps.add(new ShipmentStep(LocalDateTime.now().plusHours(6).truncatedTo(ChronoUnit.SECONDS), "快件正在运送中"));
        steps.add(new ShipmentStep(LocalDateTime.now().plusDays(2).truncatedTo(ChronoUnit.SECONDS), "快件正在派送中"));
        steps.add(new ShipmentStep(LocalDateTime.now().plusDays(3).plusHours(2).truncatedTo(ChronoUnit.SECONDS), "快件已送达"));
        shipmentVO.setSteps(steps);
        return shipmentVO;
    }
}
