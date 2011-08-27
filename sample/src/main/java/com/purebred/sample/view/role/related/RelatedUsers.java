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

package com.purebred.sample.view.role.related;

import com.purebred.core.dao.ToManyRelationshipQuery;
import com.purebred.core.view.entity.field.DisplayFields;
import com.purebred.core.view.entity.tomanyrelationship.ManyToManyRelationshipResults;
import com.purebred.core.view.entity.tomanyrelationship.ToManyRelationship;
import com.purebred.sample.dao.UserDao;
import com.purebred.sample.dao.UserRoleDao;
import com.purebred.sample.entity.security.Role;
import com.purebred.sample.entity.security.User;
import com.purebred.sample.entity.security.UserRole;
import com.purebred.sample.view.select.UserSelect;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

@Component
@Scope("prototype")
public class RelatedUsers extends ToManyRelationship<User> {

    @Resource
    private RelatedUsersResults relatedUsersResults;

    @Override
    public String getEntityCaption() {
        return "Users";
    }

    @Override
    public RelatedUsersResults getResultsComponent() {
        return relatedUsersResults;
    }

    @Component
    @Scope("prototype")
    public static class RelatedUsersResults extends ManyToManyRelationshipResults<User, UserRole> {

        @Resource
        private UserDao userDao;

        @Resource
        private UserRoleDao userRoleDao;

        @Resource
        private UserSelect userSelect;

        @Resource
        private RelatedUsersQuery relatedUsersQuery;

        @Override
        public UserDao getEntityDao() {
            return userDao;
        }

        @Override
        public UserRoleDao getAssociationDao() {
            return userRoleDao;
        }

        @Override
        public UserSelect getEntitySelect() {
            return userSelect;
        }

        @Override
        public ToManyRelationshipQuery getEntityQuery() {
            return relatedUsersQuery;
        }

        @Override
        public void add() {
            Role parentRole = relatedUsersQuery.getParent();
            userSelect.getResultsComponent().getEntityQuery().setDoesNotBelongToRole(parentRole);
            super.add();
        }

        @Override
        public void configureFields(DisplayFields displayFields) {
            displayFields.setPropertyIds(new String[]{
                    "loginName",
                    "lastModified",
                    "modifiedBy"
            });
        }

        @Override
        public String getChildPropertyId() {
            return "userRoles";
        }

        @Override
        public String getParentPropertyId() {
            return "userRoles";
        }

        @Override
        public UserRole createAssociationEntity(User user) {
            return new UserRole(user, relatedUsersQuery.getParent());
        }

        @Override
        public String getEntityCaption() {
            return "Users";
        }
    }

    @Component
    @Scope("prototype")
    public static class RelatedUsersQuery extends ToManyRelationshipQuery<User, Role> {

        @Resource
        private UserDao userDao;

        private Role role;

        @Override
        public void setParent(Role role) {
            this.role = role;
        }

        @Override
        public Role getParent() {
            return role;
        }

        @Override
        public List<User> execute() {
            return userDao.execute(this);
        }

        @Override
        public List<Predicate> buildCriteria(CriteriaBuilder builder, Root<User> rootEntity) {
            List<Predicate> criteria = new ArrayList<Predicate>();

            if (!isEmpty(role)) {
                ParameterExpression<Role> p = builder.parameter(Role.class, "role");
                criteria.add(builder.equal(rootEntity.join("userRoles").get("role"), p));
            }

            return criteria;
        }

        @Override
        public void setParameters(TypedQuery typedQuery) {
            if (!isEmpty(role)) {
                typedQuery.setParameter("role", role);
            }
        }

        @Override
        public Path buildOrderBy(Root<User> rootEntity) {
            return null;
        }

        @Override
        public void addFetchJoins(Root<User> rootEntity) {
            rootEntity.fetch("userRoles", JoinType.INNER).fetch("user", JoinType.INNER);
        }

        @Override
        public String toString() {
            return "RelatedUsers{" +
                    "role='" + role + '\'' +
                    '}';
        }

    }
}

