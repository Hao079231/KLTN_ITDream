package com.base.auth.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.provider.error.OAuth2AccessDeniedHandler;

@Configuration
@EnableResourceServer
public class ResourceServerConfig extends ResourceServerConfigurerAdapter {
    @Autowired
    private JwtAccessTokenConverter jwtAccessTokenConverter;

    @Autowired
    JsonToUrlEncodedAuthenticationFilter jsonFilter;

    @Bean
    public DefaultTokenServices createTokenServices() {
        DefaultTokenServices defaultTokenServices = new DefaultTokenServices();
        jwtAccessTokenConverter.setAccessTokenConverter(new CustomTokenConverter());
        defaultTokenServices.setTokenStore(new JwtTokenStore(jwtAccessTokenConverter));
        return defaultTokenServices;
    }

    @Override
    public void configure(HttpSecurity http) throws Exception {
        http.addFilterAfter(jsonFilter, BasicAuthenticationFilter.class)
                .requestMatchers()
                .and()
                .authorizeRequests()
                .antMatchers("/v2/api-docs", "/configuration/ui", "/swagger-resources/**", "/configuration/**", "/swagger-ui.html", "/index", "/pub/**", "/api/token","/api/auth/pwd/verify-token",
                        "/api/auth/activate/resend", "/api/auth/pwd", "/api/auth/logout", "/actuator/**").permitAll()
                .antMatchers("/v1/student/signup").permitAll()
                .antMatchers("/v1/educator/signup").permitAll()
                .antMatchers("/v1/account/request_forget_password", "/v1/account/forget_password", "/v1/account/resend_verify", "/v1/account/verify").permitAll()
                .antMatchers("/v1/category/auto-complete").permitAll()
                .antMatchers("/v1/simulation/guest_list", "/v1/simulation/guest_get/**").permitAll()
                .antMatchers("/v1/task/guest_list").permitAll()
                .antMatchers("/v1/feedback/client_list").permitAll()
                .antMatchers("/v1/organization/guest_list").permitAll()
                .antMatchers("/v1/blog/student-list", "/v1/blog/student-get").permitAll()
                .antMatchers("/v1/job-post/guest-list", "/v1/job-post/guest-get/**").permitAll()
                .antMatchers("/v1/nation/client-list", "/v1/nation/client-get/**").permitAll()
                .antMatchers("/**").authenticated()
                .and().sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and().exceptionHandling().accessDeniedHandler(new OAuth2AccessDeniedHandler());
    }

    @Override
    public void configure(ResourceServerSecurityConfigurer resources) throws Exception {
        resources.resourceId("user-base-service");
    }
}