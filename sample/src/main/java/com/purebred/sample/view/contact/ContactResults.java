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

import com.purebred.core.view.entity.Results;
import com.purebred.core.view.entity.ResultsTable;
import com.purebred.core.view.entity.field.DisplayFields;
import com.purebred.sample.dao.ContactDao;
import com.purebred.sample.entity.Contact;
import com.purebred.sample.view.account.AccountForm;
import com.vaadin.terminal.Sizeable;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
@Scope("prototype")
public class ContactResults extends Results<Contact> {

    @Resource
    private ContactDao contactDao;

    @Resource
    private ContactQuery contactQuery;

    @Resource
    private ContactForm contactForm;

    @Resource
    private AccountForm accountForm;

    @Override
    public ContactDao getEntityDao() {
        return contactDao;
    }

    @Override
    public ContactQuery getEntityQuery() {
        return contactQuery;
    }

    @Override
    public ContactForm getEntityForm() {
        return contactForm;
    }

    @Override
    public void configureFields(DisplayFields displayFields) {
        displayFields.setPropertyIds(new String[]{
                "firstName",
                "lastName",
                "account.name",
                "mailingAddress.state.code",
                "mailingAddress.country",
                "lastModified",
                "modifiedBy"
        });

        displayFields.setLabel("mailingAddress.state.code", "State");
        displayFields.setLabel("account.name", "Account");
        displayFields.setFormLink("account.name", "account", accountForm);
    }

    @Override
    public void configureTable(ResultsTable resultsTable) {
        resultsTable.setWidth(61, Sizeable.UNITS_EM);
    }
}
