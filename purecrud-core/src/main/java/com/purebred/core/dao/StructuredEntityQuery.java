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

package com.purebred.core.dao;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.List;

/**
 * A query structured to be re-executable as criteria, sort criteria and paging changes. Subclass
 * needs to implement methods for specifying criteria, parameters, sorting and fetch joins.
 *
 * @see EntityDao#execute(StructuredEntityQuery)
 *
 * @param <T> type of entity being queried
 */
public abstract class StructuredEntityQuery<T> extends EntityQuery<T> {

    /**
     * Build query criteria.
     *
     * @param builder used by implementation to build criteria
     * @param rootEntity root type in the from clause
     * @return a list of predicates, one for every part of the criteria
     */
    public abstract List<Predicate> buildCriteria(CriteriaBuilder builder, Root<T> rootEntity);

    /**
     * Set the parameter values for the query. The parameter names should matched those defined by the buildCriteria
     * implementation.
     *
     * @param typedQuery interface for setting parameters
     */
    public abstract void setParameters(TypedQuery typedQuery);

    /**
     * Build the Path used for sorting.
     *
     * @param rootEntity root type in the from clause
     * @return path used for sorting
     */
    public abstract Path buildOrderBy(Root<T> rootEntity);

    /**
     * Adding any fetch joins required to improve performance, i.e. to avoid N+1 select problem
     *
     * @param rootEntity root type in the from clause
     */
    public abstract void addFetchJoins(Root<T> rootEntity);
}
