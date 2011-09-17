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

package com.purebred.core.view.layout;


import com.purebred.core.view.field.FormField;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Field;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;

/**
 * User: Juan
 * Date: 7/27/11
 */
public class LeftLabelGridLayout extends FormGridLayout {

    public LeftLabelGridLayout(int columns, int rows) {
        super(columns * 3, rows);
    }

    public LeftLabelGridLayout() {
    }

    public void setFormColumns(int columns) {
        setColumns(columns * 3);
    }

    public int getLabelColumn(FormField formField) {
        return (formField.getColumnStart() - 1) * 3;
    }

    public int getFieldColumn(FormField formField) {
        return getLabelColumn(formField) + 1;
    }

    public int getSpacerColumn(FormField formField) {
        if (formField.getColumnEnd() == null) {
            return getFieldColumn(formField) + 1;
        } else {
            return getColumnEnd(formField) + 1;
        }
    }

    public Integer getColumnEnd(FormField formField) {
        if (formField.getColumnEnd() == null) {
            return null;
        } else {
            int diff = formField.getColumnEnd() - formField.getColumnStart();
            return getFieldColumn(formField) + diff * 3;
        }
    }

    public int getRowStart(FormField formField) {
        return formField.getRowStart() - 1;
    }

    public int getRowEnd(FormField formField) {
        return formField.getRowEnd() - 1;
    }

    public void addField(FormField formField) {
        HorizontalLayout fieldLayout = (HorizontalLayout) getComponent(getFieldColumn(formField), getRowStart(formField));
        if (fieldLayout == null) {
            addFieldImpl(formField);
        } else {
            if (formField.getFieldLabel().getValue() != null) {
                Label label = formField.getFieldLabel();
                fieldLayout.addComponent(label);
                Label spacer = new Label();
                spacer.setWidth("1em");
                fieldLayout.addComponent(spacer);
            }

            Field field = formField.getField();
            fieldLayout.addComponent(field);
            Label spacer = new Label();
            spacer.setWidth("1em");
            fieldLayout.addComponent(spacer);
        }
    }

    private void addFieldImpl(FormField formField) {
        Label label = formField.getFieldLabel();

        HorizontalLayout fieldLayout = new HorizontalLayout();
        fieldLayout.setSizeUndefined();
        Field field = formField.getField();
        fieldLayout.addComponent(field);

        Label spacer = new Label();
        spacer.setWidth("1em");

        if (formField.getColumnEnd() != null && formField.getRowEnd() != null) {
            addComponent(label, getLabelColumn(formField), getRowStart(formField),
                    getLabelColumn(formField), getRowEnd(formField));

            addComponent(fieldLayout, getFieldColumn(formField), getRowStart(formField),
                    getColumnEnd(formField), getRowEnd(formField));

            addComponent(spacer, getSpacerColumn(formField), getRowStart(formField),
                    getSpacerColumn(formField), getRowEnd(formField));
        } else {
            addComponent(label, getLabelColumn(formField), getRowStart(formField));

            addComponent(fieldLayout, getFieldColumn(formField), getRowStart(formField));

            addComponent(spacer, getSpacerColumn(formField), getRowStart(formField));
        }
        setComponentAlignment(fieldLayout, Alignment.TOP_LEFT);
        setComponentAlignment(label, Alignment.TOP_RIGHT);
        setComponentAlignment(spacer, Alignment.TOP_LEFT);
    }

    public void removeField(FormField formField) {
        removeComponent(getLabelColumn(formField), getRowStart(formField));
        removeComponent(getFieldColumn(formField), getRowStart(formField));
        removeComponent(getSpacerColumn(formField), getRowStart(formField));
    }
}
