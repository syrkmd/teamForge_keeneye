package org.yvl.teamforge.security.user;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.yvl.teamforge.entity.User;

import java.util.Collection;
import java.util.List;

@RequiredArgsConstructor
@Getter
public class UserPrincipal implements UserDetails {

    private final User user;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(
                new SimpleGrantedAuthority("ROLE_" + user.getSystemRole().getName())
        );
    }

    @Override
    public @Nullable String getPassword() {
        return user.getPasswordHash();
    }

    @Override
    public String getUsername() {
        return user.getEmail();
    }

    @Override
    public boolean isEnabled() {
        return user.getIsActive();
    }
}
