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

package com.purebred.sample.view.user.related;

import com.purebred.core.dao.ToManyRelationshipQuery;
import com.purebred.core.view.field.DisplayFields;
import com.purebred.core.view.tomanyrelationship.ManyToManyRelationshipResults;
import com.purebred.core.view.tomanyrelationship.ToManyRelationship;
import com.purebred.sample.dao.RoleDao;
import com.purebred.sample.dao.UserRoleDao;
import com.purebred.sample.entity.security.Role;
import com.purebred.sample.entity.security.User;
import com.purebred.sample.entity.security.UserRole;
import com.purebred.sample.view.select.RoleSelect;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

@Component
@Scope("prototype")
public class RelatedRoles extends ToManyRelationship<Role> {

    @Resource
    private RelatedRolesResults relatedRolesResults;

    @Override
    public String getEntityCaption() {
        return "Roles";
    }

    @Override
    public RelatedRolesResults getResults() {
        return relatedRolesResults;
    }

    @Component
    @Scope("prototype")
    public static class RelatedRolesResults extends ManyToManyRelationshipResults<Role, UserRole> {

        @Resource
        private RoleDao roleDao;

        @Resource
        private UserRoleDao userRoleDao;

        @Resource
        private RoleSelect roleSelect;

        @Resource
        private RelatedRolesQuery relatedRolesQuery;

        @Override
        public void add() {
            User parentUser = relatedRolesQuery.getParent();
            roleSelect.getResults().getEntityQuery().setDoesNotBelongToUser(parentUser);
            super.add();
        }

        @Override
        public RoleDao getEntityDao() {
            return roleDao;
        }

        @Override
        public UserRoleDao getAssociationDao() {
            return userRoleDao;
        }

        @Override
        public RoleSelect getEntitySelect() {
            return roleSelect;
        }

        @Override
        public ToManyRelationshipQuery getEntityQuery() {
            return relatedRolesQuery;
        }

        @Override
        public void configureFields(DisplayFields displayFields) {
            displayFields.setPropertyIds(new String[]{
                    "name",
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
        public UserRole createAssociationEntity(Role role) {
            return new UserRole(relatedRolesQuery.getParent(), role);
        }

        @Override
        public String getEntityCaption() {
            return "Roles";
        }
    }

    @Component
    @Scope("prototype")
    public static class RelatedRolesQuery extends ToManyRelationshipQuery<Role, User> {

        @Resource
        private RoleDao roleDao;

        private User user;

        @Override
        public void setParent(User parent) {
            this.user = parent;
        }

        @Override
        public User getParent() {
            return user;
        }

        @Override
        public List<Role> execute() {
            return roleDao.execute(this);
        }

        @Override
        public List<Predicate> buildCriteria(CriteriaBuilder builder, Root<Role> rootEntity) {
            List<Predicate> criteria = new ArrayList<Predicate>();

            if (!isEmpty(user)) {
                ParameterExpression<User> p = builder.parameter(User.class, "user");
                criteria.add(builder.equal(rootEntity.join("userRoles").get("user"), p));
            }

            return criteria;
        }

        @Override
        public void setParameters(TypedQuery typedQuery) {
            if (!isEmpty(user)) {
                typedQuery.setParameter("user", user);
            }
        }

        @Override
        public Path buildOrderBy(Root<Role> rootEntity) {
            return null;
        }

        @Override
        public void addFetchJoins(Root<Role> rootEntity) {
            rootEntity.fetch("userRoles", JoinType.INNER).fetch("user", JoinType.INNER);
        }

        @Override
        public String toString() {
            return "RelatedRoles{" +
                    "user='" + user + '\'' +
                    '}';
        }

    }
}

