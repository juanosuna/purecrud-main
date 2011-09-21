/*
 * Copyright (c) 2011 Brown Bag Consulting.
 * This file is part of the PureCRUD project.
 * Author: Juan Osuna
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License Version 3
 * as published by the Free Software Foundation with the addition of the
 * following permission added to Section 15 as permitted in Section 7(a):
 * FOR ANY PART OF THE COVERED WORK IN WHICH THE COPYRIGHT IS OWNED BY
 * Brown Bag Consulting, Brown Bag Consulting DISCLAIMS THE WARRANTY OF
 * NON INFRINGEMENT OF THIRD PARTY RIGHTS.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 * The interactive user interfaces in modified source and object code versions
 * of this program must display Appropriate Legal Notices, as required under
 * Section 5 of the GNU Affero General Public License.
 *
 * You can be released from the requirements of the license by purchasing
 * a commercial license. Buying such a license is mandatory as soon as you
 * develop commercial activities involving the PureCRUD software without
 * disclosing the source code of your own applications. These activities
 * include: offering paid services to customers as an ASP, providing
 * services from a web application, shipping PureCRUD with a closed
 * source product.
 *
 * For more information, please contact Brown Bag Consulting at this
 * address: juan@brownbagconsulting.com.
 */

package com.purebred.sample.view.contact;

import com.purebred.core.view.SearchForm;
import com.purebred.core.view.field.FormFields;
import com.purebred.sample.dao.StateDao;
import com.purebred.sample.entity.Country;
import com.purebred.sample.entity.State;
import com.vaadin.data.Property;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Component
@Scope("prototype")
public class ContactSearchForm extends SearchForm<ContactQuery> {

    @Resource
    private StateDao stateDao;

    @Override
    public void configureFields(FormFields formFields) {

        formFields.setPosition("lastName", 1, 1);
        formFields.setPosition("country", 1, 2);
        formFields.setPosition("states", 1, 3);

        formFields.setSelectItems("states", new ArrayList());
        formFields.setVisible("states", false);
        formFields.setMultiSelectDimensions("states", 5, 15);

        formFields.addValueChangeListener("country", this, "countryChanged");
    }

    public void countryChanged(Property.ValueChangeEvent event) {
        Country newCountry = (Country) event.getProperty().getValue();
        List<State> states = stateDao.findByCountry(newCountry);

        getFormFields().setSelectItems("states", states);
        getFormFields().setVisible("states", !states.isEmpty());
    }

    @Override
    public String getEntityCaption() {
        return "Contact Search Form";
    }
}
