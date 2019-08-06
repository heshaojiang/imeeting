package com.github.pig.admin.service.impl;

import com.github.pig.admin.common.meeting.CacheFunctions;
import com.github.pig.admin.common.meeting.MeetingSession;
import com.github.pig.admin.common.meeting.MeetingSessionCache;
import com.github.pig.common.util.exception.GrgException;
import com.github.pig.common.util.RespCode;
import com.github.pig.admin.service.BizMeetingParticipantService;
import com.github.pig.admin.service.IvxOpenviduService;
import io.openvidu.java.client.*;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.ssl.TrustStrategy;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.net.ssl.SSLContext;
import java.net.URI;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

;


@Service
public class IvxOpenviduServiceImpl implements IvxOpenviduService {
    private static final org.slf4j.Logger log = LoggerFactory.getLogger(IvxOpenviduServiceImpl.class);

    @Value("${openvidu.secret}")
    String openvidu_secret;
    
    @Value("${openvidu.servicename}")
    String openvidu_servicename;

    @Value("${openvidu.url}")
    String openvidu_url;
    
    @Value("${gatewayws.websocket.url}")
    String websocket_url;

    @Value("${openvidu.autorecording}")
    boolean openvidu_autorecording;

    @Autowired
    private CacheManager cacheManager;
    @Autowired
    private LoadBalancerClient loadBalancer;
    @Autowired
    private CacheFunctions cacheFunctions;
    @Autowired
    private BizMeetingParticipantService bizMeetingParticipantService;
    
    @Override
    public String GetSessionIdWithUrl(String url, String split) {
        String sessionid = new String();
        int nIdx = url.lastIndexOf(split);
        if (nIdx > 0) {
            sessionid = url.substring(nIdx + 1);
        }
        return sessionid;
    }

    @Override
    public String GetSessionIdWithUrl(String url) {
        String sessionid = GetSessionIdWithUrl(url, "#");
        if (sessionid.length() <= 0) {
            sessionid = GetSessionIdWithUrl(url, "/");
        }
        if (sessionid.length() <= 0) {
            sessionid = url;
        }
        return sessionid;
    }
    
    @Override
    public List<String> GetAllKeysInCache(String CacheName) {
        List<String> keysList = new ArrayList<>();

        Cache cache = cacheManager.getCache(CacheName);

        if (cache != null) {
            Object o = cache.getNativeCache();
            if (o != null) {
                if (o instanceof RedisTemplate) {

                    RedisTemplate redisTemplate = (RedisTemplate) o;
//                    redisTemplate.setDefaultSerializer(new StringRedisSerializer());
                    String sPattern = "*";
                    Set<String> redisKeys = redisTemplate.keys(sPattern);
                    Iterator<String> it = redisKeys.iterator();
                    while (it.hasNext()) {
                        String strKey = it.next();
                        if (strKey.indexOf("~keys") <= 0) {
                            keysList.add(strKey);
                        }

                        log.info("keys1={}", strKey);
                    }
                }
            }

        }
        return keysList;
    }

