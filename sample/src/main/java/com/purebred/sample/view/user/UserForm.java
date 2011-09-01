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

package com.purebred.sample.view.user;

import com.purebred.core.view.entity.EntityForm;
import com.purebred.core.view.entity.field.FormFields;
import com.purebred.core.view.entity.tomanyrelationship.ToManyRelationship;
import com.purebred.sample.dao.RoleDao;
import com.purebred.sample.dao.UserRoleDao;
import com.purebred.sample.entity.security.Role;
import com.purebred.sample.entity.security.User;
import com.purebred.sample.entity.security.UserRole;
import com.purebred.sample.view.user.related.RelatedRoles;
import com.vaadin.ui.PasswordField;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Component
@Scope("prototype")
public class UserForm extends EntityForm<User> {

    @Resource
    private RelatedRoles relatedRoles;

    @Resource
    private RoleDao roleDao;

    @Resource
    private UserRoleDao userRoleDao;

    @Override
    public List<ToManyRelationship> getToManyRelationships() {
        List<ToManyRelationship> toManyRelationships = new ArrayList<ToManyRelationship>();
        toManyRelationships.add(relatedRoles);

        return toManyRelationships;
    }

    @Override
    public void configureFields(FormFields formFields) {
        formFields.setPosition("loginName", 1, 1);
        formFields.setPosition("loginPassword", 2, 1);
        formFields.setField("loginPassword", new PasswordField());

        addPersistListener(this, "onPersist");
    }


    public void onPersist() {
        Role anyUserRole = roleDao.findByName("ROLE_USER");
        UserRole userRole = new UserRole(getEntity(), anyUserRole);
        userRoleDao.persist(userRole);
    }

    @Override
    public String getEntityCaption() {
        return "User Form";
    }
}
