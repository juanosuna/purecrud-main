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

import com.purebred.core.dao.EntityDao;
import com.purebred.core.entity.ReferenceEntity;
import com.purebred.core.util.*;
import com.purebred.core.util.assertion.Assert;
import com.purebred.core.validation.NumberConversionValidator;
import com.purebred.core.view.entity.EntityForm;
import com.purebred.core.view.entity.field.format.EmptyPropertyFormatter;
import com.vaadin.addon.beanvalidation.BeanValidationValidator;
import com.vaadin.data.Property;
import com.vaadin.data.Validator;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.data.util.PropertyFormatter;
import com.vaadin.terminal.CompositeErrorMessage;
import com.vaadin.terminal.ErrorMessage;
import com.vaadin.terminal.Sizeable;
import com.vaadin.ui.*;

import javax.persistence.Lob;
import java.util.*;

public class FormField extends DisplayField {
    public static final String DEFAULT_DISPLAY_PROPERTY_ID = "displayName";
    public static final Integer DEFAULT_TEXT_FIELD_WIDTH = 11;

    private String tabName = "";
    private Field field;
    private Integer columnStart;
    private Integer rowStart;
    private Integer columnEnd;
    private Integer rowEnd;
    private boolean isRequired;
    private com.vaadin.ui.Label label;
    private AutoAdjustWidthMode autoAdjustWidthMode = AutoAdjustWidthMode.PARTIAL;
    private Integer defaultWidth;
    private boolean hasConversionError;

    public FormField(FormFields formFields, String propertyId) {
        super(formFields, propertyId);
    }

    public com.vaadin.ui.Label getFieldLabel() {
        if (label == null) {
            String labelText = generateLabelText();
            if (isRequired()) {
                labelText = "<span class=\"p-required-field-indicator\">*</span>" + labelText;
            }
            label = new com.vaadin.ui.Label(labelText, com.vaadin.ui.Label.CONTENT_XHTML);
            label.setSizeUndefined();
        }

        return label;
    }

    public void setFieldLabel(String labelText) {
        getFieldLabel().setValue(labelText);
    }

    public String getTabName() {
        return tabName;
    }

    public void setTabName(String tabName) {
        this.tabName = tabName;
    }

    public Integer getColumnStart() {
        return columnStart;
    }

    public void setColumnStart(Integer columnStart) {
        this.columnStart = columnStart;
    }

    public Integer getRowStart() {
        return rowStart;
    }

    public void setRowStart(Integer rowStart) {
        this.rowStart = rowStart;
    }

    public Integer getColumnEnd() {
        return columnEnd;
    }

    public void setColumnEnd(Integer columnEnd) {
        this.columnEnd = columnEnd;
    }

    public Integer getRowEnd() {
        return rowEnd;
    }

    public void setRowEnd(Integer rowEnd) {
        this.rowEnd = rowEnd;
    }

    public Field getField() {
        if (field == null) {
            field = generateField();
            initializeFieldDefaults();
        }

        return field;
    }

    public void setField(Field field) {
        setField(field, true);
    }

    public void setField(Field field, boolean initializeDefaults) {
        this.field = field;
        if (initializeDefaults) {
            initializeFieldDefaults();
        }
    }

    public void initWidthAndMaxLengthDefaults(AbstractTextField abstractTextField) {
        defaultWidth = MathUtil.maxIgnoreNull(DEFAULT_TEXT_FIELD_WIDTH, getBeanPropertyType().getMinimumLength());
        abstractTextField.setWidth(defaultWidth, Sizeable.UNITS_EM);

        Integer maxWidth = getBeanPropertyType().getMaximumLength();
        if (maxWidth != null) {
            abstractTextField.setMaxLength(maxWidth);
        }
    }

    public AutoAdjustWidthMode getAutoAdjustWidthMode() {
        return autoAdjustWidthMode;
    }

    public void setAutoAdjustWidthMode(AutoAdjustWidthMode autoAdjustWidthMode) {
        this.autoAdjustWidthMode = autoAdjustWidthMode;
    }

