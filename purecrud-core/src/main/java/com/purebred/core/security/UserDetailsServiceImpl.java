package com.purebred.core.security;

import com.purebred.core.dao.EntityDao;
import com.purebred.core.entity.security.AbstractUser;
import com.purebred.core.util.SpringApplicationContext;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public class UserDetailsServiceImpl implements UserDetailsService {

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        EntityDao dao = SpringApplicationContext.getBeanByTypeAndGenericArgumentType(EntityDao.class, AbstractUser.class);
        AbstractUser user = (AbstractUser) dao.findByNaturalId("loginName", username);
        return new UserDetailsImpl(user);
    }
}
