package com.purebred.core.security;


import com.purebred.core.dao.EntityDao;
import com.purebred.core.entity.security.AbstractUser;
import com.purebred.core.util.SpringApplicationContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
public class SecurityService {

    public String getCurrentLoginName() {
        if (SecurityContextHolder.getContext() != null && SecurityContextHolder.getContext().getAuthentication() != null
                && SecurityContextHolder.getContext().getAuthentication().getPrincipal() != null) {
            UserDetails user = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            return user.getUsername();
        } else {
            return null;
        }
    }

    public AbstractUser getCurrentUser() {
        String loginName = getCurrentLoginName();
        if (loginName != null) {
            EntityDao dao = SpringApplicationContext.getBeanByTypeAndGenericArgumentType(EntityDao.class, AbstractUser.class);
            return (AbstractUser) dao.findByBusinessKey("loginName", loginName);
        } else {
            return null;
        }
    }

    public void logout() {
        SecurityContextHolder.getContext().setAuthentication(null);
    }
}
