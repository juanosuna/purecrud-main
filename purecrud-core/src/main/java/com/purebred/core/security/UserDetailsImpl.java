package com.purebred.core.security;

import com.purebred.core.entity.security.AbstractRole;
import com.purebred.core.entity.security.AbstractUser;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.HashSet;

/**
 * Integrates PureCRUD security entities with Spring Security so that Spring Security
 * gets user and role information from the PureCRUD database.
 */
public class UserDetailsImpl implements UserDetails {

    private Collection<GrantedAuthority> grantedAuthorities = new HashSet<GrantedAuthority>();
    private String username;
    private String password;
    private boolean accountNonExpired;
    private boolean accountNonLocked;
    private boolean credentialsNonExpired;
    private boolean enabled;

    public UserDetailsImpl(AbstractUser user) {
        Collection<AbstractRole> roles = user.getRoles();

        for (AbstractRole role : roles) {
            SimpleGrantedAuthority grantedAuthority = new SimpleGrantedAuthority(role.getName());
            grantedAuthorities.add(grantedAuthority);
        }

        username = user.getLoginName();
        password = user.getLoginPassword();
        accountNonExpired = !user.isAccountExpired();
        accountNonLocked = !user.isAccountLocked();
        credentialsNonExpired = !user.isCredentialsExpired();
        enabled = user.isEnabled();
    }

    @Override
    public Collection<GrantedAuthority> getAuthorities() {
        return this.grantedAuthorities;
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return accountNonExpired;
    }

    @Override
    public boolean isAccountNonLocked() {
        return accountNonLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return credentialsNonExpired;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }
}
