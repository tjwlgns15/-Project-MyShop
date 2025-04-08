package com.jihun.myshop.global.security.config;

import com.jihun.myshop.global.security.dsl.CustomApiDsl;
import com.jihun.myshop.global.security.config.manager.SecurityHandlersManager;
import com.jihun.myshop.global.security.config.manager.AuthenticationComponentsManager;
import com.jihun.myshop.global.security.config.manager.AuthorizationComponentsManager;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final AuthenticationComponentsManager authManager;
    private final AuthorizationComponentsManager authorizationManager;
    private final SecurityHandlersManager handlersManager;


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        AuthenticationManager authenticationManager = authManager.configureAuthenticationManager(http);

        http
//                .csrf(authorizationManager::configureCsrf)
                .csrf(AbstractHttpConfigurer::disable)
//                .cors()

                .authenticationManager(authenticationManager)

                .authorizeHttpRequests(authorizationManager::configureAuthorization)

                .with(new CustomApiDsl<>(), dsl -> dsl
                        .customSuccessHandler(handlersManager.getSuccessHandler())
                        .customFailureHandler(handlersManager.getFailureHandler())
                        .loginPage("/auth/login")
                        .loginProcessingUrl("/api/auth/login")
                )

                .logout(handlersManager::configureLogout)

                .rememberMe(authManager::configureRememberMe)

                .exceptionHandling(handlersManager::configureExceptionHandling)

                ;

        return http.build();

    }

    @Bean
    public SecurityContextRepository securityContextRepository() {
        return new HttpSessionSecurityContextRepository();
    }

}
