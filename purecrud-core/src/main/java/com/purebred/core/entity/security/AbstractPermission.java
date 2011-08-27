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
import com.purebred.core.view.entity.field.LabelDepot;
import org.hibernate.validator.constraints.NotBlank;

import javax.annotation.Resource;
import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@MappedSuperclass
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

    public abstract AbstractRole getRole();
}
