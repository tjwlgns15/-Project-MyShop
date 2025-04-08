package com.jihun.myshop.global.security.manager;

import com.jihun.myshop.global.security.mapper.MapBasedUrlRoleMapper;
import com.jihun.myshop.global.security.service.CustomAuthorizationService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.authorization.AuthorityAuthorizationManager;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;
import org.springframework.security.web.servlet.util.matcher.MvcRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcherEntry;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;

import java.util.List;
import java.util.function.Supplier;

import static org.springframework.security.web.util.matcher.RequestMatcher.*;

@Component
@RequiredArgsConstructor
public class CustomAuthorizationManager implements AuthorizationManager<RequestAuthorizationContext> {

    private final HandlerMappingIntrospector handlerMappingIntrospector;
    private static final AuthorizationDecision ACCESS = new AuthorizationDecision(true);
    private CustomAuthorizationService customAuthorizationService;
    List<RequestMatcherEntry<AuthorizationManager<RequestAuthorizationContext>>> mappings;
    private final RoleHierarchyImpl roleHierarchy;

    @PostConstruct
    public void mapping() {
        customAuthorizationService = new CustomAuthorizationService(new MapBasedUrlRoleMapper());
        mappings = customAuthorizationService.getUrlRoleMappings()
                .entrySet().stream()
                .map(entry -> new RequestMatcherEntry<>(
                        new MvcRequestMatcher(handlerMappingIntrospector, entry.getKey()),
                        customAuthorizationManager(entry.getValue()))
                )
                .toList();
    }

    @Override
    public AuthorizationDecision check(Supplier<Authentication> authentication, RequestAuthorizationContext request) {
        for (RequestMatcherEntry<AuthorizationManager<RequestAuthorizationContext>> mapping : mappings) {
            RequestMatcher requestMatcher = mapping.getRequestMatcher();
            MatchResult matchResult = requestMatcher.matcher(request.getRequest());

            if (matchResult.isMatch()) {
                AuthorizationManager<RequestAuthorizationContext> manager = mapping.getEntry();
                return manager.check(authentication, new RequestAuthorizationContext(request.getRequest(), matchResult.getVariables()));
            }
        }
        return ACCESS;
    }

//    private AuthorizationManager<RequestAuthorizationContext> customAuthorizationManager(String role) {
//        return AuthorityAuthorizationManager.hasAnyAuthority(role);
//    }

    private AuthorizationManager<RequestAuthorizationContext> customAuthorizationManager(String role) {
        // "permitAll"인 경우 특별 처리
        if ("permitAll".equals(role)) {
            return (authentication, object) -> new AuthorizationDecision(true);
        }

        // 기타 역할에 대해서는 역할 계층 사용
        AuthorityAuthorizationManager<RequestAuthorizationContext> authorizationManager =
                AuthorityAuthorizationManager.hasAuthority(role);
        authorizationManager.setRoleHierarchy(roleHierarchy);
        return authorizationManager;
    }


}
