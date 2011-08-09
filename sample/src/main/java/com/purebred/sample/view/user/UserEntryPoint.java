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

import com.purebred.core.view.entity.EntryPoint;
import com.purebred.sample.entity.User;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
@Scope("prototype")
public class UserEntryPoint extends EntryPoint<User> {

    @Resource
    private UserSearchForm userSearchForm;

    @Resource
    private UserResults userResults;

    @Override
    public UserSearchForm getSearchForm() {
        return userSearchForm;
    }

    @Override
    public UserResults getResultsComponent() {
        return userResults;
    }

    @Override
    public String getEntityCaption() {
        return "Users";
    }
}

