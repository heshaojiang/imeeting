package com.grgbanking.cmeeting.gateway.feign.fallback;

import com.github.pig.common.vo.UserVo;
import com.grgbanking.cmeeting.gateway.feign.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * @author lengleng
 * @date 2017/10/31
 * 用户服务的fallback
 */
@Service
public class UserServiceFallbackImpl implements UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserServiceFallbackImpl.class);

    @Override
    public UserVo findUserByUsername(String username) {
        logger.error("调用{}异常:{}", "findUserByUsername", username);
        return null;
    }
}
