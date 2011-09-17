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

package com.purebred.core.view.field;

import com.purebred.core.view.EntityForm;
import com.purebred.core.view.util.MessageSource;
import com.purebred.core.view.entityselect.EntitySelect;
import com.vaadin.data.Property;
import com.vaadin.data.Validator;
import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TextField;
import org.apache.commons.beanutils.PropertyUtils;
import org.vaadin.addon.customfield.CustomField;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;

public class SelectField extends CustomField {

    private MessageSource uiMessageSource;

    private TextField field;
    private EntitySelect entitySelect;

    private Button clearButton;
    private Button searchButton;

    private EntityForm entityForm;
    private String propertyId;

    public SelectField(EntityForm entityForm, String propertyId, EntitySelect entitySelect) {
        this.entityForm = entityForm;
        this.propertyId = propertyId;
        this.entitySelect = entitySelect;
        this.uiMessageSource = entityForm.getUiMessageSource();
        initialize();
    }

    public EntitySelect getEntitySelect() {
        return entitySelect;
    }

    public void setButtonVisible(boolean isVisible) {
        clearButton.setVisible(isVisible);
        searchButton.setVisible(isVisible);
    }

    public void initialize() {
        setSizeUndefined();
        field = new TextField();
        FormField.initAbstractFieldDefaults(field);
        FormField.initTextFieldDefaults(field);
        field.setReadOnly(true);

        HorizontalLayout layout = new HorizontalLayout();
        layout.addComponent(field);

        searchButton = new Button();
        searchButton.setDescription(uiMessageSource.getMessage("selectField.search.description"));
        searchButton.setSizeUndefined();
        searchButton.addStyleName("borderless");
        searchButton.setIcon(new ThemeResource("../chameleon/img/magnifier.png"));
        searchButton.addListener(new Button.ClickListener() {
            public void buttonClick(Button.ClickEvent event) {
                entitySelect.open();
            }
        });
        layout.addComponent(searchButton);

        clearButton = new Button();
        clearButton.setDescription(uiMessageSource.getMessage("selectField.clear.description"));
        clearButton.setSizeUndefined();
        clearButton.addStyleName("borderless");
        clearButton.setIcon(new ThemeResource("../runo/icons/16/cancel.png"));
        layout.addComponent(clearButton);

        entitySelect.getResults().setSelectButtonListener(this, "itemSelected");
        addClearListener(this, "itemCleared");

        setCompositionRoot(layout);
    }

    public void itemSelected() {
        Object selectedValue = getSelectedValue();
        Object entity = entityForm.getEntity();
        try {
            PropertyUtils.setProperty(entity, propertyId, selectedValue);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }

        Property property = field.getPropertyDataSource();
        field.setPropertyDataSource(property);
        entitySelect.close();
    }

    public void itemCleared() {
        Object entity = entityForm.getEntity();
        try {
            PropertyUtils.setProperty(entity, propertyId, null);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }

        Property property = field.getPropertyDataSource();
        field.setPropertyDataSource(property);
    }

    public void addClearListener(Object target, String methodName) {
        clearButton.addListener(Button.ClickEvent.class, target, methodName);
    }

    public Object getSelectedValue() {
        return entitySelect.getResults().getSelectedValue();
    }

    public String getRequiredError() {
        return field.getRequiredError();
    }

    public boolean isRequired() {
        return field.isRequired();
    }

    public void setRequired(boolean required) {
        field.setRequired(required);
    }

    public void setRequiredError(String requiredMessage) {
        field.setRequiredError(requiredMessage);
    }

    public boolean isInvalidCommitted() {
        return field.isInvalidCommitted();
    }

    public void setInvalidCommitted(boolean isCommitted) {
        field.setInvalidCommitted(isCommitted);
    }

    public void commit() throws SourceException, Validator.InvalidValueException {
//        field.commit();
    }

    public void discard() throws SourceException {
        field.discard();
    }

    public boolean isModified() {
        return field.isModified();
    }

    public boolean isReadThrough() {
        return field.isReadThrough();
    }

    public boolean isWriteThrough() {
        return field.isWriteThrough();
    }

    public void setReadThrough(boolean readThrough) throws SourceException {
        field.setReadThrough(readThrough);
    }

    public void setWriteThrough(boolean writeThrough) throws SourceException,
            Validator.InvalidValueException {
        field.setWriteThrough(writeThrough);
    }

    public void addValidator(Validator validator) {
        field.addValidator(validator);
    }

    public Collection<Validator> getValidators() {
        return field.getValidators();
    }

    public boolean isInvalidAllowed() {
        return field.isInvalidAllowed();
    }

    public boolean isValid() {
        return field.isValid();
    }

    public void removeValidator(Validator validator) {
        field.removeValidator(validator);

    }

    public void setInvalidAllowed(boolean invalidValueAllowed)
            throws UnsupportedOperationException {
        field.setInvalidAllowed(invalidValueAllowed);
    }

    public void validate() throws Validator.InvalidValueException {
        field.validate();
    }

    public Class<?> getType() {
        return field.getType();
    }

    public void setValue(Object newValue) throws ReadOnlyException,
            ConversionException {
        field.setValue(newValue);
    }

    public void addListener(ValueChangeListener listener) {
        field.addListener(listener);
    }

    public void removeListener(ValueChangeListener listener) {
        field.removeListener(listener);
    }

    public void valueChange(com.vaadin.data.Property.ValueChangeEvent event) {
        field.valueChange(event);
    }

    public Property getPropertyDataSource() {
        return field.getPropertyDataSource();
    }

    public void setPropertyDataSource(Property newDataSource) {
        field.setPropertyDataSource(newDataSource);

    }

    public void focus() {
        field.focus();
    }

    public int getTabIndex() {
        return field.getTabIndex();
    }

    public void setTabIndex(int tabIndex) {
        field.setTabIndex(tabIndex);
    }

    public Object getValue() {
        return field.getValue();
    }
}
