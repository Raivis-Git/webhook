package com.facebook.webhook.config;

import com.restfb.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.context.annotation.*;

@Configuration
public class FacebookClientConfig {

    @Value("${facebook.access.token}")
    String accessToken;
    @Value("${facebook.app.secret}")
    private String appSecret;
    @Value("${facebook.verify.token}")
    private String verifyToken;

    @Bean
    public FacebookClient facebookClient() {
        return new DefaultFacebookClient(accessToken, appSecret, Version.LATEST);
    }

    // Getter for verify token (used in webhook verification)
    public String getVerifyToken() {
        return verifyToken;
    }

}
