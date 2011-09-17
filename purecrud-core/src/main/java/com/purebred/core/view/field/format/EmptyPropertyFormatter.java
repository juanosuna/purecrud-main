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

package com.purebred.core.view.field.format;

import com.vaadin.data.util.PropertyFormatter;

public class EmptyPropertyFormatter extends PropertyFormatter {

    @Override
    public String format(Object value) {
        throw new UnsupportedOperationException("This object represents no formatter and should not actually be called.");
    }

    @Override
    public Object parse(String formattedValue) throws Exception {
        throw new UnsupportedOperationException("This object represents no formatter and should not actually be called.");
    }
}
