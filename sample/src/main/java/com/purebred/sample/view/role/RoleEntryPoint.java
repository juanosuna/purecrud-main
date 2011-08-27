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

import com.purebred.core.view.entity.EntryPoint;
import com.purebred.core.view.entity.MainEntryPoint;
import com.purebred.sample.entity.security.Role;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
@Scope("prototype")
public class RoleEntryPoint extends MainEntryPoint<Role> {

    @Resource
    private RoleSearchForm roleSearchForm;

    @Resource
    private RoleResults roleResults;

    @Override
    public RoleSearchForm getSearchForm() {
        return roleSearchForm;
    }

    @Override
    public RoleResults getResultsComponent() {
        return roleResults;
    }

    @Override
    public String getEntityCaption() {
        return "Roles";
    }
}

