package com.base.auth.config;

import com.base.auth.service.impl.UserServiceImpl;
import com.base.auth.utils.ConvertUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.exceptions.InvalidTokenException;
import org.springframework.security.oauth2.provider.*;
import org.springframework.security.oauth2.provider.token.AbstractTokenGranter;
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;

import java.io.IOException;
import java.security.GeneralSecurityException;

public class CustomTokenGranter extends AbstractTokenGranter {

    private UserServiceImpl userService;
    private AuthenticationManager authenticationManager;

    protected CustomTokenGranter(AuthorizationServerTokenServices tokenServices, ClientDetailsService clientDetailsService, OAuth2RequestFactory requestFactory, String grantType) {
        super(tokenServices, clientDetailsService, requestFactory, grantType);
    }

    public CustomTokenGranter(AuthenticationManager authenticationManager,AuthorizationServerTokenServices tokenServices, ClientDetailsService clientDetailsService, OAuth2RequestFactory requestFactory, String grantType, UserServiceImpl userService) {
        super(tokenServices, clientDetailsService, requestFactory, grantType);
        this.userService = userService;
        this.authenticationManager = authenticationManager;
    }

    @Override
    protected OAuth2Authentication getOAuth2Authentication(ClientDetails client, TokenRequest tokenRequest) {
        return super.getOAuth2Authentication(client, tokenRequest);
    }

    protected OAuth2AccessToken getAccessToken(ClientDetails client, TokenRequest tokenRequest) {
        try {
            if(SecurityConstant.GRANT_TYPE_STUDENT.equalsIgnoreCase(tokenRequest.getGrantType())){
                String email = tokenRequest.getRequestParameters().get("email");
                String password = tokenRequest.getRequestParameters().get("password");
                String accessToken = tokenRequest.getRequestParameters().get("accessToken");
                if (StringUtils.isNotBlank(accessToken)){
                    return userService.getAccessTokenForGoogleStudent(client, tokenRequest, accessToken ,this.getTokenServices());
                }
                return userService.getAccessTokenForUser(client, tokenRequest, password, email ,this.getTokenServices());
            } else if (SecurityConstant.GRANT_TYPE_EDUCATOR.equalsIgnoreCase(tokenRequest.getGrantType())) {
                String email = tokenRequest.getRequestParameters().get("email");
                String password = tokenRequest.getRequestParameters().get("password");
                String accessToken = tokenRequest.getRequestParameters().get("accessToken");
                String organizationId = tokenRequest.getRequestParameters().get("organizationId");
                if (StringUtils.isNotBlank(accessToken)){
                    return userService.getAccessTokenForGoogleEducator(client, tokenRequest, accessToken, organizationId ,this.getTokenServices());
                }
                return userService.getAccessTokenForEducator(client, tokenRequest, password, email ,this.getTokenServices());
            } else {
                String username = tokenRequest.getRequestParameters().get("username");
                String password = tokenRequest.getRequestParameters().get("password");
                return userService.getAccessTokenForCustomType(client, tokenRequest, username, password, this.getTokenServices());
            }
        } catch (GeneralSecurityException | IOException e) {
            e.printStackTrace();
            throw new InvalidTokenException("account or tenant invalid");
        }
    }

}
