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

import com.purebred.core.security.SecurityService;
import com.purebred.core.util.SpringApplicationContext;

import javax.annotation.Resource;
import javax.persistence.*;
import java.util.Date;

/**
 * Base class for entities wishing to be audited. This means that creation and modification timestamps
 * are logged as well as the login name of the user responsible for the creation or modification.
 * This class also versions entities in order to handle concurrent optimistic writes gracefully.
 * Finally, any instances of this class are automatically autowired by Spring, allowing injection
 * of resources into entities.
 *
 */
@MappedSuperclass
@EntityListeners({AuditableEntity.WritableEntityListener.class})
public abstract class AuditableEntity implements IdentifiableEntity {

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

    /**
     * Gets the version number, which is incremented every time this entity's changes are updated
     *
     * @return version number starting at 0
     */
    public Integer getVersion() {
        return version;
    }

    /**
     * Gets the last time changes were saved to the database
     *
     * @return timestamp
     */
    public Date getLastModified() {
        return lastModified;
    }

    /**
     * Gets the login name of the user who made the last modifications
     * @return login name of the user entity
     */
    public String getModifiedBy() {
        return modifiedBy;
    }

    /**
     * Gets the time this entity was created in the database
     *
     * @return timestamp
     */
    public Date getCreated() {
        return created;
    }

    /**
     * Gets the login name of the user who created this entity
     * @return login name of the user entity
     */
    public String getCreatedBy() {
        return createdBy;
    }

    // todo see if this is really necessary
    public void updateLastModified() {
        new WritableEntityListener().onPreUpdate(this);
    }

    public static class WritableEntityListener {
        @Resource
        private SecurityService securityService;

        public WritableEntityListener() {
            SpringApplicationContext.autowire(this);
        }

        @PrePersist
        public void onPrePersist(AuditableEntity writableEntity) {
            writableEntity.created = new Date();
            writableEntity.lastModified = writableEntity.created;

            writableEntity.createdBy = securityService.getCurrentLoginName();
            writableEntity.modifiedBy = writableEntity.createdBy;
        }

        @PreUpdate
        public void onPreUpdate(AuditableEntity writableEntity) {
            writableEntity.lastModified = new Date();
            writableEntity.modifiedBy = securityService.getCurrentLoginName();
        }
    }
}