    public void autoAdjustWidth() {
        Assert.PROGRAMMING.assertTrue(getField() instanceof AbstractTextField,
                "FormField.autoAdjustWidth can only be called on text fields");

        if (autoAdjustWidthMode == AutoAdjustWidthMode.NONE) return;

        Object value = getField().getPropertyDataSource().getValue();
        if (value != null) {
            AbstractTextField textField = (AbstractTextField) getField();
            int approximateWidth = StringUtil.approximateColumnWidth(value.toString());
            if (autoAdjustWidthMode == AutoAdjustWidthMode.FULL) {
                textField.setWidth(approximateWidth, Sizeable.UNITS_EM);
            } else if (autoAdjustWidthMode == AutoAdjustWidthMode.PARTIAL) {
                textField.setWidth(MathUtil.maxIgnoreNull(approximateWidth, defaultWidth), Sizeable.UNITS_EM);
            }
        }
    }

    public float getWidth() {
        return getField().getWidth();
    }

    public void setWidth(float width, int unit) {
        getField().setWidth(width, unit);
    }

    public void setSelectItems(List items) {
        // could be either collection or single item
        Object selectedItems = getSelectedItems();

        Field field = getField();
        Assert.PROGRAMMING.assertTrue(field instanceof AbstractSelect,
                "property " + getPropertyId() + " is not a AbstractSelect field");
        AbstractSelect selectField = (AbstractSelect) field;
        if (selectField.getContainerDataSource() == null
                || !(selectField.getContainerDataSource() instanceof BeanItemContainer)) {
            BeanItemContainer container;
            if (getBeanPropertyType().isCollectionType()) {
                container = new BeanItemContainer(getBeanPropertyType().getCollectionValueType(), items);
            } else {
                container = new BeanItemContainer(getPropertyType(), items);
            }

            selectField.setContainerDataSource(container);
        } else {
            BeanItemContainer container = (BeanItemContainer) selectField.getContainerDataSource();
            container.removeAllItems();
            container.addAll(items);

            if (!getBeanPropertyType().isCollectionType() && !container.containsId(selectedItems)) {
                selectField.select(selectField.getNullSelectionItemId());
            }
        }
    }

    public void setMultiSelectDimensions(int rows, int columns) {
        Field field = getField();
        Assert.PROGRAMMING.assertTrue(field instanceof ListSelect,
                "property " + getPropertyId() + " is not a AbstractSelect field");
        ListSelect selectField = (ListSelect) field;
        selectField.setRows(rows);
        selectField.setColumns(columns);
    }

    public void setDisplayPropertyId(String displayPropertyId) {
        Assert.PROGRAMMING.assertTrue(field instanceof AbstractSelect,
                "property " + getPropertyId() + " is not a Select field");

        ((AbstractSelect) field).setItemCaptionPropertyId(displayPropertyId);
    }

    public Object getSelectedItems() {
        Field field = getField();
        Assert.PROGRAMMING.assertTrue(field instanceof AbstractSelect,
                "property " + getPropertyId() + " is not a AbstractSelect field");
        AbstractSelect selectField = (AbstractSelect) field;
        return selectField.getValue();
    }

    public void addValueChangeListener(Object target, String methodName) {
        AbstractComponent component = (AbstractComponent) getField();
        component.addListener(Property.ValueChangeEvent.class, target, methodName);
    }

    public FormFields getFormFields() {
        return (FormFields) getDisplayFields();
    }

    public void setVisible(boolean isVisible) {
        getField().setVisible(isVisible);
        getFieldLabel().setVisible(isVisible);
    }

    public void setRequired(boolean isRequired) {
        getField().setRequired(isRequired);
    }

    public void disableIsRequired() {
        getField().setRequired(false);
    }

    public boolean isRequired() {
        return getField().isRequired();
    }

    public void restoreIsRequired() {
        getField().setRequired(isRequired);
    }

    public String getDescription() {
        return getField().getDescription();
    }

    public void setDescription(String description) {
        getField().setDescription(description);
    }

