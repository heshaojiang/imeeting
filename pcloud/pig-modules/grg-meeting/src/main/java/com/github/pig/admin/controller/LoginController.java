package com.github.pig.admin.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.github.pig.admin.TokenVerify.JavaWebToken;
import com.github.pig.admin.TokenVerify.TokenBean;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.openvidu.java.client.OpenViduRole;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/v1/user")
public class LoginController {
    private static final org.slf4j.Logger log = LoggerFactory.getLogger(MeetingController.class);

//    public class MyUser {
//
//        String name;
//        String pass;
//        OpenViduRole role;
//
//        public MyUser(String name, String pass, OpenViduRole role) {
//            this.name = name;
//            this.pass = pass;
//            this.role = role;
//        }
//    }
//
//    public static Map<String, MyUser> users = new ConcurrentHashMap<>();
//
//    public LoginController() {
//        users.put("publisher1", new MyUser("publisher1", "pass", OpenViduRole.PUBLISHER));
//        users.put("publisher2", new MyUser("publisher2", "pass", OpenViduRole.PUBLISHER));
//        users.put("subscriber", new MyUser("subscriber", "pass", OpenViduRole.SUBSCRIBER));
//    }

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public ResponseEntity<Object> login(HttpServletRequest request, HttpSession httpSession, HttpServletResponse response, @RequestBody String userPass) throws ParseException {
        String url = request.getHeader("Origin");
        log.debug("Access-Control-Allow-Origin:" + url);
        if (!StringUtils.isEmpty(url)) {
            String val = response.getHeader("Access-Control-Allow-Origin");
            if (StringUtils.isEmpty(val)) {
                response.addHeader("Access-Control-Allow-Origin", url);
                response.addHeader("Access-Control-Allow-Credentials", "true");
            }
        }

        System.out.println("Logging in | {user, pass}=" + userPass + ", sessionId=" + httpSession.getId());
        // Retrieve params from POST body
        JSONObject userPassJson = (JSONObject) new JSONParser().parse(userPass);
        String user = (String) userPassJson.get("user");
        String pass = (String) userPassJson.get("pass");

        JSONObject responseJson = new JSONObject();
        if (login(user, pass)) { // Correct user-pass
            String key = "loggedUser";

            Map<String,Object> m = new HashMap<>();
            TokenBean tokenBean = new TokenBean();
            tokenBean.setUser(user);
            tokenBean.setCreateTime(System.currentTimeMillis()+"");
            tokenBean.setValidTime((System.currentTimeMillis()+1000*60*60*2)+"");
            m.put("token", tokenBean);
            String token = JavaWebToken.createJavaWebToken(m);
            Cookie cookie=new Cookie("token",token);//新建cookie
            cookie.setMaxAge(Integer.MAX_VALUE);           // 设置生命周期为MAX_VALUE
            cookie.setPath("/");
            response.addCookie(cookie);

            // Validate session and return OK
            // Value stored in HttpSession allows us to identify the user in future requests
            System.out.println("setAttribute | {key, user}=" + key + ":" + user);
            httpSession.setAttribute(key, user);
            responseJson.put("login","login succeed");
            responseJson.put("token",token);
            return new ResponseEntity<>(responseJson,HttpStatus.OK);
        } else { // Wrong user-pass
            // Invalidate session and return error
            httpSession.invalidate();
            return new ResponseEntity<>("User/Pass incorrect", HttpStatus.UNAUTHORIZED);
        }
    }

    @RequestMapping(value = "/logout", method = RequestMethod.GET)
    public ResponseEntity<Object> logout(HttpSession session) {
        System.out.println("'" + session.getAttribute("loggedUser") + "' has logged out");
        session.invalidate();
        return new ResponseEntity<>(HttpStatus.OK);
    }

    private boolean login(String user, String pass) {
//        return (users.containsKey(user) && users.get(user).pass.equals(pass));
        return true;
    }

    // 用于测试 SpringBoot 容器是否启动
    // http:localhost:8080/test
    @RequestMapping("/test")
    public String test(){
        return "PING OK";
    }

    // http:localhost:8080/put?key=name&value=liwei
    @RequestMapping("/put")
    public String put(HttpSession session,
                      @RequestParam("key") String key,@RequestParam("value") String value){
        session.setAttribute(key,value);
        return "PUT OK";
    }

    // http:localhost:8080/get?key=name
    @RequestMapping("/get")
    public String get(HttpSession session,
                      @RequestParam("key") String key){
        String value = (String) session.getAttribute(key);

        if(value == null || "".equals(value)){
            return "NO VALUE GET";
        }
        return value;
    }
}

