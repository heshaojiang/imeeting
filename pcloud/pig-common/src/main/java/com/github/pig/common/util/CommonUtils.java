package com.github.pig.common.util;

import com.github.pig.common.constant.CommonConstant;
import com.github.pig.common.constant.MeetingConstant;
import com.github.pig.common.util.exception.GrgException;
import com.github.pig.common.util.exception.UnloginException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.Map;

/**
 * @author wjqiu
 * @date 2019-06-03
 * @description
 */
public class CommonUtils {
    private static Logger log = LoggerFactory.getLogger(CommonUtils.class);


    /**
     * 获取@RequestMapping方法中的参数，统一异常处理
     */
    public static String getMappingParams(Map<String, Object> params,String key,boolean isRequired) throws GrgException {
        String paramValue = null;
        try {
            if (params != null && params.size() > 0)
                paramValue=params.get(key).toString();
        } catch (Exception e) {
            log.warn("getMappingParams Exception with key:{},msg:{}",key,e.getMessage());
        }
        if (StringUtils.isEmpty(paramValue) && isRequired) {
            log.warn("getMappingParams key:{},paramValue is empty",key);
            throw new GrgException(RespCode.IME_INVALIDPARAMETER);
        }
        return paramValue;
    }
    public static String getMappingParams(Map<String, Object> params,String key) throws GrgException {
        return getMappingParams(params,key,true);
    }
}
