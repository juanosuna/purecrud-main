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

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

public class RootBean {

    @Valid
    private NestedBean nestedBean;

    private NestedBean ignoredNestedBean;

    @Valid
    @NotNull
    private NestedBean notNullNestedBean;

    public NestedBean getNestedBean() {
        return nestedBean;
    }

    public void setNestedBean(NestedBean nestedBean) {
        this.nestedBean = nestedBean;
    }

    public NestedBean getIgnoredNestedBean() {
        return ignoredNestedBean;
    }

    public void setIgnoredNestedBean(NestedBean ignoredNestedBean) {
        this.ignoredNestedBean = ignoredNestedBean;
    }

    public NestedBean getNotNullNestedBean() {
        return notNullNestedBean;
    }

    public void setNotNullNestedBean(NestedBean notNullNestedBean) {
        this.notNullNestedBean = notNullNestedBean;
    }
}
