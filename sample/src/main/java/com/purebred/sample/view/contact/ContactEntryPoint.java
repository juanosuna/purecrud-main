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

package com.purebred.sample.view.contact;

import com.purebred.core.view.entity.EntryPoint;
import com.purebred.sample.entity.Contact;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
@Scope("prototype")
public class ContactEntryPoint extends EntryPoint<Contact> {

    @Resource
    private ContactSearchForm contactSearchForm;

    @Resource
    private ContactResults contactResults;

    @Override
    public ContactSearchForm getSearchForm() {
        return contactSearchForm;
    }

    @Override
    public ContactResults getResultsComponent() {
        return contactResults;
    }

    @Override
    public String getEntityCaption() {
        return "Contacts";
    }
}

