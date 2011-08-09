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

import com.purebred.core.view.entity.EntityForm;
import com.purebred.core.view.entity.field.FormFields;
import com.purebred.core.view.entity.field.SelectField;
import com.purebred.sample.util.PhoneConversionValidator;
import com.purebred.sample.util.PhonePropertyFormatter;
import com.purebred.sample.dao.StateDao;
import com.purebred.sample.entity.*;
import com.purebred.sample.view.select.AccountSelect;
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
public class ContactForm extends EntityForm<Contact> {

    @Resource
    private StateDao stateDao;

    @Resource
    private UserSelect userSelect;

    @Resource
    private AccountSelect accountSelect;

    @Override
    public void configureFields(FormFields formFields) {
        formFields.setPosition("Overview", "firstName", 1, 1);
        formFields.setPosition("Overview", "lastName", 1, 2);

        formFields.setPosition("Overview", "title", 2, 1);
        formFields.setPosition("Overview", "birthDate", 2, 2);

        formFields.setPosition("Overview", "account.name", 3, 1);
        formFields.setPosition("Overview", "leadSource", 3, 2);

        formFields.setPosition("Overview", "email", 4, 1);
        formFields.setPosition("Overview", "doNotEmail", 4, 2);

        formFields.setPosition("Overview", "mainPhone", 5, 1);
        formFields.setPosition("Overview", "mainPhone.phoneType", 5, 1);
        formFields.setPosition("Overview", "doNotCall", 5, 2);

        formFields.setPosition("Overview", "assignedTo.loginName", 6, 1);

        formFields.setPosition("Mailing Address", "mailingAddress.street", 1, 1);
        formFields.setPosition("Mailing Address", "mailingAddress.city", 1, 2);
        formFields.setPosition("Mailing Address", "mailingAddress.country", 2, 1);
        formFields.setPosition("Mailing Address", "mailingAddress.zipCode", 2, 2);
        formFields.setPosition("Mailing Address", "mailingAddress.state", 3, 1);

        formFields.setPosition("Other Address", "otherAddress.street", 1, 1);
        formFields.setPosition("Other Address", "otherAddress.city", 1, 2);
        formFields.setPosition("Other Address", "otherAddress.country", 2, 1);
        formFields.setPosition("Other Address", "otherAddress.zipCode", 2, 2);
        formFields.setPosition("Other Address", "otherAddress.state", 3, 1);
        formFields.setTabOptional("Other Address", this, "addOtherAddress", this, "removeOtherAddress");

        formFields.setPosition("Description", "description", 1, 1);

        formFields.setLabel("description", null);
        formFields.setLabel("mainPhone.phoneType", null);
        formFields.setLabel("account.name", "Account");
        formFields.setWidth("mainPhone.phoneType", 7, Sizeable.UNITS_EM);
        formFields.setLabel("assignedTo.loginName", "Assigned to");

        formFields.addValidator("mainPhone", PhoneConversionValidator.class);
        formFields.setPropertyFormatter("mainPhone", new PhonePropertyFormatter());

        formFields.setDescription("mainPhone",
                "<strong>Example formats:</strong>" +
                        "<ul>" +
                        "  <li>US: (919) 975-5331</li>" +
                        "  <li>Germany: +49 30/70248804</li>" +
                        "</ul>");

        formFields.setSelectItems("mailingAddress.state", new ArrayList());
        formFields.addValueChangeListener("mailingAddress.country", this, "countryChanged");

        formFields.setSelectItems("otherAddress.state", new ArrayList());
        formFields.addValueChangeListener("otherAddress.country", this, "otherCountryChanged");

        SelectField selectField = new SelectField(this, "assignedTo", userSelect);
        formFields.setField("assignedTo.loginName", selectField);

        selectField = new SelectField(this, "account", accountSelect);
        formFields.setField("account.name", selectField);
    }

    public void addOtherAddress() {
        getEntity().setOtherAddress(new Address(AddressType.OTHER));
    }

    public void removeOtherAddress() {
        getEntity().setOtherAddress(null);
    }

    public void countryChanged(Property.ValueChangeEvent event) {
        countryChangedImpl(event, "mailingAddress");
    }

    public void otherCountryChanged(Property.ValueChangeEvent event) {
        countryChangedImpl(event, "otherAddress");
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
            return "Contact Form - New";
        } else {
            return "Contact Form - " + getEntity().getName();
        }
    }

    @Override
    public void configurePopupWindow(Window popupWindow) {
        popupWindow.setWidth(62, Sizeable.UNITS_EM);
        popupWindow.setHeight(30, Sizeable.UNITS_EM);
    }
}
