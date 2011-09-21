/*
 * Copyright (c) 2011 Brown Bag Consulting.
 * This file is part of the PureCRUD project.
 * Author: Juan Osuna
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License Version 3
 * as published by the Free Software Foundation with the addition of the
 * following permission added to Section 15 as permitted in Section 7(a):
 * FOR ANY PART OF THE COVERED WORK IN WHICH THE COPYRIGHT IS OWNED BY
 * Brown Bag Consulting, Brown Bag Consulting DISCLAIMS THE WARRANTY OF
 * NON INFRINGEMENT OF THIRD PARTY RIGHTS.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 * The interactive user interfaces in modified source and object code versions
 * of this program must display Appropriate Legal Notices, as required under
 * Section 5 of the GNU Affero General Public License.
 *
 * You can be released from the requirements of the license by purchasing
 * a commercial license. Buying such a license is mandatory as soon as you
 * develop commercial activities involving the PureCRUD software without
 * disclosing the source code of your own applications. These activities
 * include: offering paid services to customers as an ASP, providing
 * services from a hosted web application, shipping PureCRUD with a closed
 * source product.
 *
 * For more information, please contact Brown Bag Consulting at this
 * address: juan@brownbagconsulting.com.
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
