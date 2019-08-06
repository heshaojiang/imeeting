package com.grgbanking.server.controller;

import com.github.pig.common.util.R;
import com.github.pig.common.util.RespCode;
import com.github.pig.common.util.TokenUtil;
import com.github.pig.common.vo.UserVo;
import com.grgbanking.server.Constants;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.session.InvalidSessionException;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 用户操作控制层
 * @auther: hsjiang
 * @date: 2019/6/13 17:45
 */
@RestController
public class LoginController {
    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);
    /**
     * 存放shiro登录的subject，以token为key，subject为value
     */
    private final static Map<String, Subject> subjectMap = new ConcurrentHashMap<>();

    /**
     * 获取token，相当于登录操作
     * @param clientId 单点登录客户端id，暂时没有用到
     * @param username 用户名称
     * @param password 密码
     * @return 返回token
     * @author hsjiang
     * @date 2019/6/13 17:45
     **/
    @RequestMapping("/oauth/token")
    public Object login(String clientId,String username,String password){
        // 从SecurityUtils里边创建一个 subject
        Subject subject = SecurityUtils.getSubject();
        if(subject.isAuthenticated()){//如果已经登录
            if(!subject.getPrincipal().equals(username)){//如果不是同一用户，先注销
                subject.logout();
                subject = SecurityUtils.getSubject();
            }
            else{
                Session session = SecurityUtils.getSubject().getSession();
                //查询用户的权限
                UserVo userVO = (UserVo) session.getAttribute(Constants.SESSION_USER_INFO);
                String token = session.getAttribute("token")+"";
                if(subjectMap.get(token) == null){
                    subjectMap.put(token,subject);
                }
                Map result = initResult(token,userVO,"role_super");
                return result;
            }
        }
        // 在认证提交前准备 token（令牌）
        UsernamePasswordToken token = new UsernamePasswordToken(username, password);
        // 执行认证登陆
        try{
            subject.login(token);
        }catch (IncorrectCredentialsException e){
            return new R<String>(false,RespCode.IME_LOGIN_FAIL);
        }catch (UnknownAccountException e){
            return new R<String>(false,RespCode.IME_LOGIN_FAIL);
        }

        Session session = SecurityUtils.getSubject().getSession();
        //查询用户的权限
        UserVo userVO = (UserVo) session.getAttribute(Constants.SESSION_USER_INFO);
        String access_token = TokenUtil.createToken(userVO);//生成token
        session.setAttribute("token",access_token);
        if(subject.isAuthenticated()){
            subjectMap.put(access_token,subject);
        }
        Map result = initResult(access_token,userVO,"role_super");
        return result;
    }
    /**
     * 初始化返回数据
     * @param
     * @return
     * @author hsjiang
     * @date 2019/6/17/017
     **/
    private Map initResult(String access_token,UserVo userVo,String role){
        Map<String,Object> result = new HashMap<>();
        result.put("access_token",access_token);
        result.put("userId",userVo.getUserId());
        result.put("status","ok");
        result.put("role",role);
        result.put("token_type","bearer");
        result.put("scope","server");
        result.put("refreshToken","11");
        result.put("msg", RespCode.SUCCESS.getMsg());
        return  result;
    }
    /**
     * 注销
     * @param accesstoken token
     * @return
     * @author hsjiang
     * @date 2019/6/13 17:45
     **/
    @PostMapping("/authentication/removeToken")
    public R logout(String accesstoken) {
        Subject subject = subjectMap.remove(accesstoken);
        if(subject != null && subject.isAuthenticated()){
            try {
                SecurityUtils.getSubject().logout();
                subject.logout();
            }catch (InvalidSessionException e) {
                logger.error("session已经过期");
            }
        }
        return new R<>(Boolean.TRUE);
    }
    /**
     * 检查token是否有效
     * @param accesstoken token
     * @return true:token有效，false:token无效
     * @author hsjiang
     * @date 2019/6/13 17:45
     **/
    @PostMapping("/authentication/checkToken")
    public R checkToken(String accesstoken) {
        boolean result = false;
        Subject subject = subjectMap.get(accesstoken);
        if(subject != null && subject.isAuthenticated()){
            Session session = subject.getSession();
            UserVo userVO = null;
            try {
                userVO = (UserVo) session.getAttribute(Constants.SESSION_USER_INFO);
            }catch (InvalidSessionException e){
                logger.error("session已经过期");
                result = false;
                subjectMap.remove(accesstoken);
            }

            if(userVO != null && TokenUtil.verifyToken(accesstoken,userVO)){
                session.touch();//手动刷新session，防止session过期
                result = true;
            }
            else{//如果token已经过期，则需要移除
                subjectMap.remove(accesstoken);
                subject.logout();
            }
        }
        return new R<>(true,result);
    }
}
