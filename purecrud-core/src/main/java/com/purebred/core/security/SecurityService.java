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

/**
 * Service for getting the current user and logging out. The current user entity
 * provides access to roles and permissions.
 */
@Service
public class SecurityService {

    public static final String SYSTEM_USER = "system";

    private Map<String, AbstractUser> users = new HashMap<String, AbstractUser>();

    /**
     * Get the login name of the currently logged in user.
     *
     * @return login name
     */
    public static String getCurrentLoginName() {
        if (SecurityContextHolder.getContext() != null && SecurityContextHolder.getContext().getAuthentication() != null
                && SecurityContextHolder.getContext().getAuthentication().getPrincipal() != null) {
            UserDetails user = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            return user.getUsername();
        } else {
            return SYSTEM_USER;
        }
    }

    /**
     * Get the user entity for the currently logged in user. Uses a cache
     * to improve performance.
     *
     * @return user entity with roles and permissions
     */
    public AbstractUser getCurrentUser() {
        String loginName = getCurrentLoginName();
        AbstractUser user = users.get(loginName);
        if (user == null) {
            user = findUser(loginName);
            users.put(loginName, user);
        }

        return user;
    }

    /**
     * Find user entity for the given login name. Does not use cache.
     *
     * @param loginName login name of the user entity to find
     * @return user entity
     */
    public static AbstractUser findUser(String loginName) {

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

    /**
     * Clear the Authentication object from Spring Security's context.
     */
    public void logout() {
        SecurityContextHolder.getContext().setAuthentication(null);
    }
}
