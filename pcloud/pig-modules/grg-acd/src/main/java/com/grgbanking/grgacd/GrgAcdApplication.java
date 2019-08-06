package com.grgbanking.grgacd;

import com.grgbanking.grgacd.rpc.config.HttpHandshakeInterceptor;
import com.grgbanking.grgacd.rpc.handler.JsonRpcServerHandler;
import org.kurento.jsonrpc.internal.server.config.JsonRpcConfiguration;
import org.kurento.jsonrpc.server.JsonRpcConfigurer;
import org.kurento.jsonrpc.server.JsonRpcHandlerRegistry;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.socket.server.standard.ServletServerContainerFactoryBean;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@EnableFeignClients
@EnableDiscoveryClient
@SpringBootApplication
@EnableScheduling
@Configuration
@EnableSwagger2
@Import({ JsonRpcConfiguration.class })
@ComponentScan(basePackages = {"com.grgbanking", "com.github.pig","grgfileserver.service"})
public class GrgAcdApplication implements JsonRpcConfigurer {


    public static void main(String[] args) {
        SpringApplication.run(GrgAcdApplication.class, args);
    }

    @Bean
    public JsonRpcServerHandler rpcHandler() {
        return new JsonRpcServerHandler();
    }

    @Override
    public void registerJsonRpcHandlers(JsonRpcHandlerRegistry registry) {
        registry.addHandler(rpcHandler().withPingWatchdog(true).withInterceptors(new HttpHandshakeInterceptor()).withAllowedOrigins("*"),
                "/rpc");
    }
    @Bean
    public ServletServerContainerFactoryBean createWebSocketContainer() {
        ServletServerContainerFactoryBean container = new ServletServerContainerFactoryBean();
        return container;
    }
}
