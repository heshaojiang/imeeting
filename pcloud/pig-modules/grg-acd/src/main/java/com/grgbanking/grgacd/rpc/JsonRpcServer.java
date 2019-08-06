package com.grgbanking.grgacd.rpc;

import com.grgbanking.grgacd.rpc.config.HttpHandshakeInterceptor;
import com.grgbanking.grgacd.rpc.handler.JsonRpcServerHandler;
import org.kurento.jsonrpc.internal.server.config.JsonRpcConfiguration;
import org.kurento.jsonrpc.server.JsonRpcConfigurer;
import org.kurento.jsonrpc.server.JsonRpcHandlerRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.event.EventListener;
import org.springframework.web.socket.server.standard.ServletServerContainerFactoryBean;


/**
 * 保留，暂未使用
 * @author yjw
 */
@Configuration
@Import({ JsonRpcConfiguration.class })
public  class JsonRpcServer implements JsonRpcConfigurer {
  private static final Logger log = LoggerFactory.getLogger(JsonRpcServer.class);
  

  @Bean
  @ConditionalOnMissingBean
  public JsonRpcServerHandler rpcHandler() {
    return new JsonRpcServerHandler();
  }

  @Override
    public void registerJsonRpcHandlers(JsonRpcHandlerRegistry registry) {
      registry.addHandler(rpcHandler().withPingWatchdog(true).withInterceptors(new HttpHandshakeInterceptor()).withAllowedOrigins("*"),
              "/server");
  }
  @Bean
  public ServletServerContainerFactoryBean createWebSocketContainer() {
    ServletServerContainerFactoryBean container = new ServletServerContainerFactoryBean();
    return container;
  }

}