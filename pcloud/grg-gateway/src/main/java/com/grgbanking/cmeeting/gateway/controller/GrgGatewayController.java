package com.grgbanking.cmeeting.gateway.controller;

import com.grgbanking.cmeeting.gateway.common.cache.CacheFunctions;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 会议服务 前端控制器
 * </p>
 *
 * @author xuesen
 * @since 2018-03-26
 */
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
public class GrgGatewayController {
    
    private CacheFunctions cacheFunctions;

    /**
     * 测试接口
     *
     * @return String
     */
    @RequestMapping("/test")
    public String test(@RequestParam String sessionid) {
        if (cacheFunctions == null){
            cacheFunctions = new CacheFunctions();
        }
        return cacheFunctions.CacheGetSession(sessionid,"https://");
    }

}
