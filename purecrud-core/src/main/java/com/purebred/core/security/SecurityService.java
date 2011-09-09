package com.purebred.core.security;


import com.purebred.core.dao.EntityDao;
import com.purebred.core.entity.security.AbstractUser;
import com.purebred.core.util.SpringApplicationContext;
import com.purebred.core.util.assertion.Assert;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SecurityService {

    public static final String SYSTEM_USER = "system";

    private Map<String, AbstractUser> users = new HashMap<String, AbstractUser>();

    public static String getCurrentLoginName() {
        if (SecurityContextHolder.getContext() != null && SecurityContextHolder.getContext().getAuthentication() != null
                && SecurityContextHolder.getContext().getAuthentication().getPrincipal() != null) {
            UserDetails user = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            return user.getUsername();
        } else {
            return SYSTEM_USER;
        }
    }

    public AbstractUser getCurrentUser() {
        String loginName = getCurrentLoginName();
        AbstractUser user = users.get(loginName);
        if (user == null) {
            user = getCurrentUserImpl();
            users.put(loginName, user);
        }

        return user;
    }

    private AbstractUser getCurrentUserImpl() {
        String loginName = getCurrentLoginName();
        Assert.PROGRAMMING.assertTrue(loginName != null, "Current loginName is null");

        EntityDao dao = SpringApplicationContext.getBeanByTypeAndGenericArgumentType(EntityDao.class, AbstractUser.class);
        List<AbstractUser> users = dao.findByProperty("loginName", loginName);
        Assert.DATABASE.assertTrue(users.size() == 1, "SecurityService did not find exactly one user with loginName: "
                + loginName);
        return users.get(0);
    }

    public void logout() {
        SecurityContextHolder.getContext().setAuthentication(null);
    }
}
