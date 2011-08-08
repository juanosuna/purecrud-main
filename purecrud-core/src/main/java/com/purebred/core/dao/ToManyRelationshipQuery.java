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
 * patents in process, and are protected by trade secret or copyrightlaw.
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

public abstract class ToManyRelationshipQuery<T, P> extends StructuredEntityQuery<T> {

    public abstract void setParent(P p);

    public abstract P getParent();

    public abstract List<Predicate> buildCriteria(CriteriaBuilder builder, Root<T> rootEntity);

    public abstract void setParameters(TypedQuery typedQuery);

    public abstract Path buildOrderBy(Root<T> rootEntity);

    public abstract void addFetchJoins(Root<T> rootEntity);

}
