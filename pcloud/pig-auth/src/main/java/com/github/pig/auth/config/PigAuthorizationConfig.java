package com.github.pig.auth.config;

import com.github.pig.auth.exception.PigWebResponseExceptionTranslator;
import com.github.pig.auth.feign.UserService;
import com.github.pig.auth.util.UserDetailsImpl;
import com.github.pig.common.constant.CommonConstant;
import com.github.pig.common.constant.SecurityConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.client.JdbcClientDetailsService;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.security.oauth2.provider.token.TokenEnhancerChain;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.redis.RedisTokenStore;

import javax.sql.DataSource;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * @date 2017/10/27
 * 认证服务器逻辑实现
 */

@Configuration
@Order(Integer.MIN_VALUE)
@EnableAuthorizationServer
@Slf4j
public class PigAuthorizationConfig extends AuthorizationServerConfigurerAdapter {

    @Autowired
    private DataSource dataSource;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private UserService userService;

    @Autowired
    private RedisConnectionFactory redisConnectionFactory;

    @Autowired
    private PigWebResponseExceptionTranslator pigWebResponseExceptionTranslator;

    /**
     * @param
     * @author fmsheng
     * @description 对应于配置AuthorizationServer安全认证的相关信息
     * @date 2018/10/19 15:28
     */
    @Override
    public void configure(AuthorizationServerSecurityConfigurer security) throws Exception {
        //权限配置
        security
                .allowFormAuthenticationForClients()
                .tokenKeyAccess("isAuthenticated()")
                //配置访问/oauth/check_token无需client验证
                .checkTokenAccess("permitAll()");
    }

    /**
     * @param
     * @author fmsheng
     * @description 配置OAuth2的客户端相关信息
     * @date 2018/10/19 15:28
     */
    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
//        配置客户端认证
//        clients.inMemory()
//                .withClient(authServerConfig.getClientId())
//                .secret(authServerConfig.getClientSecret())
//                .authorizedGrantTypes(SecurityConstants.REFRESH_TOKEN, SecurityConstants.PASSWORD,SecurityConstants.AUTHORIZATION_CODE)
//                .scopes(authServerConfig.getScope())
//                .accessTokenValiditySeconds(60)
//                .refreshTokenValiditySeconds(120);
        JdbcClientDetailsService clientDetailsService = new JdbcClientDetailsService(dataSource);
        clientDetailsService.setSelectClientDetailsSql(SecurityConstants.DEFAULT_SELECT_STATEMENT);
        clientDetailsService.setFindClientDetailsSql(SecurityConstants.DEFAULT_FIND_STATEMENT);
        clients.withClientDetails(clientDetailsService);
    }

    /**
     * @param
     * @author fmsheng
     * @description 配置身份认证器，配置认证方式，TokenStore，TokenGranter，OAuth2RequestFactory
     * @date 2018/10/19 15:29
     */
    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) {
        //token增强配置
        TokenEnhancerChain tokenEnhancerChain = new TokenEnhancerChain();
        tokenEnhancerChain.setTokenEnhancers(
                Arrays.asList(tokenEnhancer(), jwtAccessTokenConverter()));

        //配置token的内存、自定义的tokenServices等信息
        endpoints
                .tokenStore(new RedisTokenStore(redisConnectionFactory))
                .tokenEnhancer(tokenEnhancerChain)
                .authenticationManager(authenticationManager)
                .exceptionTranslator(pigWebResponseExceptionTranslator)
                //该字段设置refresh token是否重复使用,true: reuse;
                // false: no reuse
                .reuseRefreshTokens(false)
                .userDetailsService(userDetailsService);
    }

    /**
     * jwt 生成token 定制化处理
     *
     * @return TokenEnhancer
     */
    @Bean
    public TokenEnhancer tokenEnhancer() {
        return (accessToken, authentication) -> {
            final Map<String, Object> additionalInfo = new HashMap<>(2);
            UserDetailsImpl user = (UserDetailsImpl) authentication.getUserAuthentication().getPrincipal();
            if (user != null) {
                checkRole(user);//检查用户是否有角色
                Integer userId = user.getUserId();
                boolean result = userService.updateLoginTimeByUserId(userId);
                if (result) {
                    log.info("loadUserByUsername(): updateLoginTimeByUserId success");
                } else {
                    log.info("loadUserByUsername(): updateLoginTimeByUserId fail");
                }
                additionalInfo.put("status", "ok");
                additionalInfo.put("msg", CommonConstant.SUCCESS);
                additionalInfo.put("userId", userId);
                String firstRoleCode = user.getRoleList().size()>0?user.getRoleList().get(0).getRoleCode():"";
                additionalInfo.put("role", firstRoleCode);

            }
            ((DefaultOAuth2AccessToken) accessToken).setAdditionalInformation(additionalInfo);
            return accessToken;
        };
    }

    /**
     * 检查当前用户是否有角色，因为功能菜单跟角色绑定
     * 如果没有角色不应该让其登录成功
     * @param user oauth认证后返回的用户对象
     * @return
     * @author hsjiang
     * @date 2019/6/3
    **/
    private void checkRole(UserDetailsImpl user){
        if(null == user.getRoleList() || user.getRoleList().size()==0){
            throw new OAuth2Exception("no_role");
        }
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public JwtAccessTokenConverter jwtAccessTokenConverter() {
        JwtAccessTokenConverter jwtAccessTokenConverter = new JwtAccessTokenConverter();
        jwtAccessTokenConverter.setSigningKey(CommonConstant.SIGN_KEY);
        return jwtAccessTokenConverter;
    }
}
