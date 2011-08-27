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

package com.purebred.sample.entity.security;


import com.purebred.core.entity.security.AbstractPermission;
import com.purebred.sample.view.role.related.ValidPermission;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Index;

import javax.persistence.*;

@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"ENTITY_TYPE", "FIELD"}))
@ValidPermission
public class Permission extends AbstractPermission {

    @Index(name = "IDX_PERMISSION_ROLE")
    @ForeignKey(name = "FK_PERMISSION_ROLE")
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.REFRESH)
    private Role role;

    public Permission() {
    }

    public Permission(String entityName) {
        super(entityName);
    }

    @Override
    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }
}