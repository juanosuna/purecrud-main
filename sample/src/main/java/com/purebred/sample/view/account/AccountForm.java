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

import com.purebred.core.view.entity.EntityForm;
import com.purebred.core.view.entity.field.FormFields;
import com.purebred.core.view.entity.field.SelectField;
import com.purebred.core.view.entity.tomanyrelationship.ToManyRelationship;
import com.purebred.sample.util.PhoneConversionValidator;
import com.purebred.sample.util.PhonePropertyFormatter;
import com.purebred.sample.dao.StateDao;
import com.purebred.sample.entity.*;
import com.purebred.sample.view.account.related.RelatedContacts;
import com.purebred.sample.view.account.related.RelatedOpportunities;
import com.purebred.sample.view.select.UserSelect;
import com.vaadin.data.Property;
import com.vaadin.terminal.Sizeable;
import com.vaadin.ui.Window;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Component
@Scope("prototype")
public class AccountForm extends EntityForm<Account> {

    @Resource
    private StateDao stateDao;

    @Resource
    private RelatedContacts relatedContacts;

    @Resource
    private RelatedOpportunities relatedOpportunities;

    @Resource
    private UserSelect userSelect;

    @Override
    public List<ToManyRelationship> getToManyRelationships() {
        List<ToManyRelationship> toManyRelationships = new ArrayList<ToManyRelationship>();
        toManyRelationships.add(relatedContacts);
        toManyRelationships.add(relatedOpportunities);

        return toManyRelationships;
    }

    @Override
    public void configureFields(FormFields formFields) {

        formFields.setPosition("Overview", "name", 1, 1);
        formFields.setPosition("Overview", "website", 1, 2);

        formFields.setPosition("Overview", "mainPhone", 2, 1);
        formFields.setPosition("Overview", "assignedTo.loginName", 2, 2);

        formFields.setPosition("Overview", "email", 3, 1);
        formFields.setPosition("Overview", "accountTypes", 3, 2);

        formFields.setPosition("Details", "tickerSymbol", 1, 1);
        formFields.setPosition("Details", "industry", 1, 2);

        formFields.setPosition("Details", "numberOfEmployees", 2, 1);
        formFields.setPosition("Details", "annualRevenue", 2, 2);
        formFields.setPosition("Details", "annualRevenueInUSD", 3, 1);
        formFields.setPosition("Details", "currency", 3, 2);

        formFields.setPosition("Billing Address", "billingAddress.street", 1, 1);
        formFields.setPosition("Billing Address", "billingAddress.city", 1, 2);
        formFields.setPosition("Billing Address", "billingAddress.country", 2, 1);
        formFields.setPosition("Billing Address", "billingAddress.zipCode", 2, 2);
        formFields.setPosition("Billing Address", "billingAddress.state", 3, 1);

        formFields.setPosition("Mailing Address", "mailingAddress.street", 1, 1);
        formFields.setPosition("Mailing Address", "mailingAddress.city", 1, 2);
        formFields.setPosition("Mailing Address", "mailingAddress.country", 2, 1);
        formFields.setPosition("Mailing Address", "mailingAddress.zipCode", 2, 2);
        formFields.setPosition("Mailing Address", "mailingAddress.state", 3, 1);
        formFields.setTabOptional("Mailing Address", this, "addMailingAddress", this, "removeMailingAddress");

        formFields.setMultiSelectDimensions("accountTypes", 3, 10);

        formFields.setLabel("accountTypes", "Types");
        formFields.setLabel("mainPhone", "Phone");
        formFields.setLabel("assignedTo.loginName", "Assigned to");

        formFields.addValidator("mainPhone", PhoneConversionValidator.class);
        formFields.setPropertyFormatter("mainPhone", new PhonePropertyFormatter());

        formFields.setSelectItems("billingAddress.state", new ArrayList());
        formFields.addValueChangeListener("billingAddress.country", this, "countryChanged");

        formFields.setSelectItems("mailingAddress.state", new ArrayList());
        formFields.addValueChangeListener("mailingAddress.country", this, "otherCountryChanged");

        SelectField selectField = new SelectField(this, "assignedTo", userSelect);
        formFields.setField("assignedTo.loginName", selectField);
    }

    public void addMailingAddress() {
        getEntity().setMailingAddress(new Address(AddressType.MAILING));
    }

    public void removeMailingAddress() {
        getEntity().setMailingAddress(null);
    }

    public void countryChanged(Property.ValueChangeEvent event) {
        countryChangedImpl(event, "billingAddress");
    }

    public void otherCountryChanged(Property.ValueChangeEvent event) {
        countryChangedImpl(event, "mailingAddress");
    }

    public void countryChangedImpl(Property.ValueChangeEvent event, String addressPropertyId) {
        Country newCountry = (Country) event.getProperty().getValue();
        List<State> states = stateDao.findByCountry(newCountry);

        String fullStatePropertyId = addressPropertyId + ".state";
        FormFields formFields = getFormFields();
        formFields.setVisible(fullStatePropertyId, !states.isEmpty());
        formFields.setSelectItems(fullStatePropertyId, states);

        String fullZipCodePropertyId = addressPropertyId + ".zipCode";

        if (newCountry != null && newCountry.getMinPostalCode() != null && newCountry.getMaxPostalCode() != null) {
            formFields.setDescription(fullZipCodePropertyId,
                    "<strong>Postal code range:</strong>" +
                            "<ul>" +
                            "  <li>" + newCountry.getMinPostalCode() + " - " + newCountry.getMaxPostalCode() + "</li>" +
                            "</ul>");
        } else {
            formFields.setDescription(fullZipCodePropertyId, null);
        }
    }


    @Override
    public String getEntityCaption() {
        if (getEntity().getName() == null) {
            return "Account Form - New";
        } else {
            return "Account Form - " + getEntity().getName();
        }
    }

    @Override
    public void configurePopupWindow(Window popupWindow) {
        popupWindow.setWidth(62, Sizeable.UNITS_EM);
        popupWindow.setHeight("95%");
    }
}
