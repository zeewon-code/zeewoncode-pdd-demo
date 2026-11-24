package com.zeewoncode.pdd_server.task;

import com.zeewoncode.entity.GrouponInstance;
import com.zeewoncode.pdd_server.mapper.GrouponMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class GrouponExpireTask {

    private static final int BATCH_SIZE = 100;

    private final GrouponMapper grouponMapper;
    private final GrouponExpireService grouponExpireService;

    @Scheduled(fixedDelay = 60_000)
    public void expireGroupons() {
        List<GrouponInstance> list =
                grouponMapper.selectExpiredInstances(LocalDateTime.now(), BATCH_SIZE);
        for (GrouponInstance instance : list) {
            try {
                grouponExpireService.expireOne(instance.getId());
            } catch (Exception e) {
                log.error("拼团过期处理失败: instanceId={}", instance.getId(), e);
            }
        }
    }
}
