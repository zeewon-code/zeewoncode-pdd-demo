package com.zeewoncode.pdd_server.mapper;

import com.zeewoncode.entity.Shipment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

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

    /**
     * 根据id和物流状态更新物流状态
     * @param id
     * @param status
     * @return
     */
    @Update("UPDATE shipment SET status = #{status} WHERE id = #{id}")
    int updateShipmentStatus(Long id, Integer status);
}
