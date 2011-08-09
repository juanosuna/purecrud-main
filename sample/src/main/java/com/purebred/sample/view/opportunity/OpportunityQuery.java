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

package com.purebred.sample.view.opportunity;

import com.purebred.core.dao.StructuredEntityQuery;
import com.purebred.sample.dao.OpportunityDao;
import com.purebred.sample.entity.Opportunity;
import com.purebred.sample.entity.SalesStage;
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
public class OpportunityQuery extends StructuredEntityQuery<Opportunity> {

    @Resource
    private OpportunityDao opportunityDao;

    private String accountName;
    private Set<SalesStage> salesStages = new HashSet<SalesStage>();

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public Set<SalesStage> getSalesStages() {
        return salesStages;
    }

    public void setSalesStages(Set<SalesStage> salesStages) {
        this.salesStages = salesStages;
    }

    @Override
    public List<Opportunity> execute() {
        return opportunityDao.execute(this);
    }

    @Override
    public List<Predicate> buildCriteria(CriteriaBuilder builder, Root<Opportunity> rootEntity) {
        List<Predicate> criteria = new ArrayList<Predicate>();

        if (!isEmpty(accountName)) {
            ParameterExpression<String> p = builder.parameter(String.class, "accountName");
            criteria.add(builder.like(builder.upper(rootEntity.get("account").<String>get("name")), p));
        }
        if (!isEmpty(salesStages)) {
            ParameterExpression<Set> p = builder.parameter(Set.class, "salesStages");
            criteria.add(builder.in(rootEntity.get("salesStage")).value(p));
        }

        return criteria;
    }

    @Override
    public void setParameters(TypedQuery typedQuery) {
        if (!isEmpty(accountName)) {
            typedQuery.setParameter("accountName", "%" + accountName.toUpperCase() + "%");
        }
        if (!isEmpty(salesStages)) {
            typedQuery.setParameter("salesStages", salesStages);
        }
    }

    @Override
    public Path buildOrderBy(Root<Opportunity> rootEntity) {
        if (getOrderByPropertyId().equals("account.name")) {
            return rootEntity.join("account", JoinType.LEFT).get("name");
        } if (getOrderByPropertyId().equals("amountWeightedInUSDFormatted")) {
            return rootEntity.get("amountWeightedInUSD");
        } else {
            return null;
        }
    }

    @Override
    public void addFetchJoins(Root<Opportunity> rootEntity) {
        rootEntity.fetch("account", JoinType.LEFT);
    }

    @Override
    public String toString() {
        return "OpportunityQuery{" +
                "accountName='" + accountName + '\'' +
                ", salesStages=" + salesStages +
                '}';
    }

}
