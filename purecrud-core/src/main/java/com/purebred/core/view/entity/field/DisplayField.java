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

package com.purebred.core.view.entity.field;

import com.purebred.core.util.BeanPropertyType;
import com.purebred.core.util.StringUtil;
import com.purebred.core.view.entity.EntityForm;
import com.purebred.core.view.entity.field.format.DefaultFormats;
import com.purebred.core.view.entity.field.format.JDKFormatPropertyFormatter;
import com.vaadin.data.util.PropertyFormatter;
import org.springframework.beans.BeanUtils;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.text.Format;

public class DisplayField {

    private DisplayFields displayFields;

    private String propertyId;
    private BeanPropertyType beanPropertyType;
    private FormLink formLink;
    private PropertyFormatter propertyFormatter;
    private boolean isSortable = true;
    private String columnHeader;

    public DisplayField(DisplayFields displayFields, String propertyId) {
        this.displayFields = displayFields;
        this.propertyId = propertyId;
        beanPropertyType = BeanPropertyType.getBeanPropertyType(getDisplayFields().getEntityType(), propertyId);
    }

    public DisplayFields getDisplayFields() {
        return displayFields;
    }

    public String getPropertyId() {
        return propertyId;
    }

    protected BeanPropertyType getBeanPropertyType() {
        return beanPropertyType;
    }

    public Class getPropertyType() {
        return beanPropertyType.getType();
    }

    public PropertyFormatter getPropertyFormatter() {
        if (propertyFormatter == null) {
            propertyFormatter = generateDefaultPropertyFormatter();
        }

        return propertyFormatter;
    }

    public void setPropertyFormatter(PropertyFormatter propertyFormatter) {
        this.propertyFormatter = propertyFormatter;
    }

    public void setFormat(Format format) {
        setPropertyFormatter(new JDKFormatPropertyFormatter(format));
    }

    public PropertyFormatter generateDefaultPropertyFormatter() {
        DefaultFormats defaultFormats = getDisplayFields().getDefaultFormats();

        if (getBeanPropertyType().getBusinessType() == BeanPropertyType.BusinessType.DATE) {
            return defaultFormats.getDateFormat();
        } else if (getBeanPropertyType().getBusinessType() == BeanPropertyType.BusinessType.DATE_TIME) {
            return defaultFormats.getDateTimeFormat();
        } else if (getBeanPropertyType().getBusinessType() == BeanPropertyType.BusinessType.NUMBER) {
            return defaultFormats.getNumberFormat();
        } else if (getBeanPropertyType().getBusinessType() == BeanPropertyType.BusinessType.MONEY) {
            return defaultFormats.getNumberFormat();
        }


        return defaultFormats.getEmptyFormat();
    }

    public boolean isSortable() {
        return isSortable;
    }

    public void setSortable(boolean sortable) {
        isSortable = sortable;
    }

    public String getLabel() {
        if (columnHeader == null) {
            columnHeader = generateLabelText();
        }

        return columnHeader;
    }

    public void setLabel(String columnHeader) {
        this.columnHeader = columnHeader;
    }

    protected String generateLabelText() {
        String labelText = getLabelTextFromMessageSource();
        if (labelText == null) {
            labelText = getLabelTextFromAnnotation();
        }
        if (labelText == null) {
            labelText = getLabelTextFromCode();
        }

        return labelText;
    }

    protected String getLabelSectionDisplayName() {
        return "Column";
    }

    private String getLabelTextFromMessageSource() {
        String fullPropertyPath = displayFields.getEntityType().getName() + "." + getPropertyId();
        return displayFields.getMessageSource().getMessage(fullPropertyPath);
    }

    private String getLabelTextFromAnnotation() {
        Class propertyContainerType = beanPropertyType.getContainerType();
        String propertyIdRelativeToContainerType = beanPropertyType.getId();
        PropertyDescriptor descriptor = BeanUtils.getPropertyDescriptor(propertyContainerType,
                propertyIdRelativeToContainerType);
        Method method = descriptor.getReadMethod();
        Label labelAnnotation = method.getAnnotation(Label.class);
        if (labelAnnotation == null) {
            return null;
        } else {
            return labelAnnotation.value();
        }
    }

    private String getLabelTextFromCode() {
        String afterPeriod = StringUtil.extractAfterPeriod(getPropertyId());
        return StringUtil.humanizeCamelCase(afterPeriod);
    }

    public void setFormLink(String propertyId, EntityForm entityForm) {
        formLink = new FormLink(propertyId, entityForm);
        entityForm.postWire();
    }

    public FormLink getFormLink() {
        return formLink;
    }

    public static class FormLink {
        private String propertyId;
        private EntityForm entityForm;

        private FormLink(String propertyId, EntityForm entityForm) {
            this.propertyId = propertyId;
            this.entityForm = entityForm;
        }

        public String getPropertyId() {
            return propertyId;
        }

        public EntityForm getEntityForm() {
            return entityForm;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DisplayField)) return false;

        DisplayField that = (DisplayField) o;

        if (!getPropertyId().equals(that.getPropertyId())) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return getPropertyId().hashCode();
    }

    @Override
    public String toString() {
        return "EntityField{" +
                "propertyId='" + getPropertyId() + '\'' +
                '}';
    }
}
