package com.zeewoncode.pdd_server.mapper;

import com.zeewoncode.entity.Shipment;
import com.zeewoncode.entity.ShipmentTrack;
import org.apache.ibatis.annotations.*;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface ShipmentMapper {

    /**
     * 根据订单id查询物流信息
     * @param orderId
     * @return
     */
    @Select("SELECT * FROM shipment WHERE order_id = #{orderId} LIMIT 1")
    Shipment selectShipmentByOrderId(Long orderId);

    /**
     * 根据id查询物流信息
     * @param shipmentId
     * @return
     */
    @Select("SELECT * FROM shipment WHERE id = #{shipmentId}")
    Shipment selectShipmentById(Long shipmentId);

    /**
     * 根据id和物流状态更新物流状态
     * @param shipmentId
     * @param status
     * @return
     */
    @Update("UPDATE shipment SET status = #{status} WHERE id = #{shipmentId}")
    int updateShipmentStatus(Long shipmentId, Integer status);

    /**
     * 插入物流信息
     * @param shipment
     * @return
     */
    int insert(Shipment shipment);

    /**
     * 根据订单id列表和物流状态更新物流状态
     * @param orderIds
     * @param status
     * @return
     */
    int updateShipmentStatusByOrderIds(List<Long> orderIds, Integer status);

    /**
     * 根据订单id列表查询物流信息
     * @param orderIds
     * @return
     */
    List<Shipment> selectShipmentsByOrderIds(List<Long> orderIds);

    /**
     * 插入物流轨迹信息
     * @param track
     * @return
     */
    @Insert("INSERT INTO shipment_track (shipment_id, status, description, happened_at) " +
            "VALUES (#{shipmentId}, #{status}, #{description}, #{happenedAt})")
    int insertTrack(ShipmentTrack track);

    /**
     * 根据物流id查询物流轨迹信息
      * @param shipmentId
     * @return
     */
    @Select("SELECT * FROM shipment_track WHERE shipment_id = #{shipmentId} " +
            "ORDER BY happened_at DESC, id DESC")
    List<ShipmentTrack> selectTracksByShipmentId(Long shipmentId);

    /**
     * 根据订单id和物流状态更新物流状态
     * @param shipmentId
     * @param status
     * @param receivedAt
     */
    int updateShipmentStatusByOrderId(Long shipmentId, Integer status, LocalDateTime receivedAt);

    /**
     * 根据物流状态和发货时间查询物流信息
     * @return
     */
    @Select("SELECT * FROM shipment WHERE status IN (1, 2) " +
            "AND shipped_at IS NOT NULL")
    List<Shipment> selectTracking();

    /**
     * 插入物流轨迹信息，忽略重复插入
     * @param track
     * @return
     */
    @Insert("INSERT IGNORE INTO shipment_track(shipment_id, status, description, happened_at) " +
            "VALUES (#{shipmentId}, #{status}, #{description}, #{happenedAt})")
    int insertTrackIgnore(ShipmentTrack track);

    /**
     * 根据id和目标状态更新物流状态，如果当前状态小于目标状态
     * @param id
     * @param target
     * @return
     */
    @Update("UPDATE shipment SET status = #{target} WHERE id = #{id} AND status < #{target}")
    int updateStatusIfLess(@Param("id") Long id, @Param("target") Integer target);
}
