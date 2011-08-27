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
import com.purebred.sample.entity.security.Permission;
import com.purebred.sample.entity.security.Role;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.FlushModeType;
import javax.persistence.Query;
import java.util.List;

import static com.purebred.sample.dao.CacheSettings.setReadOnly;

@Repository
public class PermissionDao extends EntityDao<Permission, Long> {

    @Override
    public List<Permission> findAll() {
        Query query = getEntityManager().createQuery("SELECT p FROM Permission p ORDER BY p.entityType, p.field");
        setReadOnly(query);

        return query.getResultList();
    }

    public List<Permission> findByRole(Role role) {
        Query query = getEntityManager().createQuery("SELECT p FROM Permission p WHERE p.role = :role");
        query.setParameter("role", role);

        return query.getResultList();
    }

    public List<Permission> findByRoleEntityTypeAndField(Role role, String entityType, String field) {
        Query query = getEntityManager().createQuery("SELECT p FROM Permission p WHERE p.role = :role" +
                " AND p.entityType = :entityType AND p.field = :field");
        query.setParameter("role", role);
        query.setParameter("entityType", entityType);
        query.setParameter("field", field);

        query.setFlushMode(FlushModeType.COMMIT);

        return query.getResultList();
    }

    @Transactional
    @Override
    public void remove(Permission permission) {
        // todo implement me
    }
}
