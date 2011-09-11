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

package com.purebred.core.dao;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.List;

/**
 * A query structured to be re-executable as criteria, sort criteria and paging changes. Subclass
 * needs to implement methods for specifying criteria, parameters, sorting and fetch joins.
 *
 * @see EntityDao#execute(StructuredEntityQuery)
 *
 * @param <T> type of entity being queried
 */
public abstract class StructuredEntityQuery<T> extends EntityQuery<T> {

    /**
     * Build query criteria.
     *
     * @param builder used by implementation to build criteria
     * @param rootEntity root type in the from clause
     * @return a list of predicates, one for every part of the criteria
     */
    public abstract List<Predicate> buildCriteria(CriteriaBuilder builder, Root<T> rootEntity);

    /**
     * Set the parameter values for the query. The parameter names should matched those defined by the buildCriteria
     * implementation.
     *
     * @param typedQuery interface for setting parameters
     */
    public abstract void setParameters(TypedQuery typedQuery);

    /**
     * Build the Path used for sorting.
     *
     * @param rootEntity root type in the from clause
     * @return path used for sorting
     */
    public abstract Path buildOrderBy(Root<T> rootEntity);

    /**
     * Adding any fetch joins required to improve performance, i.e. to avoid N+1 select problem
     *
     * @param rootEntity root type in the from clause
     */
    public abstract void addFetchJoins(Root<T> rootEntity);
}