    @Override
    public MeetingSession getSessionIdToken(String openviduUrl, String sessionId) {

        // get publicUrl from openviduUrl of wss
//        String publicUrl = getPublicUrlFromWssUrl(openviduUrl);
//        log.info("getSessionIdToken publicUrl: {}, openviduUrl: {}", publicUrl, openviduUrl);
        log.info("getSessionIdToken  openviduUrl:{} sessionId:{}",openviduUrl,sessionId);

        OpenVidu openVidu = new OpenVidu(openviduUrl, openvidu_secret);
        MeetingSession meetingSession = new MeetingSession();

//        log.info("GET SESSION [{}] From MediaInstance {}", sessionId, openviduUrl);

        try {
            // Create a new OpenVidu Session
            SessionProperties.Builder builder = new SessionProperties.Builder();
            if (openvidu_autorecording) {
                builder.recordingMode(RecordingMode.ALWAYS);
            }
            Session session = null;
            if (StringUtils.isEmpty(sessionId)) {
                log.info("WILL CREATE SESSION from OpenviduServer");
            } else {
                builder.customSessionId(sessionId);
                log.info("CREATE SESSION with sessionID {}", sessionId);
            }

            SessionProperties properties = builder.build();
            session = openVidu.createSession(properties);

            // Generate a new token with the recently created tokenOptions
            OpenViduRole role = OpenViduRole.PUBLISHER;
            TokenOptions tokenOptions = new TokenOptions.Builder().role(role).build();
            String websocketUrl = session.generateToken(tokenOptions);
            log.info("generateToken websocketUrl: {}", websocketUrl);

            sessionId = session.getSessionId();

//            log.info("CREATE SESSION from OpenviduServer {}", sessionId);

//            log.info("wssurl: {}, sessionId: {}", websocketUrl, sessionId);

            // Prepare the response with the sessionId and the token
//            meetingSession.setSessionId(GetSessionIdWithUrl(sessionId));

            // wss://localhost:4443?sessionId=ch7zcfg7etn0nko4&token=pzuqaoipouopfpbj&role=PUBLISHER
            // get token from url
            String[] urlsplit = websocketUrl.split("&");
            String tokenSplit = urlsplit[1];

            String[] split = tokenSplit.split("=");
            String token = split[1];
            
            // 根据网关地址修改url
            String [] tmpsplit = websocketUrl.split("sessionId");
            String params = tmpsplit[1];
            

            
            log.info("websocketUrl: {}, userToken: {}", websocketUrl, token);
    
            meetingSession.setToken(token);
            meetingSession.setSessionId(sessionId);
            meetingSession.setWebsocketUrl(websocketUrl);
            meetingSession.setOpenviduUrl(openviduUrl);

            // Return the response to the client
            return meetingSession;

        } catch (Exception e) {
            // If error generate an error message and return it to client
            log.error("Create OpenVidu Session Get Exception", e);
            return null;
        }
    }
    private String getOvUrl(String host) {
        return "http://" + host + ":4050/";
    }
    private String getPublicUrlFromWssUrl(String sOpenviduUrl) {
        String[] urlsplit = sOpenviduUrl.split(":");
        String urlstr = urlsplit[1];
        String publicUrl = getOvUrl(urlstr);
        return publicUrl;
    }

    public URI GetServerWithLoadBalance(String servicename) {

        ServiceInstance instance = loadBalancer.choose(servicename);
        if (instance != null) {
            return instance.getUri();
        }
        return null;
    }
    @Override
    public MeetingSession getMeetingSessionBySid(String sessionId, String secret, String sOpenviduUrl) throws GrgException {
        if (StringUtils.isEmpty(sOpenviduUrl)){
            log.debug("get openvidu url with LoadBalance ", openvidu_url,sOpenviduUrl);
            URI uriOpenviduUrl = getAvailableMediaInstance(openvidu_url);
            if (uriOpenviduUrl == null){
                log.error("getAvailableMediaInstance false !!");
                return null;
            }
            //获取自定义或者负载url
            sOpenviduUrl = uriOpenviduUrl.toString();
        }
        log.debug("getMeetingSessionBySid openvidu_url_in_conf {} sOpenviduUrlAvailable {} ", openvidu_url,sOpenviduUrl);


        //访问OpenViduServer请求session与token。
        //如果sessionId非null。则会使用sessionId去申请Token。不会生成新的SessionId
        MeetingSession meetingSession = getSessionIdToken(sOpenviduUrl, sessionId);

        if (meetingSession != null) {
            String strSessionId = meetingSession.getSessionId();

            if (!StringUtils.isEmpty(strSessionId)) {
                //把当前的session保存到缓存中
                log.info("CACHE SESSION SessionId:{} => {}", strSessionId, sOpenviduUrl);

                //key --> openviduSessions_All
                cacheFunctions.CacheSetSession(strSessionId, meetingSession.getWebsocketUrl());

            }
            String websocketUrl = meetingSession.getWebsocketUrl();
            String newWebsocketurl = websocketUrl;

            if (!StringUtils.isEmpty(secret) && openvidu_secret.equals(secret)) {
                newWebsocketurl = websocketUrl + "&secret=" + secret;
            }
            if (!StringUtils.isEmpty(websocket_url)) {
                newWebsocketurl = websocketUrl + "&gw=" + websocket_url;
            }
            log.info("newWebsocketurl: {}", newWebsocketurl);
            meetingSession.setWebsocketUrl(newWebsocketurl);
        } else {
            //cacheFunctions.RemoveMeetingSession(sMeetingID);
            log.warn("getMeetingSessionByMid(): request token failed");
            throw new GrgException(RespCode.OV_GETTOKEN_FAIL);
        }
        return meetingSession;
    }

