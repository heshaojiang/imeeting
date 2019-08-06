package com.grgbanking.filter;

import com.alibaba.fastjson.JSONObject;
import com.github.pig.common.bean.config.FilterUrlsPropertiesConifg;
import com.github.pig.common.util.RespCode;
import com.github.pig.common.util.UserUtils;
import com.grgbanking.utils.TokenCheckUtil;
import com.xiaoleilu.hutool.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * 请求过滤器
 * @auther hsjiang
 * @date 2019/6/14 16:49
 */
@Component
public class SsoTokenFilter extends HttpServlet implements Filter {
    @Autowired
    private FilterUrlsPropertiesConifg filterUrlsPropertiesConifg;
    @Autowired
    private TokenCheckUtil tokenCheckUtil;

    private static Logger logger = LoggerFactory.getLogger(SsoTokenFilter.class);

    private static final AntPathMatcher antPathMatcher = new AntPathMatcher();//url匹配对象

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;


        String servletPath = req.getServletPath();//获取请求的url

        /** 过滤掉不需要登录的url*/
        for (String uriPattern : filterUrlsPropertiesConifg.getAnon()) {
            // 支持ANT表达式
            if (antPathMatcher.match(uriPattern, servletPath)) {//如果匹配成功，直接放行
                chain.doFilter(request, response);
                return;
            }
        }
        // token验证
        if (!checkToken(req)) {//如果验证失败
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("code", RespCode.IME_UNAUTHORIZED.getCode());
            jsonObject.put("msg", RespCode.IME_UNAUTHORIZED.getMsg());
            PrintWriter out = null;
            try {
                res.setCharacterEncoding("UTF-8");
                res.setContentType("application/json");
                res.setStatus(HttpStatus.HTTP_UNAUTHORIZED);
                out = response.getWriter();
                out.println(jsonObject);
            } catch (Exception e) {
            } finally {
                if (null != out) {
                    out.flush();
                    out.close();
                }
            }
            return;
        }
        // 已经登录
        chain.doFilter(request, response);
        return;
    }

    /**
     * 检查token是否有效
     * @param req
     * @return
     * @author hsjiang
     * @date 2019/6/14
     **/
    private boolean checkToken(HttpServletRequest req){
        String accessToken = UserUtils.getToken(req);
        boolean result = true;
        if(StringUtils.isEmpty(accessToken)){
            result = false;
        }
        if(result){
            result = tokenCheckUtil.checkToken(accessToken);
        }
        return result;
    }

}
