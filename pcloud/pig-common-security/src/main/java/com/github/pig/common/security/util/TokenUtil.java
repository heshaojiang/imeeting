package com.github.pig.common.security.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.common.exceptions.InvalidTokenException;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.ResourceServerTokenServices;
import org.springframework.stereotype.Component;

/**
 * @description Token处理工具类
 * @author hsjiang
 * @date 2019-06-06 13:34
 **/
@Component
public class TokenUtil {
    private static Logger log = LoggerFactory.getLogger(TokenUtil.class);
    @Autowired
    private ResourceServerTokenServices tokenServices;
    /**
     * 检查token是否有效
     * @param token
     * @return true：有效，false：无效
     * @author hsjiang
     * @date 2019/6/6
     **/
    public boolean checkToken(String token){
        boolean result = false;
        try{
            OAuth2Authentication auth2Authentication = tokenServices.loadAuthentication(token);
            if(auth2Authentication != null){
                result = true;
            }
        } catch (Exception e){
            log.error("Invalid token,token:{}", token,e);
        }
        return result;
    }
}
