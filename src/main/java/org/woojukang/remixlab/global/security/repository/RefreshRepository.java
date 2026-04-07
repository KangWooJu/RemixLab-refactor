package org.woojukang.remixlab.global.security.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;
import org.woojukang.remixlab.global.config.exception.BaseExceptionEnum;
import org.woojukang.remixlab.global.config.exception.domain.BaseException;

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

        Object value = redisTemplate
                .opsForValue()
                .get(key);

        if(value == null){
            throw new BaseException(BaseExceptionEnum.REFRESH_TOKEN_NOT_FOUND);
        }

        return value;
    }

    // 삭제
    public void delete(String key){
        redisTemplate
                .delete(key);
    }

    public boolean exists(String key){
        return redisTemplate.hasKey(key);
    }
}
