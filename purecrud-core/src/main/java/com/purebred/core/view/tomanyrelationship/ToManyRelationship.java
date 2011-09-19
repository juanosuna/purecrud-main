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

package com.purebred.core.view.tomanyrelationship;

import com.purebred.core.view.EntityComponent;

import javax.annotation.PostConstruct;

public abstract class ToManyRelationship<T> extends EntityComponent<T> {

    protected ToManyRelationship() {
        super();
    }

    public abstract ToManyRelationshipResults getResults();

    @PostConstruct
    @Override
    public void postConstruct() {
        super.postConstruct();

        addStyleName("p-to-many-relationship");

        addComponent(getResults());
    }

    @Override
    public void postWire() {
        super.postWire();

        getResults().postWire();
    }
}