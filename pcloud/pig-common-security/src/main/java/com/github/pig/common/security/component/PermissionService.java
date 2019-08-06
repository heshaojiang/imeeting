package com.github.pig.common.security.component;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.util.AntPathMatcher;

import javax.servlet.http.HttpServletRequest;

/**
 * @author lengleng
 * @date 2017/10/28
 */
@Service("permissionService")
public class PermissionService{

    private AntPathMatcher antPathMatcher = new AntPathMatcher();

    @Value("${config.CheckHasPermission:true}")
    private boolean CheckHasPermission;

    private Logger log = LoggerFactory.getLogger(PermissionService.class);

    public boolean hasPermission(HttpServletRequest request, Authentication authentication) {
        //ele-admin options 跨域配置，现在处理是通过前端配置代理，不使用这种方式，存在风险
//        if (HttpMethod.OPTIONS.name().equalsIgnoreCase(request.getMethod())) {
//            return true;
//        }
//        Object principal = authentication.getPrincipal();
        String username = authentication.getName();
//        List<SimpleGrantedAuthority> grantedAuthorityList = (List<SimpleGrantedAuthority>) authentication.getAuthorities();

//        String urlRequest = request.getRequestURI();
        boolean hasPermission = false;

        // HAS PERMISSION /v1/meeting/join, role=anonymousUser
        log.debug("PERMISSION {}, principal={}", request.getRequestURI(), authentication.getName());

        // 匿名用户
        boolean isAnonymousUser = username.equals("anonymousUser");

        //匿名用户只允许访问部分接口
        if (isAnonymousUser) {
            log.debug("IS ANONYMOUS USER");
            //通过配置文件配置urls.anon即可过滤可以匿名访问的URL

        }else if(StringUtils.isNotEmpty(username)){
            //不是匿名用户并且用户名不为空时
            hasPermission = true;
        }
//        return hasPermission;

//        if (principal != null) {
//            if (CollectionUtil.isEmpty(grantedAuthorityList)) {
//                return hasPermission;
//            }
//
//            Set<MenuVo> urls = new HashSet<>();
//            for (SimpleGrantedAuthority authority : grantedAuthorityList) {
//                // ROLE_ANONYMOUS
//                log.info("GET AUTHORITY role={}", authority.getAuthority());
//                //通过角色获取菜单
//                urls.addAll(menuService.findMenuByRole(authority.getAuthority()));
//            }
//
//            for (MenuVo menu : urls) {
//                if (StringUtils.isNotEmpty(menu.getUrl()) && antPathMatcher.match(menu.getUrl(), request.getRequestURI())
//                        && request.getMethod().equalsIgnoreCase(menu.getMethod())) {
//                    hasPermission = true;
//                    break;
//                }
//            }
//        }
//
        if (CheckHasPermission == false) {
            //开发阶段临时放开验证。
            return true;
        } else
            return hasPermission;

    }
}
