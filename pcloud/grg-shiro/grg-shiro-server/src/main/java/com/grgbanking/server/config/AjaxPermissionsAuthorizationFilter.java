package com.grgbanking.server.config;

import com.alibaba.fastjson.JSONObject;
import com.github.pig.common.util.RespCode;
import com.xiaoleilu.hutool.http.HttpStatus;
import org.apache.shiro.web.filter.authc.FormAuthenticationFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;

/**
 * 授权过滤器，登录
 * @auther hsjiang
 * @date 2019/6/14 9:17
 */
public class AjaxPermissionsAuthorizationFilter extends FormAuthenticationFilter {
    /**
     * 覆写登录失败的返回数据方法
     * @param request
     * @param response
     * @return 请求返回数据
     * @exception Exception
     * @author hsjiang
     * @date 2019/6/14
     **/
    @Override
    protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws Exception {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("code", RespCode.IME_UNAUTHORIZED.getCode());
        jsonObject.put("msg", RespCode.IME_UNAUTHORIZED.getMsg());
        PrintWriter out = null;
        HttpServletResponse res = (HttpServletResponse) response;
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
        return false;
    }

    @Bean
    public FilterRegistrationBean registration(AjaxPermissionsAuthorizationFilter filter) {
        FilterRegistrationBean registration = new FilterRegistrationBean(filter);
        registration.setEnabled(false);
        return registration;
    }
}
