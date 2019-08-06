package com.grgbanking.service;

import com.github.pig.common.constant.CommonConstant;
import com.github.pig.common.service.UserTokenApi;
import com.github.pig.common.util.TokenUtil;
import com.github.pig.common.util.exception.UnloginException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

/**
 * token解析实现层，shiro
 * @auther hsjiang
 * @date 2019/6/14 15:27
 */
@Component("userTokenApi")
public class UserTokenApiImpl implements UserTokenApi {
    private static final Logger logger = LoggerFactory.getLogger(UserTokenApiImpl.class);
    @Override
    public String getUserName(HttpServletRequest request) {
        String username = "";
        String authorization = request.getHeader(CommonConstant.REQ_HEADER);
        //请求头授权为空
        if (StringUtils.isEmpty(authorization)) {
            return username;
        }
        //从请求头分割获取token
        String token = StringUtils.substringAfter(authorization, CommonConstant.TOKEN_SPLIT);
        if (StringUtils.isEmpty(token)) {
            return username;
        }
        else{
            Claims claims = TokenUtil.parseJWT(token);
            if(null != claims){
                username = claims.getIssuer();
            }
        }
        return username;
    }

    @Override
    public List<String> getRole(HttpServletRequest request) {
        List<String> result = new ArrayList<>();
        result.add("");
        return result;
    }
}
