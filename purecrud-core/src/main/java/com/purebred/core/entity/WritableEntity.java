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

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import java.util.UUID;

/**
 * Base class for entities that are writable by end users.
 * <p/>
 * This class requires that entities use a generated Long id as the primary key. This is a surrogate key
 * that should have no business meaning.
 * <p/>
 * It also generates a unique UUID that is used in the default equals and hashcode logic. Developers are free
 * to override this logic and use their own logic based on business keys, which is the "ideal" best practice.
 * However, the UUID approach also correctly solves the equality problem where transient and non-transient entities
 * are compared and/or added to collections.
 * Even though these UUIDs seem like clutter in the database, they pragmatically relieve developers
 * from having to properly implement equals/hashcode by identifying business keys for every entity.
 * Of course, even with the UUIDs, developers should make sure to annotate business keys so that unique
 * constraints are generated in the DDL, even if these business keys are not used in equals/hashcode.
 */
@MappedSuperclass
public abstract class WritableEntity extends AuditableEntity {

    @Id
    @GeneratedValue(generator = "sequence")
    @GenericGenerator(name = "sequence", strategy = "com.purebred.core.util.TableNameSequenceGenerator")
    private Long id;

    @Column(unique = true, nullable = false, updatable = false)
    private String uuid;

    protected WritableEntity() {
        super();
        uuid = UUID.randomUUID().toString();
    }

    public Long getId() {
        return id;
    }

    /**
     * Get the randomly generated UUID that was created when this entity was constructed in memory
     *
     * @return UUID that was generated from UUID.randomUUID()
     */
    public String getUuid() {
        return uuid;
    }

    /**
     * Implements equals based on randomly generated UUID
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof WritableEntity)) return false;

        WritableEntity that = (WritableEntity) o;

        if (!getUuid().equals(that.getUuid())) return false;

        return true;
    }

    /**
     * Implements hashCode based on randomly generated UUID
     */
    @Override
    public int hashCode() {
        return getUuid().hashCode();
    }

    @Override
    public String toString() {
        return "WritableEntity{" +
                "uuid=" + getUuid() +
                '}';
    }

}
