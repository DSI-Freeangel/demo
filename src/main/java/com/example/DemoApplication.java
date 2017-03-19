package com.example;

import java.util.List;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.collect.Lists;

import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.AuthorizationScope;
import springfox.documentation.service.GrantType;
import springfox.documentation.service.OAuth;
import springfox.documentation.service.ResourceOwnerPasswordCredentialsGrant;
import springfox.documentation.service.SecurityReference;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger.web.ApiKeyVehicle;
import springfox.documentation.swagger.web.SecurityConfiguration;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootApplication
@EnableWebSecurity
@EnableJpaRepositories
@EnableTransactionManagement
@EnableAutoConfiguration
@EnableAuthorizationServer
@EnableResourceServer
@EnableSwagger2
@RestController
public class DemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}
	
    @RequestMapping(path = "/userinfo", method = RequestMethod.GET)
    Authentication userinfo(Authentication authentication) {
        return authentication;
    }

    @Bean
    public Docket apiDocumentation() {
        return new Docket(DocumentationType.SWAGGER_2)        		  
                .select()
                .apis(RequestHandlerSelectors.any())
                .paths(PathSelectors.any()).build()
                .securitySchemes(Lists.newArrayList(securitySchema()))
                .securityContexts(Lists.newArrayList(securityContext()));
    }
    
    @Bean
    SecurityConfiguration security() {
        return new SecurityConfiguration("demo", "demo", "true", "demo",
                "apiKey", ApiKeyVehicle.HEADER, "api_key", ",");
    }

    public static final String SECURITY_SCHEMA_OAUTH2 = "oauth2schema";
    public static final String AUTHORIZATION_SCOPE_GLOBAL = "global";
    public static final String AUTHORIZATION_SCOPE_GLOBAL_DESC ="accessEverything";

    private OAuth securitySchema() {
        List<AuthorizationScope> authorizationScopeList = Lists.newArrayList();
        authorizationScopeList.add(new AuthorizationScope(AUTHORIZATION_SCOPE_GLOBAL, AUTHORIZATION_SCOPE_GLOBAL_DESC));
        List<GrantType> grantTypes = Lists.newArrayList();
        ResourceOwnerPasswordCredentialsGrant passwordGrant = new ResourceOwnerPasswordCredentialsGrant("http://localhost:8080/oauth/token");;
        grantTypes.add(passwordGrant);
        OAuth oAuth = new OAuth(SECURITY_SCHEMA_OAUTH2, authorizationScopeList, grantTypes);
        return oAuth;
    }

    private SecurityContext securityContext() {
        return SecurityContext.builder()
                .securityReferences(defaultAuth())
                .forPaths(PathSelectors.any())
                .build();
    }

    private List<SecurityReference> defaultAuth() {
        AuthorizationScope authorizationScope
                = new AuthorizationScope(AUTHORIZATION_SCOPE_GLOBAL, AUTHORIZATION_SCOPE_GLOBAL_DESC);
        AuthorizationScope[] authorizationScopes = new AuthorizationScope[1];
        authorizationScopes[0] = authorizationScope;
        return Lists.newArrayList(
                new SecurityReference(SECURITY_SCHEMA_OAUTH2, authorizationScopes));
    }
}
