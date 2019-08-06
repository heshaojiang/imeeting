package com.grgbanking.server.config;

import com.alibaba.fastjson.JSON;
import com.github.pig.common.vo.UserVo;
import com.grgbanking.server.Constants;
import com.grgbanking.server.feign.UserService;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.PrincipalCollection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * 自定义shiro realm
 * @auther hsjiang
 * @date 2019/6/13
 */
public class UserRealm extends AuthorizingRealm {
    private Logger logger = LoggerFactory.getLogger(UserRealm.class);
    private static final PasswordEncoder ENCODER = new BCryptPasswordEncoder();
    @Autowired
    private UserService userService;

    /**
     * 功能描述: 获取登录用户的权限
     * @param principals
     * @return
     * @auther hsjiang
     * @date 2019/6/13 15:07
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        Session session = SecurityUtils.getSubject().getSession();
        //查询用户的权限
        UserVo userVO = (UserVo) session.getAttribute(Constants.SESSION_USER_INFO);
        logger.info("本用户信息：{}", JSON.toJSONString(userVO));
        //logger.info("本用户权限为:" + userVO.getRoleList());
        //为当前用户设置角色和权限
        SimpleAuthorizationInfo authorizationInfo = new SimpleAuthorizationInfo();
        authorizationInfo.setRoles(null);
        authorizationInfo.addStringPermissions(null);
        return authorizationInfo;
    }

    /**
     * 验证当前登录的Subject
     * LoginController.login()方法中执行Subject.login()时 执行此方法
     * @param authcToken
     * @return
     * @auther hsjiang
     * @date 2019/6/13 15:08
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authcToken) throws AuthenticationException {
        String loginName = (String) authcToken.getPrincipal();
        // 获取用户密码
        UserVo userVO = userService.findUserByUsername(loginName);
        if (userVO == null) {
            //没找到帐号
            throw new UnknownAccountException();
        }
        //交给AuthenticatingRealm使用CredentialsMatcher进行密码匹配，如果觉得人家的不好可以自定义实现
        SimpleAuthenticationInfo authenticationInfo = new SimpleAuthenticationInfo(
                userVO.getUsername(),
                userVO.getPassword(),
                getName()
        );
        //session中不需要保存密码
        userVO.setPassword("");
        //将用户信息放入session中
        SecurityUtils.getSubject().getSession().setAttribute(Constants.SESSION_USER_INFO, userVO);
        return authenticationInfo;
    }
    /**
     * 覆写密码校验的方法，因为密码是自定义加密
     * @param token
     * @param info
     * @return
     * @auther hsjiang
     * @date 2019/6/13 16:08
     */
    @Override
    protected void assertCredentialsMatch(AuthenticationToken token, AuthenticationInfo info) throws AuthenticationException {
        UsernamePasswordToken authcToken = (UsernamePasswordToken) token;
        Object accountCredentials = info.getCredentials();
        if (!ENCODER.matches(new String((char[]) authcToken.getCredentials()),accountCredentials+"")) {
            //not successful - throw an exception to indicate this:
            String msg = "Submitted credentials for token [" + token + "] did not match the expected credentials.";
            throw new IncorrectCredentialsException(msg);
        }
    }

    /*class CustomCredentialsMatcher extends SimpleCredentialsMatcher {

        @Override
        public boolean doCredentialsMatch(AuthenticationToken authcToken, AuthenticationInfo info) {
            UsernamePasswordToken token = (UsernamePasswordToken) authcToken;
            Object accountCredentials = getCredentials(info);
            return ENCODER.matches(new String((char[]) authcToken.getCredentials()), accountCredentials + "");
        }
    }*/
}
