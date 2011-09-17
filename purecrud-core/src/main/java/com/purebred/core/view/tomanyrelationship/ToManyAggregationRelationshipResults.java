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

import com.purebred.core.view.entityselect.EntitySelect;

import javax.annotation.PostConstruct;
import java.util.Collection;

public abstract class ToManyAggregationRelationshipResults<T> extends ToManyRelationshipResults<T> {

    public abstract EntitySelect<T> getEntitySelect();

    @PostConstruct
    @Override
    public void postConstruct() {
        super.postConstruct();

        getEntitySelect().getResults().getResultsTable().setMultiSelect(true);
    }

    @Override
    public void postWire() {
        super.postWire();
        getEntitySelect().postWire();
        getEntitySelect().getResults().setSelectButtonListener(this, "itemsSelected");
    }

    @Override
    public void add() {
        getEntitySelect().open();
    }

    public void itemsSelected() {
        getEntitySelect().close();
        Collection<T> selectedValues = getEntitySelect().getResults().getSelectedValues();
        setReferencesToParentAndPersist((T[]) selectedValues.toArray());
    }
}
