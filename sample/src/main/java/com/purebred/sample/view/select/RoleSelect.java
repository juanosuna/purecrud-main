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

package com.purebred.sample.view.select;

import com.purebred.core.view.entity.entityselect.EntitySelect;
import com.purebred.core.view.entity.entityselect.EntitySelectResults;
import com.purebred.core.view.entity.field.DisplayFields;
import com.purebred.sample.dao.RoleDao;
import com.purebred.sample.entity.security.Role;
import com.purebred.sample.view.role.RoleQuery;
import com.purebred.sample.view.role.RoleSearchForm;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
@Scope("prototype")
public class RoleSelect extends EntitySelect<Role> {

    @Resource
    private RoleSearchForm roleSearchForm;

    @Resource
    private RoleSelectResults roleSelectResults;

    @Override
    public RoleSearchForm getSearchForm() {
        return roleSearchForm;
    }

    @Override
    public RoleSelectResults getResultsComponent() {
        return roleSelectResults;
    }

    @Override
    public String getEntityCaption() {
        return "Select Role";
    }

    @Component
    @Scope("prototype")
    public static class RoleSelectResults extends EntitySelectResults<Role> {

        @Resource
        private RoleDao roleDao;

        @Resource
        private RoleQuery roleQuery;

        @Override
        public RoleDao getEntityDao() {
            return roleDao;
        }

        @Override
        public RoleQuery getEntityQuery() {
            return roleQuery;
        }

        @Override
        public void configureFields(DisplayFields displayFields) {
            displayFields.setPropertyIds(new String[]{
                    "name",
                    "lastModified",
                    "modifiedBy"
            });
        }
    }
}

