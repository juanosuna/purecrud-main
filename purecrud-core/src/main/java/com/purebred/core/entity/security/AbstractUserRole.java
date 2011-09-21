/*
 * Copyright (c) 2011 Brown Bag Consulting.
 * This file is part of the PureCRUD project.
 * Author: Juan Osuna
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License Version 3
 * as published by the Free Software Foundation with the addition of the
 * following permission added to Section 15 as permitted in Section 7(a):
 * FOR ANY PART OF THE COVERED WORK IN WHICH THE COPYRIGHT IS OWNED BY
 * Brown Bag Consulting, Brown Bag Consulting DISCLAIMS THE WARRANTY OF
 * NON INFRINGEMENT OF THIRD PARTY RIGHTS.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 * The interactive user interfaces in modified source and object code versions
 * of this program must display Appropriate Legal Notices, as required under
 * Section 5 of the GNU Affero General Public License.
 *
 * You can be released from the requirements of the license by purchasing
 * a commercial license. Buying such a license is mandatory as soon as you
 * develop commercial activities involving the PureCRUD software without
 * disclosing the source code of your own applications. These activities
 * include: offering paid services to customers as an ASP, providing
 * services from a hosted web application, shipping PureCRUD with a closed
 * source product.
 *
 * For more information, please contact Brown Bag Consulting at this
 * address: juan@brownbagconsulting.com.
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
