package com.grgbanking.cmeeting.gateway;

import com.grgbanking.cmeeting.gateway.balance.GameCenterBalanceRule;
import com.grgbanking.cmeeting.gateway.balance.UserLoadBalancerClientFilter;
import com.grgbanking.cmeeting.gateway.filter.OpenViduFilter;
import com.grgbanking.cmeeting.gateway.filter.PasswordDecoderFilter;
import com.netflix.loadbalancer.IRule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.cloud.client.loadbalancer.LoadBalancerRetryProperties;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Controller;

/**
 * @author Spencer Gibb
 */
@Controller
@EnableAsync
@EnableDiscoveryClient
@SpringBootConfiguration
@SpringBootApplication
@EnableCaching
@EnableFeignClients
//@CrossOrigin(origins = "*", maxAge = 3600)
public class GrgGatewayApplication {

	private static final Logger logger = LoggerFactory.getLogger(GrgGatewayApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(GrgGatewayApplication.class, args);
	}

	@Bean
	public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {

		logger.info("================customRouteLocator: {}", builder.toString());

		return builder.routes()
				.route(r -> r.header("Sec-WebSocket-Version","\\d+")
						.and()
						.path("/openvidu/{sessionId}/**")
						.filters(f -> f.setPath("/openvidu").saveSession().filter(new OpenViduFilter()))
//                        .uri("lb:ws://grg-openvidu-server")
						.uri("ov:ws://grg-openvidu-server")

				)
				.route(r -> r.path("/v1/meeting/**")
						.uri("lb://grg-meeting")
				)
				.route(r -> r.path("/ov/**")//.or().path("/oo/**")
						.filters(f -> f.stripPrefix(1))
						.uri("lb://grg-openvidu-server")
				)
				.route(r -> r.path("/admin/**")
						.filters(f -> f.stripPrefix(1))
						.uri("lb://grg-meeting")
				)
				.route(r -> r.path("/acd/**")
						.filters(f -> f.stripPrefix(1))
						.uri("lb://grg-acd")
				)
				.route(r -> r.path("/rpc/**")
						.uri("lb://grg-acd")
				)
				.route(r -> r.path("/auth/**")
						.filters(f -> f.stripPrefix(1).filter(new PasswordDecoderFilter().apply()))
						.uri("lb://pig-auth")
				)
				.build();
	}

	//@Bean
	public UserLoadBalancerClientFilter userLoadBalanceClientFilter(LoadBalancerClient client, LoadBalancerRetryProperties properties) {
		return new UserLoadBalancerClientFilter(client, properties);
	}

	//@Bean
	public IRule feignRule() {
		return new GameCenterBalanceRule();
	}
}
