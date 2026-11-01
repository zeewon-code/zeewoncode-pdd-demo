//package com.zeewoncode.pdd_server.security;
//
//import com.zeewoncode.pdd_server.mapper.UserMapper;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Component;
//
//@Component
//@RequiredArgsConstructor
//public class UserStatusChecker {
//
//    private final UserMapper userMapper;
//    // private final RedisTemplate<String, Object> redisTemplate; // 建议引入缓存
//
//    /**
//     * 判断用户是否被冻结
//     */
//    public boolean isFrozen(Long userId) {
//        // 1. 优先查 Redis 缓存
//        // Boolean frozen = (Boolean) redisTemplate.opsForValue().get("user:frozen:" + userId);
//        // if (frozen != null) return frozen;
//
//        // 2. 缓存未命中，查数据库
//        Integer status = userMapper.selectById(userId.intValue()).getStatus();
//        boolean frozen = (status != null && status == 0); // 假设 0 为冻结
//
//        // 3. 写入缓存并设置过期时间
//        // redisTemplate.opsForValue().set("user:frozen:" + userId, frozen, 10, TimeUnit.MINUTES);
//
//        return frozen;
//    }
//}
