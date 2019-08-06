package com.github.pig.common.util;

import com.github.pig.common.constant.CommonConstant;
import com.github.pig.common.constant.MeetingConstant;
import com.github.pig.common.util.exception.UnloginException;
import com.github.pig.common.vo.SysRole;
import com.github.pig.common.vo.UserVo;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;

/**
 * @author lengleng
 * @date 2017/11/20
 * 用户相关工具类
 */
public class UserUtils {
    private static Logger logger = LoggerFactory.getLogger(UserUtils.class);
    private static final ThreadLocal<String> THREAD_LOCAL_USER = new ThreadLocal<>();
    private static final ThreadLocal<String> THREAD_LOCAL_ROLE = new ThreadLocal<>();
    private static final String KEY_USER = "user";
    private static final String KEY_USER_ROLE = "role";


    /**
     * 根据用户请求中的token 获取用户名
     *
     * @param request Request
     * @return “”、username
     */
    public static String getUserName(HttpServletRequest request) {
        String username = "";
        String authorization = request.getHeader(CommonConstant.REQ_HEADER);
        //请求头授权为空
        if (StringUtils.isEmpty(authorization)) {
            return username;
        }
        //从请求头分割获取token
        String token = StringUtils.substringAfter(authorization, CommonConstant.TOKEN_SPLIT);
        if (StringUtils.isEmpty(token)) {
            //throw new UnloginException("验证过期，请重新登录！");
            return username;
        }
        String key = Base64.getEncoder().encodeToString(CommonConstant.SIGN_KEY.getBytes());
        try {
            Claims claims = Jwts.parser().setSigningKey(key).parseClaimsJws(token).getBody();
            username = claims.get("user_name").toString();
            } catch (Exception ex) {
            logger.error("用户名解析异常,token:{},key:{}", token, key);
            throw new UnloginException("验证过期，请重新登录！",ex);
        }
        return username;
    }

    /**
     * 通过token 获取用户名
     *
     * @param authorization token
     * @return 用户名
     */
    public static String getUserName(String authorization) {
        String username = "";
        String token = StringUtils.substringAfter(authorization, CommonConstant.TOKEN_SPLIT);
        if (StringUtils.isEmpty(token)) {
            return username;
        }
        String key = Base64.getEncoder().encodeToString(CommonConstant.SIGN_KEY.getBytes());
        try {
            Claims claims = Jwts.parser().setSigningKey(key).parseClaimsJws(token).getBody();
            username = claims.get("user_name").toString();
        } catch (Exception ex) {
            logger.error("用户名解析异常,token:{},key:{}", token, key);
        }
        return username;
    }

    /**
     * 根据请求heard中的token获取用户角色
     *
     * @param httpServletRequest request
     * @return 角色名
     */
    public static List<String> getRole(HttpServletRequest httpServletRequest) {
        List<String> roleNames;
        String token = getToken(httpServletRequest);
        if (StringUtils.isNotEmpty(token)){
            String key = Base64.getEncoder().encodeToString(CommonConstant.SIGN_KEY.getBytes());
            Claims claims = Jwts.parser().setSigningKey(key).parseClaimsJws(token).getBody();
            roleNames = (List<String>) claims.get("authorities");
        } else {
            //为空时也初始化一个空值，避免get(0)时出错
            roleNames = Arrays.asList("");
        }

        return roleNames;
    }

    /**
     * 获取请求中token
     *
     * @param httpServletRequest request
     * @return token
     */
    public static String getToken(HttpServletRequest httpServletRequest) {
        String authorization = httpServletRequest.getHeader(CommonConstant.REQ_HEADER);
        return StringUtils.substringAfter(authorization, CommonConstant.TOKEN_SPLIT);
    }

    /**
     * @author fmsheng
     * @param
     * @description  登录认证,目前分admin、imeeting模块
     * @date 2018/12/3 14:05
     */
    public static String getClientId(HttpServletRequest request) {

        String clientId = "";

        String authorization = request.getHeader(CommonConstant.REQ_HEADER);
        //请求头授权为空
        if (StringUtils.isEmpty(authorization)) {
            throw new UnloginException("未授权");
        }
        //从请求头分割获取token
        String client = StringUtils.substringAfter(authorization, CommonConstant.CLIENT_SPLIT);
        if (StringUtils.isEmpty(client)) {
            throw new UnloginException("未授权");
        }

        try{

            byte[] bt = Base64.getDecoder().decode(client);

            String key=new String(bt);

            clientId=key.split(":")[0];

        } catch (Exception ex) {
            logger.error("登录认证解析异常,clientId:{}", clientId);
        }

        return clientId;
    }

    /**
     * 设置用户信息
     *
     * @param username 用户名
     */
    public static void setUser(String username) {
        THREAD_LOCAL_USER.set(username);

        MDC.put(KEY_USER, username);
    }
    /**
     * 设置用户权限
     *
     * @param role 用户名
     */
    public static void setUserRole(String role) {
        THREAD_LOCAL_ROLE.set(role);

//        MDC.put(KEY_USER, username);
    }
    /**
     * 从threadlocal 获取用户名
     *
     * @return 用户名
     */

    public static String getUser() {
        String username = THREAD_LOCAL_USER.get();
        if (username == null) username = "";
        return username;
    }

    /**
     * 如果没有登录，返回null
     *
     * @return 用户名
     */
    public static String getUserName() {
        return getUser();
    }

    public static String getUserRole() {
        logger.info("getUserRole:{}",THREAD_LOCAL_ROLE.get());
        return THREAD_LOCAL_ROLE.get();
    }
    public static void clearAllUserInfo() {
        THREAD_LOCAL_USER.remove();
        MDC.remove(KEY_USER);
    }
    public static boolean isAdminUser(){
        return MeetingConstant.USER_ROLE_Admin.equalsIgnoreCase(getUserRole());
    }
    public static boolean isAdminUserWithCstmId(Integer customerId){
        return customerId == 0;
    }

    public static UserVo getUserVoFromToken(String token){
        UserVo userVo = new UserVo();
        try {
            if(StringUtils.isNotEmpty(token)){
                String keyToken = Base64.getEncoder().encodeToString(CommonConstant.SIGN_KEY.getBytes());
                Claims claims = Jwts.parser().setSigningKey(keyToken).parseClaimsJws(token).getBody();
                if (claims != null) {
                    String username = claims.get("user_name").toString();
                    userVo.setUsername(username);
                    userVo.setRole(claims.get("role").toString());

                    List<String> roles = (List<String>) claims.get("authorities");
                    logger.info("Auth-Token-User:{}-Roles:{}", username, roles);
                    List<SysRole> sysRoleList = new ArrayList<>();
                    roles.stream().forEach(role -> {
                        SysRole sysRole = new SysRole();
                        sysRole.setRoleName(role);
                        sysRoleList.add(sysRole);
                    });
                    userVo.setRoleList(sysRoleList);
//                    userVo.setUserId(claims.get("userId").toString());
                }
            }
        } catch (Exception e) {
            logger.warn("getUserVoFromToken Exception!",e);
            return null;
        }
        return userVo;
    }
}
