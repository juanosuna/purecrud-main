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

import com.purebred.core.util.SpringApplicationContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.util.Date;

@MappedSuperclass
@EntityListeners({AuditableEntity.WritableEntityListener.class})
public abstract class AuditableEntity implements IdentifiableEntity {

    public static final String SYSTEM_USER = "system";

    // todo change to use autowiring
    public static String getCurrentUser() {
        if (SecurityContextHolder.getContext() != null && SecurityContextHolder.getContext().getAuthentication() != null
                && SecurityContextHolder.getContext().getAuthentication().getPrincipal() != null) {
            UserDetails user = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            return user.getUsername();
        } else {
            return SYSTEM_USER;
        }
    }

    @Version
    private Integer version;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    private Date lastModified;

    @Column(nullable = false)
    private String modifiedBy;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    private Date created;

    @Column(nullable = false)
    private String createdBy;

    protected AuditableEntity() {
        SpringApplicationContext.autowire(this);
    }

    public Integer getVersion() {
        return version;
    }

    public Date getLastModified() {
        return lastModified;
    }

    public void setLastModified(Date lastModified) {
        this.lastModified = lastModified;
    }

    public String getModifiedBy() {
        return modifiedBy;
    }

    public void setModifiedBy(String modifiedBy) {
        this.modifiedBy = modifiedBy;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public void updateLastModified() {
        new WritableEntityListener().onPreUpdate(this);
    }

    public static class WritableEntityListener {
        @PrePersist
        public void onPrePersist(AuditableEntity writableEntity) {
            writableEntity.created = new Date();
            writableEntity.lastModified = writableEntity.created;

            writableEntity.createdBy = getCurrentUser();
            writableEntity.modifiedBy = writableEntity.createdBy;
        }

        @PreUpdate
        public void onPreUpdate(AuditableEntity writableEntity) {
            writableEntity.lastModified = new Date();
            writableEntity.modifiedBy = getCurrentUser();
        }
    }
}