    @Override
    public MeetingSession getMeetingSessionByMid(String sMeetingID, String secret) throws GrgException {

        log.debug("getMeetingSessionByMid sMeetingID {} secret {}", sMeetingID,secret);
        String sessionId = null;
        String sOpenviduUrl = "";
        String compereId = "";
        // 从缓存中取出缓存数据，若有缓存则为已开始会议，不然则是未开始会议
        MeetingSessionCache meetingSessionCache = cacheFunctions.GetMeetingSession(sMeetingID);
        if (meetingSessionCache != null) {
            // 已存在会议，则以缓存中的openviduUri为主
            //sOpenviduUrl = URI.create(meetingSessionCache.getWebsocketUrl()).toString();
            sOpenviduUrl = URI.create(meetingSessionCache.getOpenviduUrl()).toString();
            sessionId = meetingSessionCache.getSessionId();
            compereId = meetingSessionCache.getCompereId();
            log.info("GET In Cache sOpenviduUrl {} sessionId {}", sOpenviduUrl,sessionId);
        }
        MeetingSession meetingSession = getMeetingSessionBySid(sessionId,secret,sOpenviduUrl);
        String newSessionId = meetingSession.getSessionId();
        if (StringUtils.isNotEmpty(newSessionId)){
            //保存meetingid与sessionid的关系
            //key --> openviduSessions_Meeting
            cacheFunctions.SetMeetingidWithSessionid(newSessionId, sMeetingID);
        }
        //把新的数据写入缓存
        //key --> meetingSessions
        meetingSessionCache = meetingSession;
        if(StringUtils.isNotEmpty(compereId))meetingSessionCache.setCompereId(compereId);
        cacheFunctions.SetMeetingSession(sMeetingID, meetingSessionCache);

        return meetingSession;
    }


    /**
     * 获取服务器所有的sessions的key值
     *
     * @param urlOpenViduServer
     * @return
     * @throws GrgException
     */
    public String checkSessionAndCacheDeal(String urlOpenViduServer, String meetingId, String sessionId) throws GrgException {
        TrustStrategy trustStrategy = new TrustStrategy() {
            public boolean isTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                return true;
            }
        };

        CredentialsProvider provider = new BasicCredentialsProvider();
        UsernamePasswordCredentials credentials = new UsernamePasswordCredentials("OPENVIDUAPP", openvidu_secret);
        provider.setCredentials(AuthScope.ANY, credentials);

        SSLContext sslContext;
        try {
            sslContext = (new SSLContextBuilder()).loadTrustMaterial((KeyStore) null, trustStrategy).build();
        } catch (NoSuchAlgorithmException | KeyStoreException | KeyManagementException var8) {
            log.error("getAllSessions {}", var8);
            throw new RuntimeException(var8);
        }

        HttpClient myHttpClient = HttpClients.custom().setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE).setSSLContext(sslContext).setDefaultCredentialsProvider(provider).build();

        try {
            HttpGet request = new HttpGet(urlOpenViduServer + "api/sessions/" + sessionId);

            HttpResponse response = myHttpClient.execute(request);
            int statusCode = response.getStatusLine().getStatusCode();

            if (statusCode == 200) {
                return sessionId;
            } else if (statusCode == 404) {
                //缓存处理
                cacheDeal(meetingId, sessionId);
                return "";
            } else {
                log.warn("checkSessionAndCacheDeal()  statusCode", statusCode);
                return sessionId;
            }
        } catch (Exception var7) {
            log.warn("checkSessionAndCacheDeal() Exception {}", var7);
            return sessionId;
        }
    }

    /**
     * @param
     * @author fmsheng
     * @description 不在allsessions里面，就把该会议的缓存清空
     * @date 2018/8/30 9:16
     */
    public void cacheDeal(String meetingId, String sessionId) {
        cacheFunctions.RemoveMeetingSession(meetingId);
        cacheFunctions.CacheRemoveSession(sessionId);
        cacheFunctions.RemoveMeetingidWithSessionid(sessionId);
    }

    /**
     * 获取有效的媒体服务实例
     *
     * @param ovUrl
     * @return availableOVUrl
     */
    private URI getAvailableMediaInstance(String ovUrl) {
        URI availableOVUrl = null;

        // 若ovUrl为空，则从LB获取；非空，则以指定的为准
        if (ovUrl.isEmpty()) {
            URI uri = GetServerWithLoadBalance(openvidu_servicename);
            if (uri != null) {
                String host = uri.getHost();
                String ovuri = getOvUrl(host);
                availableOVUrl = URI.create(ovuri);
                log.info("GET AVAILABLE from LoadBalance. Service:{} availableOVUrl:{}  ", openvidu_servicename, availableOVUrl);
            } else {
                log.error("GET AVAILABLE from LoadBalance. Service:{} fail !! ");
            }
        } else {
            availableOVUrl = URI.create(ovUrl);
            log.info("GET AVAILABLE URI from params. availableOVUrl:{}", availableOVUrl);
        }

//        log.info("GET AVAILABLE MediaInstance {}", availableOVUrl);

        return availableOVUrl;
    }
}