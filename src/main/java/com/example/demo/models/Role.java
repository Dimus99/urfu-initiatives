package com.example.demo.models;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Set;
import java.util.stream.Collectors;

public enum Role implements GrantedAuthority {
    USER(Set.of(Permission.INITIATIVE_ADD)),
    ADMIN(Set.of(Permission.values())),
    MODERATOR(Set.of(Permission.INITIATIVE_ADD, Permission.INITIATIVE_MANAGE)),
    EXPERT(Set.of(Permission.INITIATIVE_ADD, Permission.EXPERT_VOTE));


    private final Set<Permission> permissions;

    Role(Set<Permission> permissions) {
        this.permissions = permissions;
    }

    public Set<Permission> getPermissions() {
        return permissions;
    }

    public Set<SimpleGrantedAuthority> getAuthorities(){  // для PreAuthorize
        return getPermissions().stream()
                .map(permission -> new SimpleGrantedAuthority(permission.getPermission()))
                .collect(Collectors.toSet());
    }

    @Override
    public String getAuthority() {
        return name();
    }
}
