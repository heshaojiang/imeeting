package com.grgbanking.cmeeting.gateway.config;

import com.grgbanking.cmeeting.gateway.GrgGatewayApplication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.embedded.netty.NettyReactiveWebServerFactory;
import org.springframework.boot.web.reactive.server.ReactiveWebServerFactory;
import org.springframework.boot.web.server.WebServer;
import org.springframework.http.server.reactive.HttpHandler;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

/**
 * @author fmsheng
 * @description
 * @date 2018/9/27 8:46
 * @modified by
 */
@Component
public class WebServerConfig  {

    private WebServer http;

    @Autowired
    private HttpHandler httpHandler;

    @Value("${http.port}")
    private Integer port;

    private static final Logger logger = LoggerFactory.getLogger(GrgGatewayApplication.class);

    @PostConstruct
    public void start() {

        ReactiveWebServerFactory factory = new NettyReactiveWebServerFactory(port);
        this.http = factory.getWebServer(this.httpHandler);
        this.http.start();
    }

    @PreDestroy
    public void stop() {
        this.http.stop();
    }
}
