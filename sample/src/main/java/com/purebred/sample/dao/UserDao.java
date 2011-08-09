/*
 * BROWN BAG CONFIDENTIAL
 *
 * Copyright (c) 2011 Brown Bag Consulting LLC
 * All Rights Reserved.
 *
 * NOTICE:  All information contained herein is, and remains
 * the property of Brown Bag Consulting LLC and its suppliers,
 * if any.  The intellectual and technical concepts contained
 * herein are proprietary to Brown Bag Consulting LLC
 * and its suppliers and may be covered by U.S. and Foreign Patents,
 * patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Brown Bag Consulting LLC.
 */

package com.purebred.sample.dao;

import com.purebred.core.dao.EntityDao;
import com.purebred.sample.entity.User;
import org.springframework.stereotype.Repository;

import javax.persistence.Query;
import java.util.List;

import static com.purebred.sample.dao.CacheSettings.setReadOnly;

@Repository
public class UserDao extends EntityDao<User, Long> {

    @Override
    public List<User> findAll() {
        Query query = getEntityManager().createQuery("SELECT u FROM User u ORDER BY u.loginName");
        setReadOnly(query);

        return query.getResultList();
    }

    public User findByName(String loginName) {
        Query query = getEntityManager().createQuery("SELECT u FROM User u WHERE u.loginName = :loginName");
        query.setParameter("loginName", loginName);

        return (User) query.getSingleResult();
    }
}
