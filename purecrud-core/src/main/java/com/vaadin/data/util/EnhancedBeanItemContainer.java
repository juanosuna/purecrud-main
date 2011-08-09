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

package com.vaadin.data.util;

import com.purebred.core.view.entity.field.DisplayFields;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

public class EnhancedBeanItemContainer<BEANTYPE> extends BeanItemContainer<BEANTYPE> {
    private Class beanType;
    private Set<String> nonSortablePropertyIds = new HashSet<String>();
    private DisplayFields displayFields;

    public EnhancedBeanItemContainer(Class<? super BEANTYPE> type, DisplayFields displayFields)
            throws IllegalArgumentException {
        super(type);
        beanType = type;
        this.displayFields = displayFields;
    }

    public EnhancedBeanItemContainer(Class<? super BEANTYPE> type, Collection<? extends BEANTYPE> beantypes, DisplayFields displayFields)
            throws IllegalArgumentException {
        super(type, beantypes);
        beanType = type;
        this.displayFields = displayFields;
    }

    @Override
    public boolean addNestedContainerProperty(String propertyId) {
        return addContainerProperty(propertyId, new EnhancedNestedPropertyDescriptor(
                propertyId, beanType, displayFields.getField(propertyId)));
    }

    public Set<String> getNonSortablePropertyIds() {
        return nonSortablePropertyIds;
    }

    public void setNonSortablePropertyIds(Set<String> nonSortablePropertyIds) {
        this.nonSortablePropertyIds = nonSortablePropertyIds;
    }

    @Override
    public Collection<?> getSortableContainerPropertyIds() {
        LinkedList<Object> sortables = new LinkedList<Object>();
        for (Object propertyId : getContainerPropertyIds()) {
            if (!nonSortablePropertyIds.contains(propertyId)) {
                sortables.add(propertyId);
            }
        }
        return sortables;
    }
}
