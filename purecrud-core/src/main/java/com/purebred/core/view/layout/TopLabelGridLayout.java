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
import com.vaadin.ui.Field;
import com.vaadin.ui.VerticalLayout;

/**
 * User: Juan
 * Date: 7/27/11
 */
public class TopLabelGridLayout extends FormGridLayout {
    public TopLabelGridLayout(int columns, int rows) {
        super(columns, rows);
    }

    public TopLabelGridLayout() {
    }

    public void setFormColumns(int columns) {
        setColumns(columns);
    }

    public void addField(FormField formField) {
        VerticalLayout fieldLayout = (VerticalLayout) getComponent(formField.getColumnStart() - 1,
                formField.getRowStart() - 1);
        if (fieldLayout == null) {
            addFieldImpl(formField);
        } else {
            Field field = formField.getField();
            fieldLayout.addComponent(field);
        }
    }

    private void addFieldImpl(FormField formField) {

        VerticalLayout verticalLayout = new VerticalLayout();
//        verticalLayout.setSizeUndefined();
        verticalLayout.addComponent(formField.getFieldLabel());
        verticalLayout.addComponent(formField.getField());
        if (formField.getColumnEnd() != null && formField.getRowEnd() != null) {
            addComponent(verticalLayout, formField.getColumnStart() - 1, formField.getRowStart() - 1,
                    formField.getColumnEnd() - 1, formField.getRowEnd() - 1);
        } else {
            addComponent(verticalLayout, formField.getColumnStart() - 1, formField.getRowStart() - 1);
        }
    }

    public void removeField(FormField formField) {
        removeComponent(formField.getColumnStart() - 1, formField.getRowStart() - 1);
    }
}
