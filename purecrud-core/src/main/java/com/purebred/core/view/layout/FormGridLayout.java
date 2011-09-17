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
import com.vaadin.ui.GridLayout;

public abstract class FormGridLayout extends GridLayout {
    public FormGridLayout(int columns, int rows) {
        super(columns, rows);
    }

    public FormGridLayout() {
    }

    public abstract void setFormColumns(int columns);

    public abstract void addField(FormField formField);

    public abstract void removeField(FormField formField);
}
