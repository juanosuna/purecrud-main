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

package com.purebred.sample.view.user;

import com.purebred.core.view.entity.EntityForm;
import com.purebred.core.view.entity.field.FormFields;
import com.purebred.sample.dao.UserDao;
import com.purebred.sample.entity.*;
import com.vaadin.terminal.Sizeable;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.Window;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
@Scope("prototype")
public class UserForm extends EntityForm<User> {

    @Resource
    private UserDao userDao;

    @Override
    public void configureFields(FormFields formFields) {
        formFields.setPosition("loginName", 1, 1);
        formFields.setPosition("loginPassword", 2, 1);
        formFields.setField("loginPassword", new PasswordField());
    }


    @Override
    public String getEntityCaption() {
        return "User Form";
    }

    @Override
    public void configurePopupWindow(Window popupWindow) {
        popupWindow.setWidth(33, Sizeable.UNITS_EM);
        popupWindow.setHeight(17, Sizeable.UNITS_EM);
    }
}
