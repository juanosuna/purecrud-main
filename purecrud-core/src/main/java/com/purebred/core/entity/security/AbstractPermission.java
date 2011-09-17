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
import com.purebred.core.view.field.LabelDepot;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Index;
import org.hibernate.validator.constraints.NotBlank;

import javax.annotation.Resource;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * A permission for controlling view, create, edit or delete actions against an
 * entity type or a field/property within an entity type.
 */
@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public abstract class AbstractPermission extends WritableEntity {

    @Transient
    @Resource
    private LabelDepot labelDepot;

    private String entityType;
    private String field;

    private boolean view;
    private boolean create;
    private boolean edit;
    private boolean delete;

    @Index(name = "IDX_PERMISSION_ROLE")
    @ForeignKey(name = "FK_PERMISSION_ROLE")
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.REFRESH)
    private AbstractRole role;


    public AbstractPermission() {
    }

    public AbstractPermission(String entityType) {
        this.entityType = entityType;
    }

    @NotBlank
    @NotNull
    @Size(min = 1, max = 64)
    public String getEntityType() {
        return entityType;
    }

    public void setEntityType(String entityType) {
        this.entityType = entityType;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public boolean isView() {
        return view;
    }

    public void setView(boolean view) {
        this.view = view;
    }

    public boolean isCreate() {
        return create;
    }

    public void setCreate(boolean create) {
        this.create = create;
    }

    public boolean isEdit() {
        return edit;
    }

    public void setEdit(boolean edit) {
        this.edit = edit;
    }

    public boolean isDelete() {
        return delete;
    }

    public void setDelete(boolean delete) {
        this.delete = delete;
    }

    public String getPermissions() {
        StringBuilder permissions = new StringBuilder();
        if (isCreate()) {
            permissions.append("Create");
        }
        if (isView()) {
            if (permissions.length() > 0) {
                permissions.append(", ");
            }
            permissions.append("View");
        }
        if (isEdit()) {
            if (permissions.length() > 0) {
                permissions.append(", ");
            }
            permissions.append("Edit");
        }
        if (isDelete()) {
            if (permissions.length() > 0) {
                permissions.append(", ");
            }
            permissions.append("Delete");
        }

        return permissions.toString();
    }

    public String getEntityTypeLabel() {
        if (getEntityType() == null) {
            return null;
        } else {
            return labelDepot.getEntityLabel(getEntityType());
        }
    }

    public String getFieldLabel() {
        if (getEntityType() == null || getField() == null) {
            return null;
        } else {
            return labelDepot.getFieldLabel(getEntityType(), getField());
        }
    }

    public AbstractRole getRole() {
        return role;
    }

    public void setRole(AbstractRole role) {
        this.role = role;
    }
}
