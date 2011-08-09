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
import com.purebred.sample.dao.AccountDao;
import com.purebred.sample.entity.Account;
import com.purebred.sample.view.account.AccountQuery;
import com.purebred.sample.view.account.AccountSearchForm;
import com.vaadin.terminal.Sizeable;
import com.vaadin.ui.Window;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
@Scope("prototype")
public class AccountSelect extends EntitySelect<Account> {

    @Resource
    private AccountSearchForm accountSearchForm;

    @Resource
    private AccountSelectResults accountSelectResults;

    @Override
    public AccountSearchForm getSearchForm() {
        return accountSearchForm;
    }

    @Override
    public AccountSelectResults getResultsComponent() {
        return accountSelectResults;
    }


    @Component
    @Scope("prototype")
    public static class AccountSelectResults extends EntitySelectResults<Account> {

        @Resource
        private AccountDao accountDao;

        @Resource
        private AccountQuery accountQuery;

        @Override
        public AccountDao getEntityDao() {
            return accountDao;
        }

        @Override
        public AccountQuery getEntityQuery() {
            return accountQuery;
        }

        @Override
        public void configureFields(DisplayFields displayFields) {
            displayFields.setPropertyIds(new String[]{
                    "name",
                    "tickerSymbol",
                    "website",
                    "billingAddress.state.code",
                    "billingAddress.country"
            });

            displayFields.setLabel("billingAddress.state.code", "State");
        }
    }

    @Override
    public void configurePopupWindow(Window popupWindow) {
        popupWindow.setWidth(55, Sizeable.UNITS_EM);
        popupWindow.setHeight("95%");
    }
}

