package com.github.pig.common.bean.aop;

import com.github.pig.common.constant.SecurityConstants;
import com.github.pig.common.util.R;
import com.github.pig.common.util.RespCode;
import com.github.pig.common.util.UserUtils;
import com.github.pig.common.util.exception.GrgException;
import com.github.pig.common.vo.UserVo;
import org.apache.commons.lang.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;

/**
 * 每一个请求cotroller之后都要经过这里的aop处理，校验登陆者的toke是否过期，是否一致，这样做的目的不仅更加隐蔽了内部方法实现，而且让token值在有效期内得到监控，增加安全性
 * Controller 增强
 */

@Aspect
@Component
public class ControllerAop {

    private static final Logger logger = LoggerFactory.getLogger(ControllerAop.class);
    @Autowired
    private CacheManager cacheManager;

    /**
     * 定义切点，返回类型为R,类名以Controller结尾的所有方法
     */
    @Pointcut("execution(public com.github.pig.common.util.R *..*Controller.*(..))")
    public void pointCutR() {
    }

    /**
     * 拦截器具体实现
     *
     * @param pjp 切点 所有返回对象R
     * @return R  结果包装
     */
    @Around("pointCutR()")
    public Object methodRHandler(ProceedingJoinPoint pjp) {
        return methodHandler(pjp);
    }


    @Pointcut("execution(public com.baomidou.mybatisplus.plugins.Page *(..))")
    public void pointCutPage() {
    }

    /**
     * 拦截器具体实现
     *
     * @param pjp 切点 所有返回对象Page
     * @return R  结果包装
     */
    @Around("pointCutPage()")
    public Object methodPageHandler(ProceedingJoinPoint pjp) {
        return methodHandler(pjp);
    }


    /**
     * @param
     * @author fmsheng
     * @description
     * @date 2018/11/28 11:13
     */
    @Pointcut("execution(public com.github.pig.common.util.RespEntity *(..))")
    public void pointCutRespEntity() {
    }

    /**
     * 拦截器具体实现
     *
     * @param pjp
     * @return RespEntity  结果包装
     */
    @Around("pointCutRespEntity()")
    public Object methodRespEntityHandler(ProceedingJoinPoint pjp) {
        return methodHandler(pjp);
    }

    /**
     * 每个请求都会校验token的一致性
     * @param pjp
     * @return
     */
    private Object methodHandler(ProceedingJoinPoint pjp) {
        long startTime = System.currentTimeMillis();

        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();

        String token = UserUtils.getToken(request);
        UserVo userVo = null;
        String username = null;
        if (StringUtils.isNotEmpty(token)) {
            userVo = cacheManager.getCache(SecurityConstants.TOKEN_USER_DETAIL).get(token, UserVo.class);
            if (userVo == null) {
                username = UserUtils.getUserName(request);
                if (StringUtils.isNotEmpty(username)) {
                    UserUtils.setUser(username);
                }
                String role = UserUtils.getRole(request).get(0);
                if (StringUtils.isNotEmpty(role)) {
                    UserUtils.setUserRole(role);
                }
            } else {
                username = userVo.getUsername();
                UserUtils.setUser(username);
                String role = null;
                try {
                    role = userVo.getRoleList().get(0).getRoleName();
                } catch (Throwable e) {
                    logger.debug("role is emtpy", e);
                }
                if (StringUtils.isNotEmpty(role)) {
                    UserUtils.setUserRole(role);
                }
            }
        }

        logger.debug("Controller AOP get username:{}", username);

        logger.debug("URL : " + request.getRequestURL().toString());
        logger.debug("HTTP_METHOD : " + request.getMethod());
        logger.debug("IP : " + request.getRemoteAddr());
        logger.debug("CLASS_METHOD : " + pjp.getSignature().getDeclaringTypeName() + "." + pjp.getSignature().getName());
        logger.debug("ARGS : " + Arrays.toString(pjp.getArgs()));

        Object result;

        try {
            result = pjp.proceed();
            logger.debug(pjp.getSignature() + "use time:" + (System.currentTimeMillis() - startTime));
        } catch (Throwable e) {
            if (e instanceof GrgException) {
                RespCode respCode = ((GrgException) e).getStatusCode();
                logger.error("AOP GrgException RespCode:{},msg:{}",respCode.getCode(), respCode.getMsg(),e);
                result = new R(respCode);
            } else {
                logger.error("AOP RuntimeException：", e);
                throw new RuntimeException(e);
            }

        } finally {
            if (StringUtils.isNotEmpty(username)) {
                UserUtils.clearAllUserInfo();
            }
        }

        return result;
    }
}
