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

import com.purebred.core.util.MethodDelegate;
import com.purebred.core.util.assertion.Assert;
import com.purebred.core.view.entity.EntityForm;
import com.purebred.core.view.entity.FormComponent;
import com.purebred.core.view.entity.LeftLabelGridLayout;
import com.purebred.core.view.entity.TopLabelGridLayout;
import com.vaadin.data.Validator;
import com.vaadin.terminal.ErrorMessage;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.AbstractTextField;
import com.vaadin.ui.Field;
import com.vaadin.ui.GridLayout;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

public class FormFields extends DisplayFields {

    private FormComponent form;
    private Map<String, AddRemoveMethodDelegate> optionalTabs = new HashMap<String, AddRemoveMethodDelegate>();

    public FormFields(FormComponent form) {
        super(form.getEntityType(), form.getEntityMessageSource(), form.getDefaultFormat());
        this.form = form;
    }

    public FormComponent getForm() {
        return form;
    }

    public int getColumns() {
        return getColumns("");
    }

    public int getColumns(String tabName) {
        int columns = 0;
        Collection<DisplayField> fields = getFields();
        for (DisplayField field : fields) {
            FormField formField = (FormField) field;
            if (formField.getTabName().equals(tabName)) {
                columns = Math.max(columns, formField.getColumnStart() - 1);
                if (formField.getColumnEnd() != null) {
                    columns = Math.max(columns, formField.getColumnEnd() - 1);
                }
            }
        }

        return ++columns;
    }

    public int getRows() {
        return getRows("");
    }

    public int getRows(String tabName) {
        int rows = 0;
        Collection<DisplayField> fields = getFields();
        for (DisplayField field : fields) {
            FormField formField = (FormField) field;
            if (formField.getTabName().equals(tabName)) {
                rows = Math.max(rows, formField.getRowStart() - 1);
                if (formField.getRowEnd() != null) {
                    rows = Math.max(rows, formField.getRowEnd() - 1);
                }
            }
        }

        return ++rows;
    }

    public GridLayout createGridLayout() {
        return createGridLayout(getFirstTabName());
    }

    public String getFirstTabName() {
        return getTabNames().iterator().next();
    }

    public GridLayout createGridLayout(String tabName) {
        GridLayout gridLayout;
        if (form instanceof EntityForm) {
            gridLayout = new LeftLabelGridLayout(getColumns(tabName), getRows(tabName));
        } else {
            gridLayout = new TopLabelGridLayout(getColumns(tabName), getRows(tabName));
        }
        gridLayout.setMargin(true, true, true, true);
        gridLayout.setSpacing(true);
        gridLayout.setSizeUndefined();

        return gridLayout;
    }

    @Override
    protected FormField createField(String propertyId) {
        return new FormField(this, propertyId);
    }

    public void setPosition(String tabName, String propertyId, int rowStart, int columnStart) {
        setPosition(tabName, propertyId, rowStart, columnStart, null, null);
    }

    public void setPosition(String propertyId, int rowStart, int columnStart) {
        setPosition(propertyId, rowStart, columnStart, null, null);
    }

    public void setPosition(String propertyId, int rowStart, int columnStart, Integer rowEnd, Integer columnEnd) {
        setPosition("", propertyId, rowStart, columnStart, rowEnd, columnEnd);
    }

    public void setPosition(String tabName, String propertyId, int rowStart, int columnStart, Integer rowEnd, Integer columnEnd) {
        Assert.PROGRAMMING.assertTrue(rowStart > 0,
                "rowStart arg must be greater than 0 for property " + propertyId + (tabName.isEmpty() ? "" : ", for tab " + tabName));
        Assert.PROGRAMMING.assertTrue(columnStart > 0,
                "columnStart arg must be greater than 0 for property " + propertyId + (tabName.isEmpty() ? "" : ", for tab " + tabName));

        FormField formField = (FormField) getField(propertyId);
        formField.setTabName(tabName);
        formField.setColumnStart(columnStart);
        formField.setRowStart(rowStart);
        formField.setColumnEnd(columnEnd);
        formField.setRowEnd(rowEnd);
    }

    public FormField findByField(Field field) {
        Collection<DisplayField> displayFields = getFields();
        for (DisplayField displayField : displayFields) {
            FormField formField = (FormField) displayField;
            if (formField.getField().equals(field)) {
                return formField;
            }
        }

        return null;
    }

    public AddRemoveMethodDelegate getTabAddRemoveDelegate(String tabName) {
        return optionalTabs.get(tabName);
    }

    public boolean isTabOptional(String tabName) {
        return optionalTabs.containsKey(tabName);
    }

    public void setTabOptional(String tabName, Object addTarget, String addMethod,
                               Object removeTarget, String removeMethod) {

        MethodDelegate addMethodDelegate = new MethodDelegate(addTarget, addMethod);
        MethodDelegate removeMethodDelegate = new MethodDelegate(removeTarget, removeMethod);
        AddRemoveMethodDelegate addRemoveMethodDelegate = new AddRemoveMethodDelegate(addMethodDelegate,
                removeMethodDelegate);

        optionalTabs.put(tabName, addRemoveMethodDelegate);
    }

