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


import com.purebred.core.util.ReflectionUtil;
import com.purebred.core.view.entity.field.FormField;
import com.purebred.core.view.entity.field.format.JDKFormatPropertyFormatter;
import com.vaadin.data.util.PropertyFormatter;

import java.text.Format;
import java.text.ParsePosition;

public class NumberConversionValidator extends AbstractConversionValidator {

    public NumberConversionValidator(FormField formField, String errorMessage) {
        super(formField, errorMessage);
    }

    @Override
    public void validateImpl(Object value) throws Exception {
        PropertyFormatter propertyFormatter = getFormField().getPropertyFormatter();
        Object parsedValue;
        if (propertyFormatter == null) {
            parsedValue = value;
        } else {
            if (propertyFormatter instanceof JDKFormatPropertyFormatter) {
                parsedValue = parseWithJDKFormat(value, ((JDKFormatPropertyFormatter) propertyFormatter).getFormat());
            } else {
                parsedValue = propertyFormatter.parse(value.toString());
            }
        }
        ReflectionUtil.convertValue(parsedValue, getFormField().getPropertyType());
    }

    private Object parseWithJDKFormat(Object value, Format format) throws InvalidValueException {
        Object parsedValue;

        if (format == null) {
            parsedValue = value;
        } else {
            ParsePosition parsePosition = new ParsePosition(0);
            parsedValue = format.parseObject(value.toString(), parsePosition);
            if (value.toString().length() > parsePosition.getIndex() || parsePosition.getErrorIndex() >= 0) {
                throw new InvalidValueException(getErrorMessage());
            }

        }
        return parsedValue;
    }
}
