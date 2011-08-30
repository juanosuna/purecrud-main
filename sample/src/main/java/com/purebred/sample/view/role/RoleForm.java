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

import com.purebred.core.view.entity.EntityForm;
import com.purebred.core.view.entity.field.FormField;
import com.purebred.core.view.entity.field.FormFields;
import com.purebred.core.view.entity.tomanyrelationship.ToManyRelationship;
import com.purebred.sample.entity.security.Role;
import com.purebred.sample.view.role.related.RelatedPermissions;
import com.purebred.sample.view.role.related.RelatedUsers;
import com.vaadin.terminal.Sizeable;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.Window;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Component
@Scope("prototype")
public class RoleForm extends EntityForm<Role> {

    @Resource
    private RelatedUsers relatedUsers;

    @Resource
    private RelatedPermissions relatedPermissions;

    @Override
    public void configureFields(FormFields formFields) {
        formFields.setPosition("name", 1, 1);
        formFields.setPosition("allowOrDenyByDefault", 1, 2);

        formFields.setPosition("description", 2, 1, 2, 2);
        formFields.setField("description", new TextArea());
        formFields.setWidth("description", 40, Sizeable.UNITS_EM);
        formFields.setHeight("description", 5, Sizeable.UNITS_EM);
        formFields.setAutoAdjustWidthMode("description", FormField.AutoAdjustWidthMode.NONE);
    }

    @Override
    public String getEntityCaption() {
        return "Role Form";
    }

    @Override
    public List<ToManyRelationship> getToManyRelationships() {
        List<ToManyRelationship> toManyRelationships = new ArrayList<ToManyRelationship>();
        toManyRelationships.add(relatedUsers);
        toManyRelationships.add(relatedPermissions);

        return toManyRelationships;
    }

    @Override
    public void configurePopupWindow(Window popupWindow) {
        popupWindow.setWidth(66, Sizeable.UNITS_EM);
        popupWindow.setHeight("95%");
    }
}
