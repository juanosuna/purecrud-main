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

package com.purebred.sample.view.role;

import com.purebred.core.dao.StructuredEntityQuery;
import com.purebred.sample.dao.RoleDao;
import com.purebred.sample.entity.security.Role;
import com.purebred.sample.entity.security.User;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

@Component
@Scope("prototype")
public class RoleQuery extends StructuredEntityQuery<Role> {

    @Resource
    private RoleDao roleDao;

    private String name;
    private User doesNotBelongToUser;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public User getDoesNotBelongToUser() {
        return doesNotBelongToUser;
    }

    public void setDoesNotBelongToUser(User doesNotBelongToUser) {
        this.doesNotBelongToUser = doesNotBelongToUser;
    }

    @Override
    public List<Role> execute() {
        return roleDao.execute(this);
    }

    @Override
    public List<Predicate> buildCriteria(CriteriaBuilder builder, Root<Role> rootEntity) {
        List<Predicate> criteria = new ArrayList<Predicate>();

        if (!isEmpty(name)) {
            ParameterExpression<String> p = builder.parameter(String.class, "name");
            criteria.add(builder.like(builder.upper(rootEntity.<String>get("name")), p));
        }

        if (!isEmpty(doesNotBelongToUser)) {
            ParameterExpression<User> p = builder.parameter(User.class, "doesNotBelongToUser");
            Join join = rootEntity.join("userRoles", JoinType.LEFT);
            criteria.add(builder.or(
                    builder.notEqual(join.get("user"), p),
                    builder.isNull(join.get("user"))
            ));
        }

        return criteria;
    }

    @Override
    public void setParameters(TypedQuery typedQuery) {
        if (!isEmpty(name)) {
            typedQuery.setParameter("name", "%" + name.toUpperCase() + "%");
        }
        if (!isEmpty(doesNotBelongToUser)) {
            typedQuery.setParameter("doesNotBelongToUser", doesNotBelongToUser);
        }
    }

    @Override
    public Path buildOrderBy(Root<Role> rootEntity) {
        return null;
    }

    @Override
    public void addFetchJoins(Root<Role> rootEntity) {
    }

    @Override
    public void clear() {
        User doesNotBelongToUser = this.doesNotBelongToUser;
        super.clear();
        this.doesNotBelongToUser = doesNotBelongToUser;
    }

    @Override
    public String toString() {
        return "RoleQuery{" +
                "name='" + name + '\'' +
                '}';
    }
}
