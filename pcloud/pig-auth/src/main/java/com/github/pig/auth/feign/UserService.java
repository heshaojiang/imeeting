package com.github.pig.auth.feign;

import com.github.pig.auth.feign.fallback.UserServiceFallbackImpl;
import com.github.pig.common.util.R;
import com.github.pig.common.vo.UserVo;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;

import java.util.Date;

/**
 * @author lengleng
 * @date 2017/10/31
 */
@FeignClient(name = "grg-meeting", fallback = UserServiceFallbackImpl.class)
public interface UserService {
    /**
     * 通过用户名查询用户、角色信息
     *
     * @param username 用户名
     * @return UserVo
     */
    @GetMapping("/user/findUserByUsername/{username}")
    UserVo findUserByUsername(@PathVariable("username") String username);

    /**
     * 通过手机号查询用户、角色信息
     *
     * @param mobile 手机号
     * @return UserVo
     */
    @GetMapping("/user/findUserByMobile/{mobile}")
    UserVo findUserByMobile(@PathVariable("mobile") String mobile);

    /**
     * 根据OpenId查询用户信息
     * @param openId openId
     * @return UserVo
     */
    @GetMapping("/user/findUserByOpenId/{openId}")
    UserVo findUserByOpenId(@PathVariable("openId") String openId);

    /**
     * @author fmsheng
     * @param
     * @description 通过userId更新登录时间
     * @date 2019/1/4 10:44
     */
    @GetMapping("/user/updateLoginTimeByUserId/{userId}")
     boolean updateLoginTimeByUserId(@PathVariable("userId") Integer userId) ;
}
