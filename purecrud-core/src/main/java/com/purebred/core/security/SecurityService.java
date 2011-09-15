package com.purebred.core.security;


import com.purebred.core.dao.EntityDao;
import com.purebred.core.entity.security.AbstractUser;
import com.purebred.core.util.ReflectionUtil;
import com.purebred.core.util.SpringApplicationContext;
import com.purebred.core.util.assertion.Assert;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

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
            user = findCurrentUser(loginName);
            users.put(loginName, user);
        }

        return user;
    }

    public static AbstractUser findCurrentUser(String loginName) {

        Assert.PROGRAMMING.assertTrue(loginName != null, "Current loginName is null");

        EntityDao userDao = null;
        Set<EntityDao> daos = SpringApplicationContext.getBeansByTypeAndGenericArgumentType(EntityDao.class, AbstractUser.class);
        for (EntityDao dao : daos) {
            Class argType = ReflectionUtil.getGenericArgumentType(dao.getClass());
            if (argType != null && AbstractUser.class.equals(argType.getSuperclass())) {
                userDao = dao;
            }
        }
        Assert.PROGRAMMING.assertTrue(userDao != null,
                "No instance of EntityDao found with a generic argument type that is a subclass of " +
                        AbstractUser.class.getName());

        return (AbstractUser) userDao.findByNaturalId("loginName", loginName);
    }

    public void logout() {
        SecurityContextHolder.getContext().setAuthentication(null);
    }
}
