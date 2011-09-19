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

import com.purebred.core.dao.EntityDao;
import com.purebred.core.entity.ReferenceEntity;
import com.purebred.core.util.*;
import com.purebred.core.util.assertion.Assert;
import com.purebred.core.validation.NumberConversionValidator;
import com.purebred.core.view.EntityForm;
import com.purebred.core.view.field.format.EmptyPropertyFormatter;
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

/**
 * A field in a form. Wraps Vaadin field component, while providing other features and integration with PureCRUD.
 *
 * Automatically generates labels with required asterisks.
 * Keeps track of row and column positions in the form grid layout.
 */
public class FormField extends DisplayField {
    /**
     * Property id for displaying captions in select fields.
     */
    public static final String DEFAULT_DISPLAY_PROPERTY_ID = "displayName";

    /**
     * Default text field width in EM
     */
    public static final Integer DEFAULT_TEXT_FIELD_WIDTH = 11;

    /**
     * Default select field width in EM
     */
    public static final Integer DEFAULT_SELECT_FIELD_WIDTH = 11;

    private String tabName = "";
    private Field field;
    private Integer columnStart;
    private Integer rowStart;
    private Integer columnEnd;
    private Integer rowEnd;
    private boolean isRequired;
    private boolean isReadOnly;
    private com.vaadin.ui.Label label;
    private boolean isVisible;
    private AutoAdjustWidthMode autoAdjustWidthMode = AutoAdjustWidthMode.PARTIAL;
    private Integer defaultWidth;
    private boolean hasConversionError;

    FormField(FormFields formFields, String propertyId) {
        super(formFields, propertyId);
    }

    /**
     * Get Vaadin label for this field. Label is automatically generated from property ID unless configured
     * by the application.
     *
     * @return Vaadin label for this field
     */
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

    @Override
    protected String getLabelSectionDisplayName() {
        if (tabName.isEmpty()) {
            return "Form";
        } else {
            return tabName;
        }
    }

    /**
     * Set the field label, thus overriding default generated label.
     *
     * @param labelText display label
     */
    public void setFieldLabel(String labelText) {
        getFieldLabel().setValue(labelText);
    }

    /**
     * Get the name of the tab this field resides in.
     *
     * @return name of tab that contains this field
     */
    public String getTabName() {
        return tabName;
    }

    /**
     * Set the name of the tab this field resides in.
     *
     * @param tabName name of tab that contains this field
     */
    public void setTabName(String tabName) {
        this.tabName = tabName;
    }

    /**
     * Get the column start position of this field, starting with 1 not 0
     *
     * @return column start position
     */
    public Integer getColumnStart() {
        return columnStart;
    }

    /**
     * Set the column start position of this field, starting with 1 not 0
     *
     * @param columnStart column start position
     */
    public void setColumnStart(Integer columnStart) {
        this.columnStart = columnStart;
    }

    /**
     * Get the row start position of this field, starting with 1 not 0
     *
     * @return row start position
     */
    public Integer getRowStart() {
        return rowStart;
    }

    /**
     * Set the row start position of this field, starting with 1 not 0
     *
     * @param rowStart row start position
     */
    public void setRowStart(Integer rowStart) {
        this.rowStart = rowStart;
    }

    /**
     * Get the column end position of this field
     *
     * @return column end position
     */
    public Integer getColumnEnd() {
        return columnEnd;
    }

    /**
     * Set the column end position of this field
     *
     * @param columnEnd column end position
     */
    public void setColumnEnd(Integer columnEnd) {
        this.columnEnd = columnEnd;
    }

    /**
     * Get the row end position of this field
     *
     * @return row end position
     */
    public Integer getRowEnd() {
        return rowEnd;
    }

    /**
     * Set the row end position of this field
     *
     * @param rowEnd row end position
     */
    public void setRowEnd(Integer rowEnd) {
        this.rowEnd = rowEnd;
    }

    /**
     * Get the underlying Vaadin field. The field is intelligently and automatically generated based on the property type.
     *
     * In most cases, applications will not need to access Vaadin APIs directly. However,
     * it is exposed in case Vaadin features are needed that are not available in PureCRUD.
     *
     * @return Vaadin field
     */
    public Field getField() {
        if (field == null) {
            field = generateField();
            initializeFieldDefaults();
        }

        return field;
    }

    /**
     * Get the underlying Vaadin field. The field is intelligently and automatically generated based on the property type.
     *
     * In most cases, applications will not need to access Vaadin APIs directly. However,
     * it is exposed in case Vaadin features are needed that are not available in PureCRUD.
     *
     * @return Vaadin field
     */
    public void setField(Field field) {
        setField(field, true);
    }

    /**
     * Set the underlying Vaadin field, overriding the automatically generated one.
     *
     * @param field Vaadin field
     * @param initializeDefaults allow PureCRUD to initialize the default settings for Vaadin field
     */
    public void setField(Field field, boolean initializeDefaults) {
        this.field = field;
        if (initializeDefaults) {
            initializeFieldDefaults();
        }
    }

    private void initWidthAndMaxLengthDefaults(AbstractTextField abstractTextField) {
        defaultWidth = MathUtil.maxIgnoreNull(DEFAULT_TEXT_FIELD_WIDTH, getBeanPropertyType().getMinimumLength());
        abstractTextField.setWidth(defaultWidth, Sizeable.UNITS_EM);

        Integer maxWidth = getBeanPropertyType().getMaximumLength();
        if (maxWidth != null) {
            abstractTextField.setMaxLength(maxWidth);
        }
    }

