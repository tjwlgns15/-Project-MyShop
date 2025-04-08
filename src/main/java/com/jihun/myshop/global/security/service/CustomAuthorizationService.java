package com.jihun.myshop.global.security.service;

import com.jihun.myshop.global.security.mapper.UrlRoleMapper;

import java.util.Map;

public class CustomAuthorizationService {
    private final UrlRoleMapper delegate;

    public CustomAuthorizationService(UrlRoleMapper delegate) {
        this.delegate = delegate;
    }

    public Map<String, String> getUrlRoleMappings() {
        return delegate.getUrlRoleMappings();
    }
}
