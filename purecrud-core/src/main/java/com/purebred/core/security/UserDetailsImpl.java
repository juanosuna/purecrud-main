package com.purebred.core.security;

import com.purebred.core.entity.security.AbstractRole;
import com.purebred.core.entity.security.AbstractUser;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.HashSet;

public class UserDetailsImpl implements UserDetails {

    private Collection<GrantedAuthority> grantedAuthorities = new HashSet<GrantedAuthority>();
    private String username;
    private String password;

    public UserDetailsImpl(AbstractUser user) {
        Collection<AbstractRole> roles = user.getRoles();

        for (AbstractRole role : roles) {
            SimpleGrantedAuthority grantedAuthority = new SimpleGrantedAuthority(role.getName());
            grantedAuthorities.add(grantedAuthority);
        }

        username = user.getLoginName();
        password = user.getLoginPassword();
    }


    public Collection<GrantedAuthority> getAuthorities() {
        return this.grantedAuthorities;
    }

    public String getPassword() {
        return this.password;
    }

    public String getUsername() {
        return this.username;
    }

    public boolean isAccountNonExpired() {
        return true;
    }

    public boolean isAccountNonLocked() {
        return true;
    }

    public boolean isCredentialsNonExpired() {
        return true;
    }

    public boolean isEnabled() {
        return true;
    }
}
