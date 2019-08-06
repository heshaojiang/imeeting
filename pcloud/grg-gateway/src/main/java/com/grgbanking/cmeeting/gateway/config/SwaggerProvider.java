package com.grgbanking.cmeeting.gateway.config;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import springfox.documentation.swagger.web.SwaggerResource;
import springfox.documentation.swagger.web.SwaggerResourcesProvider;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Triston
 */
@Component
@Configuration
public class SwaggerProvider implements SwaggerResourcesProvider {

    public static final String API_URI = "/v2/api-docs";

    @Value("${swagger.show}")
    public boolean swaggerShow;

    @Override
    public List<SwaggerResource> get() {
        List<SwaggerResource> resources = new ArrayList<>();

        if(swaggerShow) {
            resources.add(setResources("grg-meeting", "/admin/v2/api-docs"));
            resources.add(setResources("grg-acd", "/acd/v2/api-docs"));
        }
        return resources;
    }

    private SwaggerResource setResources(String name, String location){
        SwaggerResource swaggerResource = new SwaggerResource();
        swaggerResource.setName(name);
        swaggerResource.setLocation(location);
        swaggerResource.setSwaggerVersion("2.0");
        return swaggerResource;
    }


}