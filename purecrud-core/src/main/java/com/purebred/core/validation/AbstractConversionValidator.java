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

package com.purebred.core.validation;


import com.purebred.core.view.entity.EntityForm;
import com.purebred.core.view.entity.field.FormField;
import com.vaadin.data.Validator;

public abstract class AbstractConversionValidator implements Validator {
    private String errorMessage;
    private FormField formField;

    public AbstractConversionValidator(FormField formField, String errorMessage) {
        this.formField = formField;
        this.errorMessage = errorMessage;
    }

    protected AbstractConversionValidator(FormField formField) {
        this.formField = formField;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public FormField getFormField() {
        return formField;
    }

    public abstract void validateImpl(Object value) throws Exception;

    public void validate(Object value) throws InvalidValueException {
        try {
            validateImpl(value);
            formField.setHasConversionError(false);
        } catch (Exception e) {
            formField.setHasConversionError(true);
            if (getErrorMessage() != null) {
                throw new InvalidValueException(getErrorMessage());
            } else {
                throw new InvalidValueException(e.getMessage());
            }
        } finally {
            EntityForm entityForm = (EntityForm) formField.getFormFields().getForm();
            entityForm.syncTabAndSaveButtonErrors();
        }
    }

    @Override
    public boolean isValid(Object value) {
        try {
            validate(value);
            return true;
        } catch (InvalidValueException e) {
            return false;
        }
    }

}
