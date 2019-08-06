package com.grgbanking.cmeeting.gateway.common.cache;

import com.github.pig.common.constant.CacheNames;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

/**
 * @author wjqiu
 */
@Service
public class CacheFunctions {

    private static final org.slf4j.Logger log = LoggerFactory.getLogger(CacheFunctions.class);

    @Autowired
    private RedisTemplate redisTemplate;

    public String CacheGetSession(String sessionid, String publicUrl) {
//        log.info("CacheGetSession sessionid={}", sessionid);
        String key = CacheNames.OPENVIDU_SESSIONS_ALL;
        Object hashKey = key + ':' + sessionid;
        redisTemplate.opsForHash().get(key, hashKey);

        return (String) redisTemplate.opsForHash().get(key, hashKey);
    }

}
