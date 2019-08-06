package com.grgbanking.cmeeting.gateway.balance;

import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.cloud.client.loadbalancer.LoadBalancerRetryProperties;
import org.springframework.cloud.gateway.filter.LoadBalancerClientFilter;
import org.springframework.cloud.gateway.support.ServerWebExchangeUtils;
import org.springframework.cloud.netflix.ribbon.RibbonLoadBalancerClient;
import org.springframework.web.server.ServerWebExchange;

import java.net.URI;

/**
 * @author hsjiang
 * @version 1.0
 * @Description: TODO
 * @date 2019/7/29/029
 **/
public class UserLoadBalancerClientFilter extends LoadBalancerClientFilter {

    public UserLoadBalancerClientFilter(LoadBalancerClient loadBalancer, LoadBalancerRetryProperties properties) {
        super(loadBalancer);
    }

    @Override
    protected ServiceInstance choose(ServerWebExchange exchange) {
        //这里可以拿到web请求的上下文，可以从header中取出来自己定义的数据。
        String userId = exchange.getRequest().getHeaders().getFirst("userId");
        String ip = exchange.getRequest().getRemoteAddress().getHostString();
        ip =  ip.equals("0:0:0:0:0:0:0:1") ? "127.0.0.1" : ip;
        if (ip == null) {
            return super.choose(exchange);
        }
        if (this.loadBalancer instanceof RibbonLoadBalancerClient) {
            RibbonLoadBalancerClient client = (RibbonLoadBalancerClient) this.loadBalancer;
            String serviceId = ((URI) exchange.getAttribute(ServerWebExchangeUtils.GATEWAY_REQUEST_URL_ATTR)).getHost();
            //这里使用userId做为选择服务实例的key
            return client.choose(serviceId, ip);
        }
        return super.choose(exchange);
    }

}
