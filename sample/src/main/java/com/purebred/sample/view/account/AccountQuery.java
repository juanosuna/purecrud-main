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

package com.purebred.sample.view.account;

import com.purebred.core.dao.StructuredEntityQuery;
import com.purebred.sample.dao.AccountDao;
import com.purebred.sample.entity.Account;
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
public class AccountQuery extends StructuredEntityQuery<Account> {

    @Resource
    private AccountDao accountDao;

    private String name;
    private Set<State> states = new HashSet<State>();
    private Country country;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
    public List<Account> execute() {
        return accountDao.execute(this);
    }

    @Override
    public List<Predicate> buildCriteria(CriteriaBuilder builder, Root<Account> rootEntity) {
        List<Predicate> criteria = new ArrayList<Predicate>();

        if (!isEmpty(name)) {
            ParameterExpression<String> p = builder.parameter(String.class, "name");
            criteria.add(builder.like(builder.upper(rootEntity.<String>get("name")), p));
        }
        if (!isEmpty(states)) {
            ParameterExpression<Set> p = builder.parameter(Set.class, "states");
            criteria.add(builder.in(rootEntity.get("billingAddress").get("state")).value(p));
        }
        if (!isEmpty(country)) {
            ParameterExpression<Country> p = builder.parameter(Country.class, "country");
            criteria.add(builder.equal(rootEntity.get("billingAddress").get("country"), p));
        }

        return criteria;
    }

    @Override
    public void setParameters(TypedQuery typedQuery) {
        if (!isEmpty(name)) {
            typedQuery.setParameter("name", "%" + name.toUpperCase() + "%");
        }
        if (!isEmpty(states)) {
            typedQuery.setParameter("states", states);
        }
        if (!isEmpty(country)) {
            typedQuery.setParameter("country", country);
        }
    }

    @Override
    public Path buildOrderBy(Root<Account> rootEntity) {
        if (getOrderByPropertyId().equals("billingAddress.country")) {
            return rootEntity.join("billingAddress", JoinType.LEFT).join("country", JoinType.LEFT);
        } else if (getOrderByPropertyId().equals("billingAddress.state.code")) {
            return rootEntity.join("billingAddress", JoinType.LEFT).join("state", JoinType.LEFT).get("code");
        } else if (getOrderByPropertyId().equals("annualRevenueInUSDFormatted")) {
            return rootEntity.get("annualRevenueInUSD");
        } else {
            return null;
        }
    }

    @Override
    public void addFetchJoins(Root<Account> rootEntity) {
        rootEntity.fetch("billingAddress", JoinType.LEFT);
    }

    @Override
    public String toString() {
        return "AccountQuery{" +
                "name='" + name + '\'' +
                ", states=" + states +
                '}';
    }
}