    /**
     * Get auto-adjust-width mode
     *
     * @return auto-adjust-width mode
     */
    public AutoAdjustWidthMode getAutoAdjustWidthMode() {
        return autoAdjustWidthMode;
    }

    /**
     * Set auto-adjust-width mode
     *
     * @param autoAdjustWidthMode auto-adjust-width mode
     */
    public void setAutoAdjustWidthMode(AutoAdjustWidthMode autoAdjustWidthMode) {
        this.autoAdjustWidthMode = autoAdjustWidthMode;
    }

    void autoAdjustTextFieldWidth() {
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

    /**
     * Get width of the field
     *
     * @return width of the field
     */
    public float getWidth() {
        return getField().getWidth();
    }

    /**
     * Manually set width of the field.
     * @param width
     * @param unit
     */
    public void setWidth(float width, int unit) {
        getField().setWidth(width, unit);
    }

    public void setHeight(float height, int unit) {
        getField().setHeight(height, unit);
    }

    private void autoAdjustSelectWidth() {
        Assert.PROGRAMMING.assertTrue(getField() instanceof AbstractSelect,
                "FormField.autoAdjustSelectWidth can only be called on select fields");

        if (autoAdjustWidthMode == AutoAdjustWidthMode.NONE) return;

        AbstractSelect selectField = (AbstractSelect) getField();
        Collection itemsIds = selectField.getItemIds();

        int maxWidth = 0;
        for (Object itemsId : itemsIds) {
            String caption = selectField.getItemCaption(itemsId);
            int approximateWidth = StringUtil.approximateColumnWidth(caption);
            maxWidth = Math.max(maxWidth, approximateWidth);
        }

        if (autoAdjustWidthMode == AutoAdjustWidthMode.FULL) {
            selectField.setWidth(maxWidth, Sizeable.UNITS_EM);
        } else if (autoAdjustWidthMode == AutoAdjustWidthMode.PARTIAL) {
            selectField.setWidth(MathUtil.maxIgnoreNull(maxWidth, DEFAULT_SELECT_FIELD_WIDTH), Sizeable.UNITS_EM);
        }
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
        autoAdjustSelectWidth();
    }

    public void setSelectItems(Map<Object, String> items) {
        setSelectItems(items, null);
    }

    public void setSelectItems(Map<Object, String> items, String nullCaption) {
        Field field = getField();
        Assert.PROGRAMMING.assertTrue(field instanceof AbstractSelect,
                "property " + getPropertyId() + " is not a AbstractSelect field");
        AbstractSelect selectField = (AbstractSelect) field;
        selectField.setItemCaptionMode(Select.ITEM_CAPTION_MODE_EXPLICIT);

        selectField.removeAllItems();

        if (nullCaption != null) {
            selectField.addItem(nullCaption);
            selectField.setItemCaption(nullCaption, nullCaption);
            selectField.setNullSelectionItemId(nullCaption);
        }

        for (Object item : items.keySet()) {
            String caption = items.get(item);
            selectField.addItem(item);
            selectField.setItemCaption(item, caption);
        }

        autoAdjustSelectWidth();
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
        this.isVisible = isVisible;
        getField().setVisible(isVisible);
        getFieldLabel().setVisible(isVisible);
    }

    public void allowView() {
        getField().setVisible(isVisible);
        getFieldLabel().setVisible(isVisible);
    }

    public void denyView() {
        getField().setVisible(false);
        getFieldLabel().setVisible(false);
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

    public void setEnabled(boolean enabled) {
        getField().setEnabled(enabled);
    }

    public void setReadOnly(boolean isReadOnly) {
        if (getField() instanceof SelectField) {
            ((SelectField) getField()).setButtonVisible(!isReadOnly);
        }

        getField().setReadOnly(isReadOnly);
    }

    public void restoreIsReadOnly() {
        if (getField() instanceof SelectField) {
            ((SelectField) getField()).setButtonVisible(!isReadOnly);
        }
        getField().setReadOnly(isReadOnly);
    }

    public void setValue(Object value) {
        getField().setValue(value);
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

            List referenceEntities = null;
            if (Currency.class.isAssignableFrom(valueType)) {
                referenceEntities = CurrencyUtil.getAvailableCurrencies();
                ((AbstractSelect) field).setItemCaptionPropertyId("currencyCode");
            } else if (valueType.isEnum()) {
                Object[] enumConstants = valueType.getEnumConstants();
                referenceEntities = Arrays.asList(enumConstants);
            } else if (ReferenceEntity.class.isAssignableFrom(valueType)) {
                EntityDao propertyDao = SpringApplicationContext.getBeanByTypeAndGenericArgumentType(EntityDao.class,
                        valueType);
                referenceEntities = propertyDao.findAll();
            }

            if (referenceEntities != null) {
                setSelectItems(referenceEntities);
            }
        }


        if (getFormFields().isEntityForm()) {
            if (getBeanPropertyType().isValidatable()) {
                initializeIsRequired();
                initializeValidators();
            }

            // Change listener causes erratic behavior for RichTextArea
            if (!(field instanceof RichTextArea)) {
                field.addListener(new FieldValueChangeListener());
            }
        }

        isReadOnly = field.isReadOnly();
        isVisible = field.isVisible();
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
        field.setWidth(DEFAULT_SELECT_FIELD_WIDTH, Sizeable.UNITS_EM);
        field.setItemCaptionMode(Select.ITEM_CAPTION_MODE_PROPERTY);
        field.setNullSelectionAllowed(true);
        field.setItemCaptionPropertyId(DEFAULT_DISPLAY_PROPERTY_ID);
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
