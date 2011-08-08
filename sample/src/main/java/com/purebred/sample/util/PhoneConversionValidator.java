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

package com.purebred.sample.util;


import com.purebred.core.validation.AbstractConversionValidator;
import com.purebred.core.view.entity.field.FormField;
import com.vaadin.data.util.PropertyFormatter;

public class PhoneConversionValidator extends AbstractConversionValidator {

    public PhoneConversionValidator(FormField formField) {
        super(formField);
    }

    @Override
    public void validateImpl(Object value) throws Exception {
        PropertyFormatter propertyFormatter = getFormField().getPropertyFormatter();
        propertyFormatter.parse(value.toString());
    }
}
