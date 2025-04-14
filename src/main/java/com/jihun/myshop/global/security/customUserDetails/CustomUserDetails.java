package com.jihun.myshop.global.security.customUserDetails;

import com.jihun.myshop.domain.user.entity.dto.UserDto;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

import static com.jihun.myshop.domain.user.entity.dto.UserDto.*;

@Data
public class CustomUserDetails implements UserDetails {

    private final UserResponse userResponse;
    private final List<GrantedAuthority> roles;

    public Long getId() {
        return userResponse.getId();
    }

    public boolean hasRole(String roleName) {
        return this.roles.stream()
                .anyMatch(authority -> authority.getAuthority().equals(roleName));
    }

    @Override
    public String getUsername() {
        return userResponse.getUsername();
    }

    @Override
    public String getPassword() {
        return userResponse.getPassword();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
