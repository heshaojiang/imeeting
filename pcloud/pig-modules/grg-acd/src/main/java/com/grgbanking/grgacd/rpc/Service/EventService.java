package com.grgbanking.grgacd.rpc.Service;

import com.github.pig.common.constant.CommonConstant;
import com.github.pig.common.util.R;
import com.github.pig.common.util.RespCode;
import com.github.pig.common.util.exception.GrgException;
import com.grgbanking.grgacd.common.Constants;
import com.grgbanking.grgacd.common.HangupReason;
import com.grgbanking.grgacd.service.AgentService;
import com.grgbanking.grgacd.service.CallerService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Base64;


/**
 * @author yjw
 * @date 2019-5-16
 */

@Slf4j
@Service
public class  EventService {

    @Autowired
    private AgentService agentService;

    @Autowired
    private CallerService callerService;
    public String getNameFromToken(String token){
        String  username = null;
        try {
            if(StringUtils.isNotEmpty(token)){
                String keyToken = Base64.getEncoder().encodeToString(CommonConstant.SIGN_KEY.getBytes());
                Claims claims = Jwts.parser().setSigningKey(keyToken).parseClaimsJws(token).getBody();
                if (claims != null) {
                    username = claims.get("user_name").toString();
                }
            }
        } catch (Exception e) {
            log.warn("getNameFromToken Exception!",e);
        }

        return username;
    }
    public R sendLogin(String clientId, String clientType, String clientName,String token,String platformType) {
        log.info("sendLogin clientId.login clientId:{}.clientType:{},clientName:{},token:{},platformType:{}",clientId,clientType,clientName,token,platformType);
        R r = new R(RespCode.SUCCESS);
        if (StringUtils.isNotEmpty(clientType)) {
            try {
                Object resp = null;
                if (Constants.CLIENT_TYPE_AGENT.equals(clientType)) {
                    log.info("sendLogin agentService.login clientId:{}", clientId);
                    resp = agentService.login(clientId,token);
                } else {
                    // Caller Login时不对token做校验
                    log.info("sendLogin callerService.login clientId:{}", clientId);
                    resp = callerService.login(clientName, clientId, platformType);
                }
                r.setData(resp);
            } catch (GrgException e) {
                log.error("login error:" + e.getMessage());
                e.printStackTrace();
                r.setRespCode(e.getStatusCode());
            }
        }
        return r;
    }
    public void sendLogout(String clientId,String clientType) throws GrgException {
        log.info("sendLogout clientId:{},clientType:{}",clientId,clientType);
        if (StringUtils.isNotEmpty(clientId)) {
            if (Constants.CLIENT_TYPE_AGENT.equals(clientType)) {
                agentService.logout(clientId,HangupReason.FROM_AGNET);
            } else {
                callerService.logout(clientId,HangupReason.FROM_CALLER);
            }
        }
    }
    public void sendConnectionClosed(String clientId,String clientType) throws GrgException {
        log.info("sendConnectionClosed clientId:{},clientType:{}",clientId,clientType);
        if (StringUtils.isNotEmpty(clientId)) {
            if (Constants.CLIENT_TYPE_AGENT.equals(clientType)) {
                agentService.logout(clientId,HangupReason.CONNECT_CLOSED);
            } else {
                callerService.logout(clientId,HangupReason.CONNECT_CLOSED);
            }
        }
    }
    public void sendTransportError(String clientId,String clientType) throws GrgException {
        log.info("sendTransportError clientId:{},clientType:{}",clientId,clientType);
        if (StringUtils.isNotEmpty(clientId)) {
            if (Constants.CLIENT_TYPE_AGENT.equals(clientType)) {
                agentService.logout(clientId,HangupReason.TRANSPORT_ERROR);
            } else {
                callerService.logout(clientId,HangupReason.TRANSPORT_ERROR);
            }
        }
    }
}
