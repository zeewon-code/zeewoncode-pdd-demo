package com.zeewoncode.pdd_server.c.service;

import com.zeewoncode.vo.ShipmentVO;

public interface ShipmentService {

    /**
     * 根据id获取物流信息
     * @param shipmentId
     * @return
     */
    ShipmentVO getShipment(Long shipmentId);

    /**
     * 确认收货
     * @param shipmentId
     * @param userId
     */
    void receive(Long shipmentId, Long userId);
}
