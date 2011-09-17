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

package com.purebred.core.entity;

import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

/**
 * Base class for entities that are read-only by end users and that represent
 * things like menu selects like states or countries.
 */
@MappedSuperclass
public abstract class ReferenceEntity implements IdentifiableEntity, Comparable {

    public static final String CACHE_REGION = "ReadOnly";

    @Id
    private String id;

    private String displayName;

    private Integer sortOrder;

    protected ReferenceEntity() {
    }

    protected ReferenceEntity(String id) {
        this.id = id;
    }

    protected ReferenceEntity(String id, String displayName) {
        this.id = id;
        this.displayName = displayName;
    }

    @Override
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    /**
     * Get caption text for displaying to the user in menus. The display name
     * can be different than the id but doesn't have to be.
     *
     * @return friendly name that identifies this entity to an end-user
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Set caption text for displaying to the user in menus. The display name
     * can be different than the id but doesn't have to be.
     *
     * @param  displayName friendly name that identifies this entity to an end-user
     */
    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public Integer getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(Integer order) {
        this.sortOrder = order;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ReferenceEntity)) return false;

        ReferenceEntity that = (ReferenceEntity) o;

        if (!getId().equals(that.getId())) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return getId().hashCode();
    }

    @Override
    public String toString() {
        return getId();
    }

    @Override
    public int compareTo(Object o) {
        return id.compareTo(((ReferenceEntity) o).id);
    }
}
