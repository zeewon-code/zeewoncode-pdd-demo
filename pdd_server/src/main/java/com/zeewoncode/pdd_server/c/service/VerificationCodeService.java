package com.zeewoncode.pdd_server.c.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.Duration;
import java.util.List;

@Service
@RequiredArgsConstructor
public class VerificationCodeService {

    private static final String CODE_KEY    = "auth:code:register:";
    private static final String LIMIT_KEY   = "auth:code:limit:";
    private static final String ATTEMPT_KEY = "auth:code:attempt:";

    private static final Duration CODE_TTL  = Duration.ofMinutes(5);
    private static final Duration LIMIT_TTL = Duration.ofSeconds(60);
    private static final int MAX_ATTEMPTS = 5;

//    private final StringRedisTemplate redisTemplate;
//    private final SecureRandom random = new SecureRandom();
//
//    /**
//     * 生成验证码，60 秒内同一手机号不能重复获取
//     */
//    public String generate(String phone) {
//        Boolean first = redisTemplate.opsForValue()
//                .setIfAbsent(LIMIT_KEY + phone, "1", LIMIT_TTL);
//        if (Boolean.FALSE.equals(first)) {
//            throw new RuntimeException("验证码发送过于频繁，请稍后再试");
//        }
//
//        String code = String.format("%06d", random.nextInt(1_000_000));
//        redisTemplate.opsForValue().set(CODE_KEY + phone, code, CODE_TTL);
//        redisTemplate.delete(ATTEMPT_KEY + phone);
//        return code;
//    }
//
//    /**
//     * 校验验证码，成功后立即删除；错误次数超限直接作废
//     */
//    public boolean verify(String phone, String code) {
//        if (phone == null || code == null) {
//            return false;
//        }
//        String codeKey = CODE_KEY + phone;
//        String stored = redisTemplate.opsForValue().get(codeKey);
//        if (stored == null) {
//            return false;
//        }
//        if (stored.equals(code)) {
//            redisTemplate.delete(codeKey);
//            redisTemplate.delete(ATTEMPT_KEY + phone);
//            return true;
//        }
//
//        Long attempts = redisTemplate.opsForValue().increment(ATTEMPT_KEY + phone);
//        if (attempts != null && attempts == 1) {
//            redisTemplate.expire(ATTEMPT_KEY + phone, CODE_TTL);
//        }
//        if (attempts != null && attempts >= MAX_ATTEMPTS) {
//            redisTemplate.delete(codeKey);
//            redisTemplate.delete(ATTEMPT_KEY + phone);
//        }
//        return false;
//    }
    /**
     * 生成验证码 Lua 脚本（原子操作）
     * KEYS[1]: limit_key, KEYS[2]: code_key, KEYS[3]: attempt_key
     * ARGV[1]: code, ARGV[2]: limit_ttl(秒), ARGV[3]: code_ttl(秒)
     * 返回: 1=成功, 0=触发限流
     */
    private static final String GENERATE_LUA = """
            if redis.call('EXISTS', KEYS[1]) == 1 then
                return 0
            end
            redis.call('SET', KEYS[1], '1', 'EX', tonumber(ARGV[2]))
            redis.call('SET', KEYS[2], ARGV[1], 'EX', tonumber(ARGV[3]))
            redis.call('DEL', KEYS[3])
            return 1
            """;

    /**
     * 校验验证码 Lua 脚本（原子操作）
     * KEYS[1]: code_key, KEYS[2]: attempt_key
     * ARGV[1]: input_code, ARGV[2]: max_attempts, ARGV[3]: code_ttl(秒)
     * 返回: 1=成功, 0=失败可重试, -1=验证码不存在, -2=超过最大尝试次数
     */
    private static final String VERIFY_LUA = """
            local stored = redis.call('GET', KEYS[1])
            if not stored then
                return -1
            end
            if stored == ARGV[1] then
                redis.call('DEL', KEYS[1])
                redis.call('DEL', KEYS[2])
                return 1
            end
            local attempts = redis.call('INCR', KEYS[2])
            if attempts == 1 then
                redis.call('EXPIRE', KEYS[2], tonumber(ARGV[3]))
            end
            if attempts >= tonumber(ARGV[2]) then
                redis.call('DEL', KEYS[1])
                redis.call('DEL', KEYS[2])
                return -2
            end
            return 0
            """;

    private final StringRedisTemplate redisTemplate;
    private final SecureRandom random = new SecureRandom();

    // DefaultRedisScript 作为成员变量复用，Spring 自动缓存 SHA1，避免重复传输脚本体
    private final DefaultRedisScript<Long> generateScript = new DefaultRedisScript<>(GENERATE_LUA, Long.class);
    private final DefaultRedisScript<Long> verifyScript   = new DefaultRedisScript<>(VERIFY_LUA, Long.class);

    /**
     * 生成验证码，60 秒内同一手机号不能重复获取
     */
    public String generate(String phone) {
        String code = String.format("%06d", random.nextInt(1_000_000));
        List<String> keys = List.of(
                LIMIT_KEY + phone,
                CODE_KEY + phone,
                ATTEMPT_KEY + phone
        );
        Long result = redisTemplate.execute(
                generateScript,
                keys,
                code,
                String.valueOf(LIMIT_TTL.getSeconds()),
                String.valueOf(CODE_TTL.getSeconds())
        );
        if (result == null || result != 1L) {
            throw new RuntimeException("验证码发送过于频繁，请稍后再试");
        }
        return code;
    }

    /**
     * 校验验证码，成功后立即删除；错误次数超限直接作废
     */
    public boolean verify(String phone, String code) {
        if (phone == null || code == null) {
            return false;
        }
        List<String> keys = List.of(CODE_KEY + phone, ATTEMPT_KEY + phone);
        Long result = redisTemplate.execute(
                verifyScript,
                keys,
                code,
                String.valueOf(MAX_ATTEMPTS),
                String.valueOf(CODE_TTL.getSeconds())
        );
        return Long.valueOf(1L).equals(result);
    }
}