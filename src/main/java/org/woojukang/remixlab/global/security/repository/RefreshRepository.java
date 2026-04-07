package org.woojukang.remixlab.global.security.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.concurrent.TimeUnit;

@Repository
@RequiredArgsConstructor
public class RefreshRepository {

    private final RedisTemplate<String,String> redisTemplate;

    // 저장
    public void save(String key,
                     String value,
                     Long expiredMs){

        redisTemplate
                .opsForValue()
                .set(key,
                        value,
                        expiredMs,
                        TimeUnit.MILLISECONDS);

    }

    // 조회
    public Object findByKey(String key){
        return redisTemplate
                .opsForValue()
                .get(key);
    }

    // 삭제
    public void delete(String key){
        redisTemplate
                .delete(key);
    }
}
