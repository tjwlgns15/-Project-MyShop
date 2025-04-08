package com.jihun.myshop.global.security.config.manager;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.RememberMeConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
@RequiredArgsConstructor
public class AuthenticationComponentsManager {
    private final AuthenticationProvider customAuthenticationProvider;
    private final UserDetailsService userDetailsService;

    public AuthenticationManager configureAuthenticationManager(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder authenticationManagerBuilder = http.getSharedObject(AuthenticationManagerBuilder.class);
        authenticationManagerBuilder.authenticationProvider(customAuthenticationProvider);
        return authenticationManagerBuilder.build();
    }

    public void configureRememberMe(RememberMeConfigurer<HttpSecurity> rememberMe) {
        String REMEMBER_ME_KEY = "solmitech-cms400-remember-me-key";
        rememberMe
                .key(REMEMBER_ME_KEY)
                .tokenValiditySeconds((int) Duration.ofDays(14).toSeconds())
                .userDetailsService(userDetailsService)
                .rememberMeParameter("remember-me");
    }
}