    public boolean hasError() {
        if (hasConversionError) {
            return true;
        } else if (getField() instanceof AbstractComponent) {
            AbstractComponent abstractComponent = (AbstractComponent) getField();
            return abstractComponent.getComponentError() != null || hasIsRequiredError();
        } else {
            return false;
        }
    }

    @Override
    public PropertyFormatter getPropertyFormatter() {
        if (getField() instanceof AbstractTextField) {
            return super.getPropertyFormatter();
        } else {
            return new EmptyPropertyFormatter();
        }
    }

    public boolean hasIsRequiredError() {
        return getField().isRequired() && StringUtil.isEmpty(getField().getValue());
    }

    public void clearError(boolean clearConversionError) {
        if (clearConversionError) {
            hasConversionError = false;
        }
        if (getField() instanceof AbstractComponent) {
            AbstractComponent abstractComponent = (AbstractComponent) getField();
            abstractComponent.setComponentError(null);
        }
    }

    public void addError(ErrorMessage errorMessage) {
        Assert.PROGRAMMING.assertTrue(getField() instanceof AbstractComponent,
                "Error message cannot be added to field that is not an AbstractComponent");

        AbstractComponent abstractComponent = (AbstractComponent) getField();
        ErrorMessage existingErrorMessage = abstractComponent.getComponentError();
        if (existingErrorMessage == null) {
            abstractComponent.setComponentError(errorMessage);
        } else if (existingErrorMessage instanceof CompositeErrorMessage) {
            CompositeErrorMessage existingCompositeErrorMessage = (CompositeErrorMessage) existingErrorMessage;
            Iterator<ErrorMessage> iterator = existingCompositeErrorMessage.iterator();
            Set<ErrorMessage> newErrorMessages = new LinkedHashSet<ErrorMessage>();
            while (iterator.hasNext()) {
                ErrorMessage next = iterator.next();
                newErrorMessages.add(next);
            }
            newErrorMessages.add(errorMessage);
            CompositeErrorMessage newCompositeErrorMessage = new CompositeErrorMessage(newErrorMessages);
            abstractComponent.setComponentError(newCompositeErrorMessage);
        } else {
            Set<ErrorMessage> newErrorMessages = new LinkedHashSet<ErrorMessage>();
            newErrorMessages.add(existingErrorMessage);
            newErrorMessages.add(errorMessage);
            CompositeErrorMessage newCompositeErrorMessage = new CompositeErrorMessage(newErrorMessages);
            abstractComponent.setComponentError(newCompositeErrorMessage);
        }
    }

    private Field generateField() {
        Class propertyType = getPropertyType();

        if (propertyType == null) {
            return null;
        }

        if (Date.class.isAssignableFrom(propertyType)) {
            return new DateField();
        }

        if (boolean.class.isAssignableFrom(propertyType) || Boolean.class.isAssignableFrom(propertyType)) {
            return new CheckBox();
        }

        if (ReferenceEntity.class.isAssignableFrom(propertyType)) {
            return new Select();
        }

        if (Currency.class.isAssignableFrom(propertyType)) {
            return new Select();
        }

        if (propertyType.isEnum()) {
            return new Select();
        }

        if (Collection.class.isAssignableFrom(propertyType)) {
            return new ListSelect();
        }

        if (getBeanPropertyType().hasAnnotation(Lob.class)) {
            return new RichTextArea();
        }

        return new TextField();
    }

