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

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EmbeddedId;
import javax.persistence.MappedSuperclass;
import java.io.Serializable;

@MappedSuperclass
public abstract class AbstractUserRole extends AuditableEntity {

    @EmbeddedId
    private Id id = new Id();

    public AbstractUserRole(Long userId, Long roleId) {
        id.userId = userId;
        id.roleId = roleId;
    }

    public AbstractUserRole() {
    }

    public Id getId() {
        return id;
    }

    public abstract AbstractUser getUser();

    public abstract AbstractRole getRole();

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
