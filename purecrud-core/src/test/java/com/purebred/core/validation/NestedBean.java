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

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class NestedBean {

    @NotNull
    @Size(min = 1, max = 1)
    private String property;

    private String optionalProperty;

    public String getProperty() {
        return property;
    }

    public void setProperty(String property) {
        this.property = property;
    }

    public String getOptionalProperty() {
        return optionalProperty;
    }

    public void setOptionalProperty(String optionalProperty) {
        this.optionalProperty = optionalProperty;
    }
}
