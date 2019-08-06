package com.grgbanking.cmeeting.gateway.filter;

import com.grgbanking.cmeeting.gateway.GrgGatewayApplication;
import com.grgbanking.cmeeting.gateway.common.cache.CacheFunctions;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpCookie;
import org.springframework.http.ResponseCookie;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.util.pattern.PathPattern;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClientResponse;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

import static org.springframework.cloud.gateway.support.ServerWebExchangeUtils.*;

/**
 * @author wjqiu
 */

public class OpenViduExFilter implements GlobalFilter, Ordered {

    private static  final Logger log = LoggerFactory.getLogger(GrgGatewayApplication.class);

    public static final int LOAD_BALANCER_CLIENT_FILTER_ORDER = 10100;

    private final LoadBalancerClient loadBalancer;

    public OpenViduExFilter(LoadBalancerClient loadBalancer) {
        this.loadBalancer = loadBalancer;
    }

    @Autowired
    private CacheFunctions cacheFunctions;

    @Override
    public int getOrder() {
        return LOAD_BALANCER_CLIENT_FILTER_ORDER;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        URI url = exchange.getAttribute(GATEWAY_REQUEST_URL_ATTR);
        String schemePrefix = exchange.getAttribute(GATEWAY_SCHEME_PREFIX_ATTR);

        String joinPath = url.getPath();
        Mono<Void> returnMono = null;
        // 1) Handle lb https REST api
        if (url == null || (!"ov".equals(url.getScheme()) && !"ov".equals(schemePrefix))) {

            //imeeting join网关处理  by add fmsheng
//            if (("/v1/meeting/join").equals(joinPath) && "lb".equals(url.getScheme()) ) {
//                log.info("meeting/join URL Origin: {}, Scheme: {}, Prefix: {}", exchange.getAttribute(GATEWAY_REQUEST_URL_ATTR), url.getScheme(), schemePrefix);
//
//                ServiceInstance instance = loadBalancer.choose(url.getHost());
//                if (instance != null) {
//                    String ip = instance.getHost();
//                    int port = instance.getPort();
//                    String specificPart = url.getPath();
//                    String newUrl = "http://" + ip + ":" + String.valueOf(port) + specificPart;
//
//                    try {
//                        url = new URI(newUrl);
//                    } catch (URISyntaxException e) {
//                        throw new RuntimeException(e);
//                    }
//                }
//                exchange.getAttributes().put(GATEWAY_REQUEST_URL_ATTR, url);
//
//                log.info("meeting/join URL New: " + url);
//            }

            return chain.filter(exchange);
        }

        // 2) Handle ov:ws websocket api
        log.info("URL Origin: {}, Scheme: {}, Prefix: {}", exchange.getAttribute(GATEWAY_REQUEST_URL_ATTR), url.getScheme(), schemePrefix);

        //preserve the original url
        addOriginalRequestUrl(exchange, url);

        PathPattern.PathMatchInfo pathInfo = exchange.getAttribute(URI_TEMPLATE_VARIABLES_ATTRIBUTE);
        Map<String,String> mapAttribute = pathInfo.getUriVariables();

        String sessionid = mapAttribute.get("sessionId");
        String openviduUrl = new String();

        if (sessionid != "" && !StringUtils.isEmpty(sessionid)){
            openviduUrl = cacheFunctions.CacheGetSession(sessionid,"https://");
        }

        log.info("URL Cache by SessionId[{}]: {}", sessionid, openviduUrl);
        if (openviduUrl != null){
            URI requestUrl = null;

            try {
                URI uriOpenvidu = new URI(openviduUrl);
                if (uriOpenvidu != null){
                    String sWsUrl = url.getScheme()+"://"+uriOpenvidu.getHost()+":"+String.valueOf(uriOpenvidu.getPort())+url.getRawPath();
//                String sWsUrl = url.getScheme()+"://"+uriOpenvidu.getHost()+":4050/openvidu";
//                String sWsUrl = url.getScheme()+"://"+uriOpenvidu.getHost()+":4050/openvidu?"+url.getQuery();
//                String path = url.getRawPath().replaceFirst("/"+sessionid,"");;
//                String sWsUrl = url.getScheme()+"://"+uriOpenvidu.getHost()+":4050"+path;
                    requestUrl = new URI(sWsUrl);
                }
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
            //exchange.getRequest().mutate().header("Set-Cookie","JSESSIONID=68358DB126FDB050180E77505C2790E8; Path=/; HttpOnly").build();


            log.info("URL New: " + requestUrl);
            exchange.getAttributes().put(GATEWAY_REQUEST_URL_ATTR, requestUrl);
        }
        MultiValueMap<String, ResponseCookie> mapResp = exchange.getResponse().getCookies();
        MultiValueMap<String, HttpCookie> mapReq = exchange.getRequest().getCookies();
        HttpClientResponse clientResponse = exchange.getAttribute(CLIENT_RESPONSE_CONN_ATTR);
        ServerHttpResponse response = exchange.getResponse();


        exchange.getResponse().addCookie(ResponseCookie.from("JSESSIONID","68358DB126FDB050180E77505C2790E8").build());

        response.setComplete();


        return chain.filter(exchange);
    }

    class DelegatingServiceInstance implements ServiceInstance {
        final ServiceInstance delegate;
        private String overrideScheme;

        DelegatingServiceInstance(ServiceInstance delegate, String overrideScheme) {
            this.delegate = delegate;
            this.overrideScheme = overrideScheme;
        }

        @Override
        public String getServiceId() {
            return delegate.getServiceId();
        }

        @Override
        public String getHost() {
            return delegate.getHost();
        }

        @Override
        public int getPort() {
            return delegate.getPort();
        }

        @Override
        public boolean isSecure() {
            return delegate.isSecure();
        }

        @Override
        public URI getUri() {
            return delegate.getUri();
        }

        @Override
        public Map<String, String> getMetadata() {
            return delegate.getMetadata();
        }

        @Override
        public String getScheme() {
            String scheme = delegate.getScheme();
            if (scheme != null) {
                return scheme;
            }
            return this.overrideScheme;
        }

    }


    public String GetSessionIdWithUrl(String url, String split){
        String sessionid = new String();
        int nIdx = url.lastIndexOf(split);
        if (nIdx > 0){
            sessionid = url.substring(nIdx+1);
        }
        return sessionid;
    }
    public String GetSessionIdWithUrl(String url){
        String sessionid = GetSessionIdWithUrl(url,"#");
        if (sessionid.length() <= 0){
            sessionid = GetSessionIdWithUrl(url,"/");
        }
        return sessionid;
    }
}
