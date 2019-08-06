package com.github.pig.admin.TokenVerify;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.xml.bind.DatatypeConverter;
import java.security.Key;
import java.util.LinkedHashMap;
import java.util.Map;

public class JavaWebToken {
        private static Logger log = LoggerFactory.getLogger(JavaWebToken.class);
        //检验成功
        public static final String VERIFY_SUCCESS = "VERIFY_SUCCESS";
        //校验失败
        public static final String VERIFY_FAILED = "VERIFY_FAILED";
        //时间过期
        public static final String TIME_EXPIRED = "TIME_EXPIRED";

        //该方法使用HS256算法和Secret:bankgl生成signKey
        private static Key getKeyInstance() {
            //We will sign our JavaWebToken with our ApiKey secret
            SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
            byte[] apiKeySecretBytes = DatatypeConverter.parseBase64Binary("bankgl");
            Key signingKey = new SecretKeySpec(apiKeySecretBytes, signatureAlgorithm.getJcaName());
            return signingKey;
        }

        //使用HS256签名算法和生成的signingKey最终的Token,claims中是有效载荷
        public static String createJavaWebToken(Map<String, Object> claims) {
            return Jwts.builder().setClaims(claims).signWith(SignatureAlgorithm.HS256, getKeyInstance()).compact();
        }

        //解析Token，同时也能验证Token，当验证失败返回null
        public static String parserJavaWebToken(HttpServletRequest request) {

            try {
                Map<String, Object> jwtClaims =
                        Jwts.parser().setSigningKey(getKeyInstance()).parseClaimsJws(getToken(request)).getBody();


                if (jwtClaims == null){
                    return VERIFY_FAILED;
                }
                LinkedHashMap<String,String> tokenBean = (LinkedHashMap) jwtClaims.get("token");
                long validTime = Long.valueOf(tokenBean.get("validTime"));
                if(validTime<System.currentTimeMillis()){
                    return TIME_EXPIRED;
                }

                return "";

            } catch (Exception e) {
                log.error("json web token verify failed");
                return VERIFY_FAILED;
            }
        }

        /**
         * 获取token内容
         * @param request
         * @return
         */
        public static String getToken (HttpServletRequest request){
            Cookie[] cookies = request.getCookies();
            for (Cookie c:cookies){
                if (c.getName().equals("token")){
                    return c.getValue();
                }
            }
            return "";
        }
    }
