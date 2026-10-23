package com.zeewoncode.pdd_server.mapper;

import com.zeewoncode.entity.Shipment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface ShipmentMapper {

    /**
     * 根据订单id查询物流信息
     * @param id
     * @return
     */
    @Select("SELECT * FROM shipment WHERE order_id = #{id}")
    Shipment selectShipmentByOrderId(Integer id);

    /**
     * 根据id查询物流信息
     * @param shipmentId
     * @return
     */
    @Select("SELECT * FROM shipment WHERE id = #{shipmentId}")
    Shipment selectShipmentById(Long shipmentId);
}
