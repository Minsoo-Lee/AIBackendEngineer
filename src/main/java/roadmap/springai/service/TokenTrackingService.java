package roadmap.springai.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class TokenTrackingService {

    private final StringRedisTemplate redisTemplate;

    private static final String KEY_PREFIX = "api:call:";

    // 호출 횟수 증가
    public void increment(String username) {
        String key = KEY_PREFIX + username;
        Long count = redisTemplate.opsForValue().increment(key);
        log.info("📊 API 호출 횟수 - {}: {}회", username, count);
        log.info("📊 Redis 키: {}", key);

        if (count != null && count == 1) {
            redisTemplate.expire(key, 24, TimeUnit.HOURS);
        }
    }

    // 현재 호출 횟수 조회
    public Long getCount(String username) {
        String key = KEY_PREFIX + username;
        String value = redisTemplate.opsForValue().get(key);
        return value != null ? Long.parseLong(value) : 0L;
    }
}
