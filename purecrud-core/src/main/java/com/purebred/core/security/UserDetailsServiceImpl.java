package com.purebred.core.security;

import com.purebred.core.entity.security.AbstractUser;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

/**
 * Loads UserDetails by username by integrating Spring Security with PureCRUD SecurityService
 */
public class UserDetailsServiceImpl implements UserDetailsService {

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        AbstractUser user = SecurityService.findUser(username);

        return new UserDetailsImpl(user);
    }
}
