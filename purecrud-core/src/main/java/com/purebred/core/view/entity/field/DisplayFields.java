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

package com.purebred.core.view.entity.field;

import com.purebred.core.util.CollectionsUtil;
import com.purebred.core.view.MessageSource;
import com.purebred.core.view.entity.EntityForm;
import com.purebred.core.view.entity.field.format.DefaultFormats;
import com.vaadin.data.util.PropertyFormatter;

import java.util.*;

public class DisplayFields {

    private Class entityType;
    private MessageSource messageSource;
    private Map<String, DisplayField> fields = new LinkedHashMap<String, DisplayField>();
    private DefaultFormats defaultFormats;

    public DisplayFields(Class entityType, MessageSource messageSource, DefaultFormats defaultFormats) {
        this.entityType = entityType;
        this.messageSource = messageSource;
        this.defaultFormats = defaultFormats;
    }

    public void setPropertyIds(String[] propertyIds) {
        for (String propertyId : propertyIds) {
            DisplayField displayField = createField(propertyId);
            fields.put(propertyId, displayField);
        }
    }

    public Class getEntityType() {
        return entityType;
    }

    public DefaultFormats getDefaultFormats() {
        return defaultFormats;
    }

    public MessageSource getMessageSource() {
        return messageSource;
    }

    public List<String> getPropertyIds() {
        return new ArrayList(fields.keySet());
    }

    public String[] getPropertyIdsAsArray() {
        return CollectionsUtil.toStringArray(getPropertyIds());
    }

    public List<String> getLabels() {
        List<String> labels = new ArrayList<String>();
        List<String> propertyIds = getPropertyIds();
        for (String propertyId : propertyIds) {
            labels.add(getField(propertyId).getLabel());
        }

        return labels;
    }

    public String[] getLabelsAsArray() {
        return CollectionsUtil.toStringArray(getLabels());
    }

    public boolean containsPropertyId(String propertyId) {
        return fields.containsKey(propertyId);
    }

    public DisplayField getField(String propertyId) {
        if (!containsPropertyId(propertyId)) {
            DisplayField displayField = createField(propertyId);
            fields.put(propertyId, displayField);
        }

        return fields.get(propertyId);
    }

    protected DisplayField createField(String propertyId) {
        return new DisplayField(this, propertyId);
    }

    public Collection<DisplayField> getFields() {
        return fields.values();
    }

    public Set<String> getNonSortablePropertyIds() {
        Set<String> nonSortablePropertyIds = new HashSet<String>();
        for (DisplayField displayField : fields.values()) {
            if (!displayField.isSortable()) {
                nonSortablePropertyIds.add(displayField.getPropertyId());
            }
        }

        return nonSortablePropertyIds;
    }

    public void setLabel(String propertyId, String label) {
        getField(propertyId).setLabel(label);
    }

    public void setSortable(String propertyId, boolean isSortable) {
        getField(propertyId).setSortable(isSortable);
    }

    public void setFormLink(String propertyId, String entityPropertyId, EntityForm entityForm) {
        getField(propertyId).setFormLink(entityPropertyId, entityForm);
    }

    public void setPropertyFormatter(String propertyId, PropertyFormatter propertyFormatter) {
        getField(propertyId).setPropertyFormatter(propertyFormatter);
    }
}
