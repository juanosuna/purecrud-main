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

package com.purebred.sample.view.account.related;

import com.purebred.core.dao.ToManyRelationshipQuery;
import com.purebred.core.view.entity.field.DisplayFields;
import com.purebred.core.view.entity.tomanyrelationship.ToManyRelationship;
import com.purebred.core.view.entity.tomanyrelationship.ToManyRelationshipResults;
import com.purebred.sample.util.PhonePropertyFormatter;
import com.purebred.sample.dao.ContactDao;
import com.purebred.sample.entity.Account;
import com.purebred.sample.entity.Contact;
import com.purebred.sample.view.select.ContactSelect;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

@Component
@Scope("prototype")
public class RelatedContacts extends ToManyRelationship<Contact> {

    @Resource
    private RelatedContactsResults relatedContactsResults;

    @Override
    public String getEntityCaption() {
        return "Company Contacts";
    }

    @Override
    public RelatedContactsResults getResultsComponent() {
        return relatedContactsResults;
    }

    @Component
    @Scope("prototype")
    public static class RelatedContactsResults extends ToManyRelationshipResults<Contact> {

        @Resource
        private ContactDao contactDao;

        @Resource
        private ContactSelect contactSelect;

        @Resource
        private RelatedContactsQuery relatedContactsQuery;

        @Override
        public ContactDao getEntityDao() {
            return contactDao;
        }

        @Override
        public ContactSelect getEntitySelect() {
            return contactSelect;
        }

        @Override
        public ToManyRelationshipQuery getEntityQuery() {
            return relatedContactsQuery;
        }

        @Override
        public void configureFields(DisplayFields displayFields) {
            displayFields.setPropertyIds(new String[]{
                    "name",
                    "title",
                    "mailingAddress.state.code",
                    "mailingAddress.country",
                    "mainPhone"
            });

            displayFields.setLabel("mailingAddress.state.code", "State");
            displayFields.setLabel("mainPhone", "Phone");
            displayFields.setSortable("name", false);
            displayFields.setSortable("mainPhone", false);
            displayFields.setPropertyFormatter("mainPhone", new PhonePropertyFormatter());
        }

        @Override
        public String getParentPropertyId() {
            return "account";
        }

        @Override
        public String getEntityCaption() {
            return "Contacts";
        }
    }

    @Component
    @Scope("prototype")
    public static class RelatedContactsQuery extends ToManyRelationshipQuery<Contact, Account> {

        @Resource
        private ContactDao contactDao;

        private Account account;

        @Override
        public void setParent(Account account) {
            this.account = account;
        }

        @Override
        public Account getParent() {
            return account;
        }

        @Override
        public List<Contact> execute() {
            return contactDao.execute(this);
        }

        @Override
        public List<Predicate> buildCriteria(CriteriaBuilder builder, Root<Contact> rootEntity) {
            List<Predicate> criteria = new ArrayList<Predicate>();

            if (!isEmpty(account)) {
                ParameterExpression<Account> p = builder.parameter(Account.class, "account");
                criteria.add(builder.equal(rootEntity.get("account"), p));
            }

            return criteria;
        }

        @Override
        public void setParameters(TypedQuery typedQuery) {
            if (!isEmpty(account)) {
                typedQuery.setParameter("account", account);
            }
        }

        @Override
        public Path buildOrderBy(Root<Contact> rootEntity) {
            if (getOrderByPropertyId().equals("mailingAddress.country")) {
                return rootEntity.join("mailingAddress", JoinType.LEFT).join("country", JoinType.LEFT);
            } else if (getOrderByPropertyId().equals("mailingAddress.state.code")) {
                return rootEntity.join("mailingAddress", JoinType.LEFT).join("state", JoinType.LEFT).get("code");
            } else {
                return null;
            }
        }

        @Override
        public void addFetchJoins(Root<Contact> rootEntity) {
            rootEntity.fetch("mailingAddress", JoinType.LEFT);
            rootEntity.fetch("account", JoinType.LEFT);
        }

        @Override
        public String toString() {
            return "RelatedContacts{" +
                    "account='" + account + '\'' +
                    '}';
        }

    }
}

