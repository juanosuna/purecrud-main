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
