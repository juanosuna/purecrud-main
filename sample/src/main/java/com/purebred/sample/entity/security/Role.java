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
import com.purebred.core.entity.security.AbstractRole;
import com.purebred.core.entity.security.AbstractUserRole;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table
public class Role extends AbstractRole {

    @OneToMany(mappedBy = "role", cascade = CascadeType.ALL)
    private Set<UserRole> userRoles = new HashSet<UserRole>();

    @OneToMany(mappedBy = "role", cascade = CascadeType.ALL)
    private Set<Permission> permissions = new HashSet<Permission>();

    public Role() {
    }

    public Role(String name) {
        super(name);
    }

    @Override
    public Set<UserRole> getUserRoles() {
        return userRoles;
    }

    @Override
    public void setUserRoles(Set<? extends AbstractUserRole> userRoles) {
        this.userRoles = (Set<UserRole>) userRoles;
    }

    @Override
    public Set<Permission> getPermissions() {
        return permissions;
    }

    @Override
    public void setPermissions(Set<? extends AbstractPermission> permissions) {
        this.permissions = (Set<Permission>) permissions;
    }
}