package com.zeewoncode.pdd_server.task;

import com.zeewoncode.entity.Shipment;
import com.zeewoncode.entity.ShipmentTrack;
import com.zeewoncode.pdd_server.mapper.ShipmentMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class ShipmentTrackTask {

    @Autowired
    private ShipmentMapper shipmentMapper;

    /** 发货 1 分钟后：运输中 */
    @Scheduled(fixedDelay = 30_000)
    @Transactional
    public void markInTransit() {
        LocalDateTime now = LocalDateTime.now();
        for (Shipment shipment : shipmentMapper.selectTracking()) {
            if (shipment.getShippedAt().plusMinutes(1).isAfter(now)) {
                continue;
            }
            addTrack(shipment, 2, "快件运输中，已到达【广州转运中心】", now);
        }
    }

    /** 发货 2 分钟后：派送中 */
    @Scheduled(fixedDelay = 30_000)
    @Transactional
    public void markDelivering() {
        LocalDateTime now = LocalDateTime.now();
        for (Shipment shipment : shipmentMapper.selectTracking()) {
            if (shipment.getShippedAt().plusMinutes(2).isAfter(now)) {
                continue;
            }
            addTrack(shipment, 3, "快件正在派送中，请保持电话畅通", now);
        }
    }

    private void addTrack(Shipment shipment, int stage, String desc, LocalDateTime now) {
        int inserted = shipmentMapper.insertTrackIgnore(ShipmentTrack.builder()
                .shipmentId(shipment.getId())
                .status(stage)
                .description(desc)
                .happenedAt(now)
                .build());

        if (inserted > 0) {
            // 运输中、派送中在业务上都属于「配送中」
            shipmentMapper.updateStatusIfLess(shipment.getId(), 2);
            log.info("物流轨迹新增: shipmentId={}, stage={}", shipment.getId(), stage);
        }
    }
}