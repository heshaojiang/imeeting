package com.grgbanking.config;

import com.grgbanking.filter.SsoTokenFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
/**
 * 请求过滤器配置
 * @auther hsjiang
 * @date 2019/6/14 16:49
 */
@Configuration
public class FilterConfig {
    @Autowired
    private SsoTokenFilter tokenFilter;
    @Bean
    public FilterRegistrationBean filterRegistrationBean(){
        FilterRegistrationBean bean = new FilterRegistrationBean();
        bean.setFilter(tokenFilter);
        bean.addUrlPatterns("/*");//对所以请求进行过滤
        return bean;
    }
}
