package com.purebred.core.security;

import com.purebred.core.entity.security.AbstractUser;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public class UserDetailsServiceImpl implements UserDetailsService {

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        AbstractUser user = SecurityService.findCurrentUser(username);

        return new UserDetailsImpl(user);
    }
}
