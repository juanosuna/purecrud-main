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

package com.purebred.sample.view.user;

import com.purebred.core.dao.StructuredEntityQuery;
import com.purebred.sample.dao.UserDao;
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
public class UserQuery extends StructuredEntityQuery<User> {

    @Resource
    private UserDao userDao;

    private String loginName;
    private Role doesNotBelongToRole;


    public String getLoginName() {
        return loginName;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }

    public Role getDoesNotBelongToRole() {
        return doesNotBelongToRole;
    }

    public void setDoesNotBelongToRole(Role doesNotBelongToRole) {
        this.doesNotBelongToRole = doesNotBelongToRole;
    }

    @Override
    public List<User> execute() {
        return userDao.execute(this);
    }

    @Override
    public List<Predicate> buildCriteria(CriteriaBuilder builder, Root<User> rootEntity) {
        List<Predicate> criteria = new ArrayList<Predicate>();

        if (!isEmpty(loginName)) {
            ParameterExpression<String> p = builder.parameter(String.class, "loginName");
            criteria.add(builder.like(builder.upper(rootEntity.<String>get("loginName")), p));
        }

        if (!isEmpty(doesNotBelongToRole)) {
            ParameterExpression<Role> p = builder.parameter(Role.class, "doesNotBelongToRole");
            Join join = rootEntity.join("userRoles", JoinType.LEFT);
            criteria.add(builder.or(
                    builder.notEqual(join.get("role"), p),
                    builder.isNull(join.get("role"))
            ));
        }

        return criteria;
    }

    @Override
    public void setParameters(TypedQuery typedQuery) {
        if (!isEmpty(loginName)) {
            typedQuery.setParameter("loginName", "%" + loginName.toUpperCase() + "%");
        }
        if (!isEmpty(doesNotBelongToRole)) {
            typedQuery.setParameter("doesNotBelongToRole", doesNotBelongToRole);
        }
    }

    @Override
    public Path buildOrderBy(Root<User> rootEntity) {
        return null;
    }

    @Override
    public void addFetchJoins(Root<User> rootEntity) {
    }

    @Override
    public void clear() {
        Role doesNotBelongToRole = this.doesNotBelongToRole;
        super.clear();
        this.doesNotBelongToRole = doesNotBelongToRole;
    }

    @Override
    public String toString() {
        return "UserQuery{" +
                "loginName='" + loginName + '\'' +
                '}';
    }
}
