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
import com.purebred.sample.util.PhonePropertyFormatter;
import com.purebred.sample.dao.ContactDao;
import com.purebred.sample.entity.Contact;
import com.purebred.sample.view.contact.ContactQuery;
import com.purebred.sample.view.contact.ContactSearchForm;
import com.vaadin.terminal.Sizeable;
import com.vaadin.ui.Window;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
@Scope("prototype")
public class ContactSelect extends EntitySelect<Contact> {

    @Resource
    private ContactSearchForm contactSearchForm;

    @Resource
    private ContactSelectResults contactSelectResults;

    @Override
    public ContactSearchForm getSearchForm() {
        return contactSearchForm;
    }

    @Override
    public ContactSelectResults getResultsComponent() {
        return contactSelectResults;
    }


    @Component
    @Scope("prototype")
    public static class ContactSelectResults extends EntitySelectResults<Contact> {

        @Resource
        private ContactDao contactDao;

        @Resource
        private ContactQuery contactQuery;

        @Override
        public ContactDao getEntityDao() {
            return contactDao;
        }

        @Override
        public ContactQuery getEntityQuery() {
            return contactQuery;
        }

        @Override
        public void configureFields(DisplayFields displayFields) {
            displayFields.setPropertyIds(new String[]{
                    "name",
                    "title",
                    "mailingAddress.state.code",
                    "mailingAddress.country",
                    "mainPhone"
            });

            displayFields.setLabel("mailingAddress.state.code", "State");
            displayFields.setLabel("mainPhone", "Phone");
            displayFields.setSortable("name", false);
            displayFields.setPropertyFormatter("mainPhone", new PhonePropertyFormatter());
        }
    }

    @Override
    public void configurePopupWindow(Window popupWindow) {
        popupWindow.setWidth(45, Sizeable.UNITS_EM);
        popupWindow.setHeight("95%");
    }
}

