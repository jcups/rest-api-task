package ru.jcups.restapitask.model;

import org.springframework.security.core.GrantedAuthority;

public enum Role implements GrantedAuthority {
    ROLE_USER,
    ROLE_MODERATOR,
    ROLE_ADMIN,
    ROLE_CREATOR;

    @Override
    public String getAuthority() {
        return name();
    }

}
