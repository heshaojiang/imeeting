package com.github.pig.common.security.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.pig.common.constant.CommonConstant;
import com.github.pig.common.util.R;
import com.github.pig.common.util.RespCode;
import com.xiaoleilu.hutool.http.HttpStatus;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author fmsheng
 * @date 2018/12/13
 * 客户端异常处理
 * 1. 可以根据 AuthenticationException 不同细化异常处理
 */
@Slf4j
@Component
@AllArgsConstructor
public class ResourceAuthExceptionEntryPoint implements AuthenticationEntryPoint {
	private static Logger logger = LoggerFactory.getLogger(ResourceAuthExceptionEntryPoint.class);
	private final ObjectMapper objectMapper;

	@Override
	public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException {
		R<String> responseBody = new R<String>(false);
		if(authException.getCause() == null){
			responseBody.setRespCode(RespCode.IME_UNAUTHORIZED);
			logger.error("no right to access or token invalid!");
		}
		else{
			String exceptionStr = authException.getCause().toString();
			if (StringUtils.contains(exceptionStr, "unauthorized")) {//oauth2.0认证,用户不存在
				responseBody.setRespCode(RespCode.IME_LOGIN_FAIL);
				logger.info(RespCode.IME_LOGIN_FAIL.getMsg(),authException);
			} else if (StringUtils.contains(exceptionStr, "invalid_grant")) {//oauth2.0认证,密码错误
				responseBody.setRespCode(RespCode.IME_LOGIN_FAIL);
				logger.info(RespCode.IME_LOGIN_FAIL.getMsg(), authException);
			} else if (StringUtils.contains(exceptionStr, "invalid_token")) {//oauth2.0认证,token过期
				responseBody.setRespCode(RespCode.IME_INVALID_TOKEN);
				logger.info(RespCode.IME_INVALID_TOKEN.getMsg(), authException);
			} else if (StringUtils.contains(exceptionStr, "no_role")) {//oauth2.0认证,该用户没有角色，设置角色后重新登录
				responseBody.setRespCode(RespCode.IME_NO_ROLE);
				logger.info(RespCode.IME_NO_ROLE.getMsg(), authException);
			}  else {//其他错误
				responseBody.setRespCode(RespCode.IME_UNAUTHORIZED);
				logger.error("no right to access or token invalid!");
			}
		}

		response.setCharacterEncoding(CommonConstant.UTF8);
		response.setContentType(CommonConstant.CONTENT_TYPE);
		response.setStatus(HttpStatus.HTTP_UNAUTHORIZED);
		response.getWriter().append(objectMapper.writeValueAsString(responseBody));
	}
}
