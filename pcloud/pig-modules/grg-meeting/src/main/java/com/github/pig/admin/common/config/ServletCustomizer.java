package com.github.pig.admin.common.config;

import org.apache.catalina.connector.Connector;
import org.apache.coyote.http11.Http11NioProtocol;
import org.apache.tomcat.util.net.SSLHostConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.embedded.EmbeddedServletContainerFactory;
import org.springframework.boot.context.embedded.tomcat.TomcatEmbeddedServletContainerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
//public class ServletCustomizer implements EmbeddedServletContainerCustomizer {
public class ServletCustomizer  {
    @Value("${https.port}")
    private Integer port;

    @Value("${https.ssl.key-store-password}")
    private String key_store_password;

//    @Value("${https.ssl.key-password}")
//    private String key_password;

    @Bean
    public EmbeddedServletContainerFactory servletContainer() {
        TomcatEmbeddedServletContainerFactory tomcat = new TomcatEmbeddedServletContainerFactory();

        // 添加http
        tomcat.addAdditionalTomcatConnectors(createSslConnector());

        return tomcat;
    }

    // 配置https
    private Connector createSslConnector() {
        Connector connector = new Connector("org.apache.coyote.http11.Http11NioProtocol");
        Http11NioProtocol protocol = (Http11NioProtocol) connector.getProtocolHandler();

        connector.setScheme("https");
        connector.setSecure(true);
        connector.setPort(port);

        // import jks
        SSLHostConfig hostconfig = new SSLHostConfig();
        hostconfig.setCertificateKeystoreFile("classpath:openvidu-selfsigned.jks");
        hostconfig.setCertificateKeystorePassword("openvidu");
        hostconfig.setCertificateKeystoreType("JKS");
        hostconfig.setCertificateKeyAlias("openvidu-selfsigned");

        connector.addSslHostConfig(hostconfig);

        protocol.setSSLEnabled(true);
        protocol.setKeystorePass(key_store_password);

        return connector;
    }
}
