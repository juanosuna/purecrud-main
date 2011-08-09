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

import com.purebred.core.view.entity.Results;
import com.purebred.core.view.entity.ResultsTable;
import com.purebred.core.view.entity.field.DisplayFields;
import com.purebred.sample.dao.UserDao;
import com.purebred.sample.entity.User;
import com.vaadin.terminal.Sizeable;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
@Scope("prototype")
public class UserResults extends Results<User> {

    @Resource
    private UserDao userDao;

    @Resource
    private UserQuery userQuery;

    @Resource
    private UserForm userForm;

    @Override
    public UserDao getEntityDao() {
        return userDao;
    }

    @Override
    public UserQuery getEntityQuery() {
        return userQuery;
    }

    @Override
    public UserForm getEntityForm() {
        return userForm;
    }

    @Override
    public void configureFields(DisplayFields displayFields) {
        displayFields.setPropertyIds(new String[]{
                "loginName",
                "lastModified",
                "modifiedBy"
        });
    }

    @Override
    public void configureTable(ResultsTable resultsTable) {
        resultsTable.setWidth(61, Sizeable.UNITS_EM);
    }
}
