package org.study.learning_mate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class RedisService {

    private final RedisTemplate<String, Object> redisTemplate;

    // 현재 활성화된 Spring Profile (local, dev, prod 등)
    private final String activeEnv;

    // 생성자 주입 (의존성 주입)
    public RedisService(RedisTemplate<String, Object> redisTemplate,
                        @Value("${spring.profiles.active}") String activeEnv) {
        this.redisTemplate = redisTemplate;
        this.activeEnv = activeEnv;
    }

    /**
     * Redis에 데이터를 저장하며 만료 시간을 설정합니다.
     *
     * @param key         Redis 키
     * @param value       저장할 값
     * @param expiredTime 만료 시간 (밀리초)
     */
    public void setData(String key, String value, Long expiredTime) {
        if (expiredTime == null || expiredTime <= 0) {
            log.warn("Invalid expiredTime provided for key: {}. Using default expiration.", key);
            expiredTime = getExpireByEnv(); // 기본 만료 시간 설정
        }
        redisTemplate.opsForValue().set(key, value, expiredTime, TimeUnit.MILLISECONDS);
        log.info("Data set in Redis with key: {}, value: {}, expiration: {} ms", key, value, expiredTime);
    }

    /**
     * Redis에서 데이터를 가져옵니다.
     *
     * @param key Redis 키
     * @return 저장된 값 (없으면 null 반환)
     */
    public String getData(String key) {
        Object value = redisTemplate.opsForValue().get(key);
        if (value == null) {
            log.info("No data found in Redis for key: {}", key);
            return null;
        }
        return value.toString();
    }

    /**
     * Redis에 해당 키가 존재하는지 확인합니다.
     *
     * @param key Redis 키
     * @return 존재 여부
     */
    public boolean isExist(String key) {
        String data = getData(key);
        return data != null && !data.isEmpty();
    }

    /**
     * Redis에서 데이터를 삭제합니다.
     *
     * @param key Redis 키
     */
    public void deleteData(String key) {
        boolean deleted = Boolean.TRUE.equals(redisTemplate.delete(key));
        if (deleted) {
            log.info("Data deleted in Redis for key: {}", key);
        } else {
            log.warn("No data found to delete in Redis for key: {}", key);
        }
    }

    /**
     * 현재 활성화된 환경에 따라 기본 만료 시간을 반환합니다.
     *
     * @return 환경별 기본 만료 시간 (밀리초)
     */
    public Long getExpireByEnv() {
        if ("prod".equals(activeEnv)) { // 문자열 비교는 equals() 사용
            return 1_800_000L; // 30분 (밀리초)
        }
        return 60_000L; // 1분 (밀리초)
    }
}
