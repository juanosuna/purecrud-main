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

/**
 * A structured query for finding entities that are related to a parent through a "to-many" relationship.
 *
 * @param <T> type of entity being queried
 * @param <P> type of the parent of the to-many relationship
 */
public abstract class ToManyRelationshipQuery<T, P> extends StructuredEntityQuery<T> {

    /**
     * Get the parent entity for the query.
     *
     * @return parent entity
     */
    public abstract P getParent();

    /**
     * Set the parent entity for the query.
     *
     * @param parent parent entity
     */
    public abstract void setParent(P parent);
}
