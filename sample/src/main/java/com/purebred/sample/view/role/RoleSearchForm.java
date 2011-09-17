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

package com.purebred.sample.view.role;

import com.purebred.core.view.SearchForm;
import com.purebred.core.view.field.FormFields;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
public class RoleSearchForm extends SearchForm<RoleQuery> {

    @Override
    public void configureFields(FormFields formFields) {
        formFields.setPosition("name", 1, 1);
    }

    @Override
    public String getEntityCaption() {
        return "Role Search Form";
    }
}
