package com.jihun.myshop.global.security.mapper;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class MapBasedUrlRoleMapper implements UrlRoleMapper {

    private final LinkedHashMap<String, String> urlRoleMappings = new LinkedHashMap<>();

    @Override
    public Map<String, String> getUrlRoleMappings() {

        urlRoleMappings.put("/", "permitAll");
        urlRoleMappings.put("/test", "permitAll");

        // 정적 리소스
        urlRoleMappings.put("/templates/**", "permitAll");
        urlRoleMappings.put("/js/**", "permitAll");
        urlRoleMappings.put("/css/**", "permitAll");
        urlRoleMappings.put("/images/**", "permitAll");
        urlRoleMappings.put("/favicon.*", "permitAll");
        urlRoleMappings.put("/*/icon-*", "permitAll");

        // rest api ( 웹 )
        urlRoleMappings.put("/auth/**", "permitAll");
        urlRoleMappings.put("/api/auth/logout", "authenticated");
        urlRoleMappings.put("/api/auth/**", "permitAll");

        urlRoleMappings.put("/user/**", "ROLE_USER");
        urlRoleMappings.put("/api/user/**", "ROLE_USER");

        urlRoleMappings.put("/manager/**", "ROLE_MANAGER");
        urlRoleMappings.put("/api/manager/**", "ROLE_MANAGER");

        urlRoleMappings.put("/dba/**", "ROLE_DBA");
        urlRoleMappings.put("/api/dba/**", "ROLE_DBA");

        urlRoleMappings.put("/admin/**", "ROLE_ADMIN");
        urlRoleMappings.put("/api/admin/**", "ROLE_ADMIN");

        return new HashMap<>(urlRoleMappings);
    }
}