    public FormField getFormField(String propertyId) {
        return (FormField) getField(propertyId);
    }

    public void setField(String propertyId, Field field) {
        FormField formField = (FormField) getField(propertyId);
        formField.setField(field);
    }

    public boolean containsPropertyId(String tabName, String propertyId) {
        return containsPropertyId(propertyId) && getFormField(propertyId).getTabName().equals(tabName);
    }

    public Set<FormField> getFormFields(String tabName) {
        Set<FormField> formFields = new HashSet<FormField>();
        Collection<DisplayField> displayFields = getFields();
        for (DisplayField displayField : displayFields) {
            FormField formField = (FormField) displayField;
            if (formField.getTabName().equals(tabName)) {
                formFields.add(formField);
            }
        }

        return formFields;
    }

    public Set<FormField> getFormFields() {
        Set<FormField> formFields = new HashSet<FormField>();
        Collection<DisplayField> displayFields = getFields();
        for (DisplayField displayField : displayFields) {
            FormField formField = (FormField) displayField;
            formFields.add(formField);
        }

        return formFields;
    }

    public void clearErrors(boolean clearConversionErrors) {
        Collection<DisplayField> fields = getFields();
        for (DisplayField field : fields) {
            FormField formField = (FormField) field;
            formField.clearError(clearConversionErrors);
        }
    }

    public void clearErrors(String tabName, boolean clearConversionErrors) {
        Set<FormField> formFields = getFormFields(tabName);
        for (FormField formField : formFields) {
            formField.clearError(clearConversionErrors);
        }
    }

    public boolean hasError(String tabName) {
        Set<FormField> formFields = getFormFields(tabName);
        for (FormField formField : formFields) {
            if (formField.hasError()) {
                return true;
            }
        }

        return false;
    }

    public Set<String> getTabNames() {
        Set<String> tabNames = new LinkedHashSet<String>();
        Collection<DisplayField> displayFields = getFields();
        for (DisplayField displayField : displayFields) {
            FormField formField = (FormField) displayField;
            tabNames.add(formField.getTabName());
        }

        return tabNames;
    }

    public String getLabel(String propertyId) {
        return ((FormField) getField(propertyId)).getFieldLabel().getValue().toString();
    }

    public void setLabel(String propertyId, String label) {
        ((FormField) getField(propertyId)).setFieldLabel(label);
    }

    public float getWidth(String propertyId) {
        return getFormField(propertyId).getWidth();
    }

    public void setWidth(String propertyId, float width, int unit) {
        getFormField(propertyId).setWidth(width, unit);
    }

    public void autoAdjustWidths() {
        Set<FormField> formFields = getFormFields();
        for (FormField formField : formFields) {
            if (formField.getField() instanceof AbstractTextField) {
                formField.autoAdjustWidth();
            }
        }
    }

    public void addValidator(String propertyId, Class<? extends Validator> validatorClass) {
        try {
            Constructor<? extends Validator> constructor = validatorClass.getConstructor(FormField.class);
            Validator validator = constructor.newInstance(getFormField(propertyId));
            getFormField(propertyId).addValidator(validator);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public String getDescription(String propertyId) {
        return getFormField(propertyId).getDescription();
    }

    public void setDescription(String propertyId, String description) {
        getFormField(propertyId).setDescription(description);
    }

    public void setSelectItems(String propertyId, List items) {
        getFormField(propertyId).setSelectItems(items);
    }

    public void setMultiSelectDimensions(String propertyId, int rows, int columns) {
        getFormField(propertyId).setMultiSelectDimensions(rows, columns);
    }

    public void setVisible(String propertyId, boolean isVisible) {
        getFormField(propertyId).setVisible(isVisible);
    }

    public void setRequired(String propertyId, boolean isRequired) {
        getFormField(propertyId).setRequired(isRequired);
    }

    public void setComponentError(String propertyId, ErrorMessage errorMessage) {
        Assert.PROGRAMMING.assertTrue(getFormField(propertyId).getField() instanceof AbstractComponent,
                "field is not of the right type");

        AbstractComponent abstractComponent = (AbstractComponent) getFormField(propertyId).getField();
        abstractComponent.setComponentError(errorMessage);
    }

    public void addValueChangeListener(String propertyId, Object target, String methodName) {
        FormField formField = (FormField) getField(propertyId);
        formField.addValueChangeListener(target, methodName);
    }

    public boolean isEntityForm() {
        return form instanceof EntityForm;
    }

    public static class AddRemoveMethodDelegate {
        private MethodDelegate addMethodDelegate;
        private MethodDelegate removeMethodDelegate;

        private AddRemoveMethodDelegate(MethodDelegate addMethodDelegate, MethodDelegate removeMethodDelegate) {
            this.addMethodDelegate = addMethodDelegate;
            this.removeMethodDelegate = removeMethodDelegate;
        }

        public MethodDelegate getAddMethodDelegate() {
            return addMethodDelegate;
        }

        public MethodDelegate getRemoveMethodDelegate() {
            return removeMethodDelegate;
        }
    }
}
