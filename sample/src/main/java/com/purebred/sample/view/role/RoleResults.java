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

import com.purebred.core.view.ResultsTable;
import com.purebred.core.view.CrudResults;
import com.purebred.core.view.field.DisplayFields;
import com.purebred.sample.dao.RoleDao;
import com.purebred.sample.entity.security.Role;
import com.vaadin.terminal.Sizeable;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
@Scope("prototype")
public class RoleResults extends CrudResults<Role> {

    @Resource
    private RoleDao roleDao;

    @Resource
    private RoleQuery roleQuery;

    @Resource
    private RoleForm roleForm;

    @Override
    public RoleDao getEntityDao() {
        return roleDao;
    }

    @Override
    public RoleQuery getEntityQuery() {
        return roleQuery;
    }

    @Override
    public RoleForm getEntityForm() {
        return roleForm;
    }

    @Override
    public void configureFields(DisplayFields displayFields) {
        displayFields.setPropertyIds(new String[]{
                "name",
                "allowOrDenyByDefault",
                "lastModified",
                "modifiedBy"
        });

        displayFields.setLabel("allowOrDenyByDefault", "Allow/Deny");
    }

    @Override
    public void configureTable(ResultsTable resultsTable) {
        resultsTable.setWidth(61, Sizeable.UNITS_EM);
    }
}
