package com.zeewoncode.pdd_server.security;

import com.zeewoncode.constant.UserTypeConstant;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

public class LoginUser implements UserDetails {

    private final Long userId;
    private final Integer userType;

    public LoginUser(Long userId, Integer userType) {
        this.userId = userId;
        this.userType = userType;
    }

    public Long getUserId() {
        return userId;
    }

    public Integer getUserType() {
        return userType;
    }

    /**
     * userType：1 买家、2 商家、9 管理员
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (userType == null) {
            return List.of();
        }
        return switch (userType) {
            case UserTypeConstant.USER_TYPE_ADMIN -> List.of(new SimpleGrantedAuthority("ROLE_ADMIN"));
            case UserTypeConstant.USER_TYPE_BUSINESS -> List.of(new SimpleGrantedAuthority("ROLE_BUSINESS"));
            case UserTypeConstant.USER_TYPE_USER -> List.of(new SimpleGrantedAuthority("ROLE_USER"));
            // 非法用户类型：不授予任何权限，后续 hasRole 校验会返回 403
            default -> List.of();
        };
    }

    @Override
    public String getPassword() {
        return null;
    }

    @Override
    public String getUsername() {
        return String.valueOf(userId);
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