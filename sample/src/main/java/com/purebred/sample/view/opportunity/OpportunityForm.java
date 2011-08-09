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

package com.purebred.sample.view.opportunity;

import com.purebred.core.view.entity.EntityForm;
import com.purebred.core.view.entity.field.FormFields;
import com.purebred.core.view.entity.field.SelectField;
import com.purebred.sample.entity.Opportunity;
import com.purebred.sample.view.select.AccountSelect;
import com.purebred.sample.view.select.UserSelect;
import com.vaadin.terminal.Sizeable;
import com.vaadin.ui.Window;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
@Scope("prototype")
public class OpportunityForm extends EntityForm<Opportunity> {

    @Resource
    private AccountSelect accountSelect;

    @Resource
    private UserSelect userSelect;

    @Override
    public void configureFields(FormFields formFields) {

        formFields.setPosition("Overview", "name", 1, 1);
        formFields.setPosition("Overview", "opportunityType", 1, 2);

        formFields.setPosition("Overview", "account.name", 2, 1);
        formFields.setPosition("Overview", "leadSource", 2, 2);

        formFields.setPosition("Overview", "salesStage", 3, 1);
        formFields.setPosition("Overview", "assignedTo.loginName", 3, 2);

        formFields.setPosition("Overview", "amount", 4, 1);
        formFields.setPosition("Overview", "currency", 4, 2);

        formFields.setPosition("Overview", "probability", 5, 1);
        formFields.setPosition("Overview", "amountWeightedInUSD", 5, 2);

        formFields.setPosition("Overview", "expectedCloseDate", 6, 1);

        formFields.setPosition("Description", "description", 1, 1);

        formFields.setLabel("description", null);
        formFields.setLabel("opportunityType", "Type");
        formFields.setLabel("account.name", "Account");
        formFields.setLabel("assignedTo.loginName", "Assigned to");

        SelectField selectField = new SelectField(this, "assignedTo", userSelect);
        formFields.setField("assignedTo.loginName", selectField);

        selectField = new SelectField(this, "account", accountSelect);
        formFields.setField("account.name", selectField);
    }

    @Override
    public String getEntityCaption() {
        if (getEntity().getName() == null) {
            return "Opportunity Form - New";
        } else {
            return "Opportunity Form - " + getEntity().getName();
        }
    }

    @Override
    public void configurePopupWindow(Window popupWindow) {
        popupWindow.setWidth(68, Sizeable.UNITS_EM);
        popupWindow.setHeight(32, Sizeable.UNITS_EM);
    }
}
