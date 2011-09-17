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

import com.purebred.core.entity.AuditableEntity;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Index;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Table
public abstract class AbstractUserRole extends AuditableEntity {

    @EmbeddedId
    private Id id = new Id();

    @Index(name = "IDX_USER_ROLE_USER")
    @ForeignKey(name = "FK_USER_ROLE_USER")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(insertable = false, updatable = false)
    private AbstractUser user;

    @Index(name = "IDX_USER_ROLE_ROLE")
    @ForeignKey(name = "FK_USER_ROLE_ROLE")
    @JoinColumn(insertable = false, updatable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private AbstractRole role;

    public AbstractUserRole(Long userId, Long roleId) {
        id.userId = userId;
        id.roleId = roleId;
    }

    public AbstractUserRole() {
    }

    public AbstractUserRole(AbstractUser user, AbstractRole role) {
        this(user.getId(), role.getId());
        this.user = user;
        this.role = role;
    }

    public Id getId() {
        return id;
    }

    public AbstractUser getUser() {
        return user;
    }

    public AbstractRole getRole() {
        return role;
    }

    @Embeddable
    public static class Id implements Serializable {
        @Column(name = "USER_ID")
        private Long userId;
        @Column(name = "ROLE_ID")
        private Long roleId;

        public Id() {
        }

        public Id(Long userId, Long roleId) {
            this.userId = userId;
            this.roleId = roleId;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Id)) return false;

            Id id = (Id) o;

            if (!roleId.equals(id.roleId)) return false;
            if (!userId.equals(id.userId)) return false;

            return true;
        }

        @Override
        public int hashCode() {
            int result = userId.hashCode();
            result = 31 * result + roleId.hashCode();
            return result;
        }
    }
}
