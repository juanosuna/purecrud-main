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

package com.purebred.core.entity.security;

import com.purebred.core.entity.WritableEntity;
import com.purebred.core.util.assertion.Assert;
import org.hibernate.validator.constraints.NotBlank;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Lob;
import javax.persistence.MappedSuperclass;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Set;

@MappedSuperclass
public abstract class AbstractRole extends WritableEntity {
    private String name;

    @Enumerated(EnumType.STRING)
    private AllowOrDeny allowOrDenyByDefault = AllowOrDeny.ALLOW;

    @Lob
    private String description;

    public AbstractRole() {
    }

    public AbstractRole(String name) {
        this.name = name;
    }

    @NotBlank
    @NotNull
    @Size(min = 4, max = 64)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public AllowOrDeny getAllowOrDenyByDefault() {
        return allowOrDenyByDefault;
    }

    public void setAllowOrDenyByDefault(AllowOrDeny allowOrDenyByDefault) {
        this.allowOrDenyByDefault = allowOrDenyByDefault;
    }

    @Size(min = 4, max = 255)
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public abstract Set<? extends AbstractUserRole> getUserRoles();

    public abstract void setUserRoles(Set<? extends AbstractUserRole> userRoles);

    public abstract Set<? extends AbstractPermission> getPermissions();

    public abstract void setPermissions(Set<? extends AbstractPermission> permissions);

    public AbstractPermission getPermission(String entityType) {
        Set<? extends AbstractPermission> permissions = getPermissions();

        AbstractPermission foundPermission = null;
        for (AbstractPermission permission : permissions) {
            if (permission.getEntityType().equals(entityType) && permission.getField() == null) {
                Assert.DATABASE.assertTrue(foundPermission == null, "Database must not contain two records" +
                        " with the same entityType and field: " + entityType);
                foundPermission = permission;
            }
        }

        return foundPermission;
    }

    public AbstractPermission getPermission(String entityType, String field) {
        Set<? extends AbstractPermission> permissions = getPermissions();

        AbstractPermission foundPermission = null;
        for (AbstractPermission permission : permissions) {
            if (permission.getEntityType().equals(entityType) && permission.getField() != null
                    && permission.getField().equals(field)) {
                Assert.DATABASE.assertTrue(foundPermission == null, "Database must not contain two records" +
                        " with the same entityType and field: " + entityType + "." + field);
                foundPermission = permission;
            }
        }

        return foundPermission;
    }

    public boolean isViewAllowed(String entityType) {
        return getPermission(entityType) == null ? allowOrDenyByDefault == AllowOrDeny.ALLOW
                : getPermission(entityType).isView();
    }

    public boolean isEditAllowed(String entityType) {
        return getPermission(entityType) == null ? allowOrDenyByDefault == AllowOrDeny.ALLOW
                : getPermission(entityType).isEdit();
    }

    public boolean isCreateAllowed(String entityType) {
        return getPermission(entityType) == null ? allowOrDenyByDefault == AllowOrDeny.ALLOW
                : getPermission(entityType).isCreate();
    }

    public boolean isDeleteAllowed(String entityType) {
        return getPermission(entityType) == null ? allowOrDenyByDefault == AllowOrDeny.ALLOW
                : getPermission(entityType).isDelete();
    }

    public boolean isViewAllowed(String entityType, String field) {
        return getPermission(entityType, field) == null ? isViewAllowed(entityType)
                : getPermission(entityType, field).isView();
    }

    public boolean isEditAllowed(String entityType, String field) {
        return getPermission(entityType, field) == null ? isEditAllowed(entityType)
                : getPermission(entityType, field).isEdit();

    }
}