    private void initializeFieldDefaults() {
        if (field == null) {
            return;
        }

//        field.setCaption(getLabel());
        field.setInvalidAllowed(true);

        if (field instanceof AbstractField) {
            initAbstractFieldDefaults((AbstractField) field);
        }

        if (field instanceof AbstractTextField) {
            initTextFieldDefaults((AbstractTextField) field);
            initWidthAndMaxLengthDefaults((AbstractTextField) field);
        }

        if (field instanceof RichTextArea) {
            initRichTextFieldDefaults((RichTextArea) field);
        }

        if (field instanceof DateField) {
            initDateFieldDefaults((DateField) field);
        }

        if (field instanceof AbstractSelect) {
            initAbstractSelectDefaults((AbstractSelect) field);

            if (field instanceof Select) {
                initSelectDefaults((Select) field);
            }

            if (field instanceof ListSelect) {
                initListSelectDefaults((ListSelect) field);
            }

            Class valueType = getPropertyType();
            if (getBeanPropertyType().isCollectionType()) {
                valueType = getBeanPropertyType().getCollectionValueType();
            }

            List referenceEntities;
            if (Currency.class.isAssignableFrom(valueType)) {
                referenceEntities = CurrencyUtil.getAvailableCurrencies();
                ((AbstractSelect) field).setItemCaptionPropertyId("currencyCode");
            } else if (valueType.isEnum()) {
                Object[] enumConstants = valueType.getEnumConstants();
                referenceEntities = Arrays.asList(enumConstants);
            } else {
                EntityDao propertyDao = SpringApplicationContext.getBeanByTypeAndGenericArgumentType(EntityDao.class,
                        valueType);
                referenceEntities = propertyDao.findAll();
            }
            setSelectItems(referenceEntities);
        }


        if (getFormFields().isEntityForm()) {
            if (getBeanPropertyType().isValidatable()) {
                initializeIsRequired();
                initializeValidators();
            }

            field.addListener(new FieldValueChangeListener());
        }
    }

    private void initializeValidators() {
        if (field instanceof AbstractTextField) {
            if (getBeanPropertyType().getBusinessType() != null &&
                    getBeanPropertyType().getBusinessType().equals(BeanPropertyType.BusinessType.NUMBER)) {
                addValidator(new NumberConversionValidator(this, "Invalid number"));
            }
        }
    }

    public void addValidator(Validator validator) {
        getField().addValidator(validator);
    }

    public class FieldValueChangeListener implements Property.ValueChangeListener {

        @Override
        public void valueChange(Property.ValueChangeEvent event) {
            EntityForm entityForm = (EntityForm) getFormFields().getForm();

            if (entityForm.isValidationEnabled()) {
                entityForm.validate(false);
            }
        }
    }

    public boolean isHasConversionError() {
        return hasConversionError;
    }

    public void setHasConversionError(boolean hasConversionError) {
        this.hasConversionError = hasConversionError;
    }

    private void initializeIsRequired() {
        BeanValidationValidator validator = new BeanValidationValidator(getBeanPropertyType().getContainerType(),
                getBeanPropertyType().getId());
        if (validator.isRequired()) {
            field.setRequired(true);
            field.setRequiredError(validator.getRequiredMessage());
        }

        isRequired = field.isRequired();
    }

    public static void initAbstractFieldDefaults(AbstractField field) {
        field.setRequiredError("Required value is missing");
        field.setImmediate(true);
        field.setInvalidCommitted(false);
        field.setWriteThrough(true);
    }

    public static void initTextFieldDefaults(AbstractTextField field) {
        field.setWidth(DEFAULT_TEXT_FIELD_WIDTH, Sizeable.UNITS_EM);
        field.setNullRepresentation("");
        field.setNullSettingAllowed(false);
    }

    public static void initRichTextFieldDefaults(RichTextArea field) {
        field.setNullRepresentation("");
        field.setNullSettingAllowed(false);
    }

    public static void initDateFieldDefaults(DateField field) {
        field.setResolution(DateField.RESOLUTION_DAY);
    }

    public static void initAbstractSelectDefaults(AbstractSelect field) {
        field.setItemCaptionMode(Select.ITEM_CAPTION_MODE_PROPERTY);
        field.setNullSelectionAllowed(true);
        field.setItemCaptionPropertyId(DEFAULT_DISPLAY_PROPERTY_ID);
        field.setImmediate(true);
    }

    public static void initSelectDefaults(Select field) {
        field.setFilteringMode(Select.FILTERINGMODE_CONTAINS);
    }

    public static void initListSelectDefaults(ListSelect field) {
        field.setMultiSelect(true);
    }

    public enum AutoAdjustWidthMode {
        FULL,
        PARTIAL,
        NONE
    }
}
