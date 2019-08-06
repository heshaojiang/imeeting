package com.grgbanking.cmeeting.gateway.filter;

import com.grgbanking.cmeeting.gateway.GrgGatewayApplication;
import com.grgbanking.cmeeting.gateway.common.cache.CacheFunctions;
import com.grgbanking.cmeeting.gateway.config.SpringContextUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.web.util.pattern.PathPattern;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

import static org.springframework.cloud.gateway.support.ServerWebExchangeUtils.*;

/**
 * @author wjqiu
 */
//@Component
public class OpenViduFilter implements GatewayFilter, Ordered {

    private static  final Logger log = LoggerFactory.getLogger(GrgGatewayApplication.class);

    public static final int LOAD_BALANCER_CLIENT_FILTER_ORDER = 10100;

//    @Autowired
    private CacheFunctions cacheFunctions;

    @Override
    public int getOrder() {
        return LOAD_BALANCER_CLIENT_FILTER_ORDER;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
            if (cacheFunctions == null)
                cacheFunctions = SpringContextUtils.getBean(CacheFunctions.class);
        URI url = exchange.getAttribute(GATEWAY_REQUEST_URL_ATTR);
        String schemePrefix = exchange.getAttribute(GATEWAY_SCHEME_PREFIX_ATTR);


        log.info("URL Origin: {}, Prefix: {}", url, schemePrefix);

        //preserve the original url
        addOriginalRequestUrl(exchange, url);

        PathPattern.PathMatchInfo pathInfo = exchange.getAttribute(URI_TEMPLATE_VARIABLES_ATTRIBUTE);
        Map<String,String> mapAttribute = null;
        String sessionid = null;
        if (pathInfo != null) {
            mapAttribute = pathInfo.getUriVariables();
            sessionid = mapAttribute.get("sessionId");
        }


        String openviduUrl = "";

        if (!StringUtils.isEmpty(sessionid)){
            openviduUrl = cacheFunctions.CacheGetSession(sessionid,"https://");
        }

        log.info("URL Cache by SessionId[{}]: {}", sessionid, openviduUrl);
        if (openviduUrl != null){
            URI requestUrl = null;

            try {
                URI uriOpenvidu = new URI(openviduUrl);
                if (uriOpenvidu != null){
//                    String sWsUrl = url.getScheme()+"://"+uriOpenvidu.getHost()+":"+String.valueOf(uriOpenvidu.getPort())+url.getRawPath();
//
//                    requestUrl = new URI(sWsUrl);
                    requestUrl = UriComponentsBuilder.fromUri(url)
                            .host(uriOpenvidu.getHost())
                            .port(uriOpenvidu.getPort())
                            .build(true).toUri();
                }
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
            //exchange.getRequest().mutate().header("Set-Cookie","JSESSIONID=68358DB126FDB050180E77505C2790E8; Path=/; HttpOnly").build();


            log.info("URL New: " + requestUrl);
            exchange.getAttributes().put(GATEWAY_REQUEST_URL_ATTR, requestUrl);
        }
//        MultiValueMap<String, ResponseCookie> mapResp = exchange.getResponse().getCookies();
//        MultiValueMap<String, HttpCookie> mapReq = exchange.getRequest().getCookies();
//        HttpClientResponse clientResponse = exchange.getAttribute(CLIENT_RESPONSE_CONN_ATTR);
//        ServerHttpResponse response = exchange.getResponse();
//        exchange.getResponse().addCookie(ResponseCookie.from("JSESSIONID","68358DB126FDB050180E77505C2790E8").build());
//        response.setComplete();


        return chain.filter(exchange);
    }


//    @Override
//    public GatewayFilter apply(Object config) {
//        return (exchange, chain) -> {
//            if (cacheFunctions == null)
//                cacheFunctions = SpringContextUtils.getBean(CacheFunctions.class);
//            URI url = exchange.getRequest().getURI();
////            URI url = exchange.getAttribute(GATEWAY_REQUEST_URL_ATTR);
////            LinkedHashSet<URI> uris = exchange.getAttribute(GATEWAY_ORIGINAL_REQUEST_URL_ATTR);
//
//            String schemePrefix = exchange.getAttribute(GATEWAY_SCHEME_PREFIX_ATTR);
//
//            // 2) Handle ov:ws websocket api
//            log.info("URL Origin: {}, Scheme: {}, Prefix: {}", url, url.getScheme(), schemePrefix);
//
//
//            //preserve the original url
////            addOriginalRequestUrl(exchange, url);
//
//            PathPattern.PathMatchInfo pathInfo = exchange.getAttribute(URI_TEMPLATE_VARIABLES_ATTRIBUTE);
//            Map<String,String> mapAttribute = null;
//            if (pathInfo != null) {
//                mapAttribute = pathInfo.getUriVariables();
//            }
//
//            String sessionid = null;
//            if (mapAttribute != null) {
//                sessionid = mapAttribute.get("sessionId");
//            }
//            String openviduUrl = "";
//
//            if (!StringUtils.isEmpty(sessionid)){
//                openviduUrl = cacheFunctions.CacheGetSession(sessionid,"https://");
//            }
//
//            log.info("URL Cache by SessionId[{}]: {}", sessionid, openviduUrl);
//            URI newUrl = null;
//            if (openviduUrl != null){
//
//
//                try {
//                    URI uriOpenvidu = new URI(openviduUrl);
//
////                    String sWsUrl = "http://"+uriOpenvidu.getHost()+":"+String.valueOf(uriOpenvidu.getPort())+url.getRawPath();
////                String sWsUrl = url.getScheme()+"://"+uriOpenvidu.getHost()+":4050/openvidu";
////                String sWsUrl = url.getScheme()+"://"+uriOpenvidu.getHost()+":4050/openvidu?"+url.getQuery();
////                String path = url.getRawPath().replaceFirst("/"+sessionid,"");;
////                String sWsUrl = url.getScheme()+"://"+uriOpenvidu.getHost()+":4050"+path;
////                    newUrl = new URI(sWsUrl);
//
//                    newUrl = UriComponentsBuilder.fromUri(url)
//                            .host(uriOpenvidu.getHost())
//                            .port(uriOpenvidu.getPort())
//                            .build(true).toUri();
//
//
//                } catch (URISyntaxException e) {
//                    e.printStackTrace();
//                }
//
//
//                log.info("URL New: " + newUrl);
////                exchange.getAttributes().put(GATEWAY_REQUEST_URL_ATTR, newUrl);
//            } else {
//                newUrl = UriComponentsBuilder.fromUri(url).scheme("ws").build(true).toUri();
//
//            }
//
//
//
//            ServerHttpRequest newRequest = exchange.getRequest().mutate().uri(newUrl).build();
//            return chain.filter(exchange.mutate().request(newRequest).build());
//        };
//    }

}
