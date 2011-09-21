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

package com.purebred.sample.dao;

import com.purebred.core.dao.EntityDao;
import com.purebred.core.entity.security.AbstractPermission;
import com.purebred.core.entity.security.AbstractRole;
import com.purebred.sample.entity.security.Permission;
import com.purebred.sample.entity.security.Role;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.FlushModeType;
import javax.persistence.Query;
import java.util.List;

import static com.purebred.sample.dao.CacheSettings.setReadOnly;

@Repository
public class PermissionDao extends EntityDao<Permission, Long> {

    @Override
    public List<Permission> findAll() {
        Query query = getEntityManager().createQuery("SELECT p FROM Permission p ORDER BY p.entityType, p.field");
        setReadOnly(query);

        return query.getResultList();
    }

    public List<Permission> findByRole(Role role) {
        Query query = getEntityManager().createQuery("SELECT p FROM Permission p WHERE p.role = :role");
        query.setParameter("role", role);

        return query.getResultList();
    }

    public List<Permission> findByRoleEntityTypeAndField(AbstractRole role, String entityType, String field) {
        Query query = getEntityManager().createQuery("SELECT p FROM Permission p WHERE p.role = :role" +
                " AND p.entityType = :entityType AND p.field = :field");
        query.setParameter("role", role);
        query.setParameter("entityType", entityType);
        query.setParameter("field", field);

        query.setFlushMode(FlushModeType.COMMIT);

        return query.getResultList();
    }
}
