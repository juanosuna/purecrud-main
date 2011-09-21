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
 * services from a hosted web application, shipping PureCRUD with a closed
 * source product.
 *
 * For more information, please contact Brown Bag Consulting at this
 * address: juan@brownbagconsulting.com.
 */

package com.purebred.sample.view.role;

import com.purebred.core.view.EntityForm;
import com.purebred.core.view.field.FormField;
import com.purebred.core.view.field.FormFields;
import com.purebred.core.view.tomanyrelationship.ToManyRelationship;
import com.purebred.sample.entity.security.Role;
import com.purebred.sample.view.role.related.RelatedPermissions;
import com.purebred.sample.view.role.related.RelatedUsers;
import com.vaadin.terminal.Sizeable;
import com.vaadin.ui.TextArea;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Component
@Scope("prototype")
public class RoleForm extends EntityForm<Role> {

    @Resource
    private RelatedUsers relatedUsers;

    @Resource
    private RelatedPermissions relatedPermissions;

    @Override
    public void configureFields(FormFields formFields) {
        formFields.setPosition("name", 1, 1);
        formFields.setPosition("allowOrDenyByDefault", 1, 2);

        formFields.setPosition("description", 2, 1, 2, 2);
        formFields.setField("description", new TextArea());
        formFields.setWidth("description", 40, Sizeable.UNITS_EM);
        formFields.setHeight("description", 5, Sizeable.UNITS_EM);
        formFields.setAutoAdjustWidthMode("description", FormField.AutoAdjustWidthMode.NONE);
    }

    @Override
    public String getEntityCaption() {
        return "Role Form";
    }

    @Override
    public List<ToManyRelationship> getToManyRelationships() {
        List<ToManyRelationship> toManyRelationships = new ArrayList<ToManyRelationship>();
        toManyRelationships.add(relatedUsers);
        toManyRelationships.add(relatedPermissions);

        return toManyRelationships;
    }
}
