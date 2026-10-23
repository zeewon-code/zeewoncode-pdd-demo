package com.zeewoncode.pdd_server.c.service;

import com.zeewoncode.vo.ShipmentVO;

public interface ShipmentService {

    /**
     * 根据id获取物流信息
     * @param id
     * @return
     */
    ShipmentVO getShipment(Long id);

    /**
     * 确认收货
     * @param id
     */
    void receive(Long id);
}
