package com.github.pig.admin.common.meeting;

import com.github.pig.common.constant.CacheNames;
import org.apache.commons.lang.StringUtils;
import org.kurento.jsonrpc.Session;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author wjqiu
 * <p>
 * <p>
 * 新增Cachename时，需要注意去pig-common->RedisCacheConfig->cacheManager中设置cache有效期。
 */
@Service
public class CacheFunctions {
    private static final org.slf4j.Logger log = LoggerFactory.getLogger(CacheFunctions.class);

    @Autowired
    private RedisTemplate redisTemplate;

    //每次都会执行方法，并将结果存入指定的缓存中
    public String CacheSetSession(String sessionid, String publicUrl) {
//        log.info("CacheSetSession sessionid={}", sessionid);
        String key = CacheNames.OPENVIDU_SESSIONS_ALL;
        Object hashKey = key + ':' + sessionid;
        redisTemplate.opsForHash().put(key, hashKey, publicUrl);
        return publicUrl;
    }

    public String CacheGetSession(String sessionid) {
//        log.info("CacheGetSession sessionid={}", sessionid);
        String key = CacheNames.OPENVIDU_SESSIONS_ALL;
        Object hashKey = key + ':' + sessionid;
        redisTemplate.opsForHash().get(key,hashKey);
        return (String)redisTemplate.opsForHash().get(key,hashKey);
    }

    public void CacheRemoveSession(String sessionid) {
//        log.info("CacheRemoveSession sessionid={}", sessionid);
        String key = CacheNames.OPENVIDU_SESSIONS_ALL;
        Object hashKey = key + ':' + sessionid;
        redisTemplate.opsForHash().delete(key,hashKey);
    }


    @CachePut(value = CacheNames.OPENVIDU_SESSIONS_ONE_SERVER, key = "caches[0].name+':'+#publicUrl")
// 每次都会执行方法，并将结果存入指定的缓存中
    public List<String> CacheSetSessionOneServer(String publicUrl, List<String> sessions) {
        return sessions;
    }

    @Cacheable(value = CacheNames.OPENVIDU_SESSIONS_ONE_SERVER, key = "caches[0].name+':'+#publicUrl")
    public List<String> CacheGetSessionOneServer(String publicUrl, List<String> sessions) {
        return sessions;
    }

    //每次都会执行方法，并将结果存入指定的缓存中
    public MeetingSessionCache SetMeetingSession(String meetingId, MeetingSessionCache meetingSession) {
//        log.info("SetMeetingSession meetingId={}", meetingId);
        String key = CacheNames.MEETING_SESSIONS;
        Object hashKey = key + ':' + meetingId;
        redisTemplate.opsForHash().put(key, hashKey, meetingSession);
        return meetingSession;
    }

    public MeetingSessionCache GetMeetingSession(String meetingId) {
        log.info("GetMeetingSession meetingId={}", meetingId);
        String key = CacheNames.MEETING_SESSIONS;
        Object hashKey = key + ':' + meetingId;
        return (MeetingSessionCache)redisTemplate.opsForHash().get(key, hashKey);
    }


    public void RemoveMeetingSession(String meetingId) {
        log.info("RemoveMeetingSession meetingId={}", meetingId);
        String key = CacheNames.MEETING_SESSIONS;
        Object hashKey = key + ':' + meetingId;
        redisTemplate.opsForHash().delete(key, hashKey);
    }

    //每次都会执行方法，并将结果存入指定的缓存中
    public String SetMeetingidWithSessionid(String sessionid, String meetingId) {
        log.info("SetMeetingidWithSessionid sessionid={}, meetingId={}", sessionid, meetingId);
        String key = CacheNames.OPENVIDU_SESSIONS_MEETING;
        Object hashKey = key + ':' + sessionid;
        redisTemplate.opsForHash().put(key, hashKey, meetingId);
        return meetingId;
    }

    public String GetMeetingidWithSessionid(String sessionid, String meetingId) {
        //只获取数据。不触发写入动作。
        log.info("GetMeetingidWithSessionid sessionid={}", sessionid);
        String key = CacheNames.OPENVIDU_SESSIONS_MEETING;
        Object hashKey = key + ':' + sessionid;
        return (String)redisTemplate.opsForHash().get(key, hashKey);
    }

    @CacheEvict(value = CacheNames.OPENVIDU_SESSIONS_MEETING, key = "caches[0].name+':'+#sessionid")
    public void RemoveMeetingidWithSessionid(String sessionid) {
        log.info("RemoveMeetingidWithSessionid sessionid={}", sessionid);
        String key = CacheNames.OPENVIDU_SESSIONS_MEETING;
        Object hashKey = key + ':' + sessionid;
        redisTemplate.opsForHash().delete(key,hashKey);
    }

    //每次都会执行方法，并将结果存入指定的缓存中
    @CachePut(value = CacheNames.MEETING_GET_WS_URL, key = "caches[0].name+':'+#meetingId")
    public String SetMeetingWebsocketUrl(String meetingId, String sessionid) {
        return meetingId;
    }

    @Cacheable(value = CacheNames.MEETING_GET_WS_URL, key = "caches[0].name+':'+#meetingId", unless = "#result == null")
    public String GetMeetingWebsocketUrl(String meetingId, String sessionid) {
        //只获取数据。不触发写入动作。
        return null;
    }

    @CacheEvict(value = CacheNames.MEETING_GET_WS_URL, key = "caches[0].name+':'+#meetingId")
    public void RemoveMeetingWebsocketUrl(String meetingId) {
        log.info("RemoveMeetingidWithSessionid sessionid={}", meetingId);
    }

    @CacheEvict(value = "user_details", key = "#username")
    public void deleteUserDetails(String username) {
        log.info("deleteUserDetails username={}", username);
    }

    //添加成员ID
    public void setParticipant(String meetingId, String connectionId) {
        if(StringUtils.isNotEmpty(meetingId) && StringUtils.isNotEmpty(connectionId)){
            String key = CacheNames.MEETING_PATTICIPANT + "_" + meetingId + "_" + connectionId;
            redisTemplate.opsForValue().set(key, connectionId);
            redisTemplate.expire(key, 15, TimeUnit.SECONDS);
        }
    }

    //获取参会成员
    public String getParticipant(String meetingId, String connectionId) {
        String key = CacheNames.MEETING_PATTICIPANT + "_" + meetingId + "_" + connectionId;
        return (String) redisTemplate.opsForValue().get(key);
    }

    //删除参会成员
    public void delParticipant(String meetingId, String connectionId) {
        String key = CacheNames.MEETING_PATTICIPANT + "_" + meetingId + "_" + connectionId;
        redisTemplate.delete(key);
    }
}