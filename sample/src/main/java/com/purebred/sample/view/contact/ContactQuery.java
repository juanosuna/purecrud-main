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

package com.purebred.sample.view.contact;

import com.purebred.core.dao.StructuredEntityQuery;
import com.purebred.sample.dao.ContactDao;
import com.purebred.sample.entity.Contact;
import com.purebred.sample.entity.Country;
import com.purebred.sample.entity.State;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
@Scope("prototype")
public class ContactQuery extends StructuredEntityQuery<Contact> {

    @Resource
    private ContactDao contactDao;

    private String lastName;
    private Set<State> states = new HashSet<State>();
    private Country country;

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Set<State> getStates() {
        return states;
    }

    public void setStates(Set<State> states) {
        this.states = states;
    }

    public Country getCountry() {
        return country;
    }

    public void setCountry(Country country) {
        this.country = country;
    }

    @Override
    public List<Contact> execute() {
        return contactDao.execute(this);
    }

    @Override
    public List<Predicate> buildCriteria(CriteriaBuilder builder, Root<Contact> rootEntity) {
        List<Predicate> criteria = new ArrayList<Predicate>();

        if (!isEmpty(lastName)) {
            ParameterExpression<String> p = builder.parameter(String.class, "lastName");
            criteria.add(builder.like(builder.upper(rootEntity.<String>get("lastName")), p));
        }
        if (!isEmpty(states)) {
            ParameterExpression<Set> p = builder.parameter(Set.class, "states");
            criteria.add(builder.in(rootEntity.get("mailingAddress").get("state")).value(p));
        }
        if (!isEmpty(country)) {
            ParameterExpression<Country> p = builder.parameter(Country.class, "country");
            criteria.add(builder.equal(rootEntity.get("mailingAddress").get("country"), p));
        }

        return criteria;
    }

    @Override
    public void setParameters(TypedQuery typedQuery) {
        if (!isEmpty(lastName)) {
            typedQuery.setParameter("lastName", "%" + lastName.toUpperCase() + "%");
        }
        if (!isEmpty(states)) {
            typedQuery.setParameter("states", states);
        }
        if (!isEmpty(country)) {
            typedQuery.setParameter("country", country);
        }
    }

    @Override
    public Path buildOrderBy(Root<Contact> rootEntity) {
        if (getOrderByPropertyId().equals("mailingAddress.country")) {
            return rootEntity.join("mailingAddress", JoinType.LEFT).join("country", JoinType.LEFT);
        } else if (getOrderByPropertyId().equals("mailingAddress.state.code")) {
            return rootEntity.join("mailingAddress", JoinType.LEFT).join("state", JoinType.LEFT).get("code");
        } else if (getOrderByPropertyId().equals("account.name")) {
            return rootEntity.join("account", JoinType.LEFT).get("name");
        } else {
            return null;
        }
    }

    @Override
    public void addFetchJoins(Root<Contact> rootEntity) {
        rootEntity.fetch("mailingAddress", JoinType.LEFT).fetch("state", JoinType.LEFT);
        rootEntity.fetch("account", JoinType.LEFT);
    }

    @Override
    public String toString() {
        return "ContactQuery{" +
                "lastName='" + lastName + '\'' +
                ", states=" + states +
                '}';
    }
}
