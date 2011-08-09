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

package com.purebred.sample.view.account;

import com.purebred.core.view.entity.Results;
import com.purebred.core.view.entity.ResultsTable;
import com.purebred.core.view.entity.field.DisplayFields;
import com.purebred.sample.util.PhonePropertyFormatter;
import com.purebred.sample.dao.AccountDao;
import com.purebred.sample.entity.Account;
import com.vaadin.terminal.Sizeable;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
@Scope("prototype")
public class AccountResults extends Results<Account> {

    @Resource
    private AccountDao accountDao;

    @Resource
    private AccountQuery accountQuery;

    @Resource
    private AccountForm accountForm;

    @Override
    public AccountDao getEntityDao() {
        return accountDao;
    }

    @Override
    public AccountQuery getEntityQuery() {
        return accountQuery;
    }

    @Override
    public AccountForm getEntityForm() {
        return accountForm;
    }

    @Override
    public void configureFields(DisplayFields displayFields) {
        displayFields.setPropertyIds(new String[]{
                "name",
                "billingAddress.state.code",
                "billingAddress.country",
                "mainPhone",
                "numberOfEmployees",
                "annualRevenueInUSDFormatted",
                "lastModified",
                "modifiedBy"
        });

        displayFields.setLabel("billingAddress.state.code", "State");
        displayFields.setLabel("mainPhone", "Phone");
        displayFields.setLabel("numberOfEmployees", "# of Employees");
        displayFields.setLabel("annualRevenueInUSDFormatted", "Annual Revenue");
        displayFields.setSortable("mainPhone", false);
        displayFields.setPropertyFormatter("mainPhone", new PhonePropertyFormatter());
    }

    @Override
    public void configureTable(ResultsTable resultsTable) {
        resultsTable.setWidth(70, Sizeable.UNITS_EM);
    }
}
