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

package com.purebred.core.dao;

import com.purebred.core.entity.IdentifiableEntity;
import com.purebred.core.util.ReflectionUtil;
import com.sun.deploy.panel.CacheSettingsDialog;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Repository
public abstract class EntityDao<T, ID extends Serializable> {

    @PersistenceContext
    private EntityManager entityManager;

    private Class<T> entityType;
    private Class<ID> idType;

    public EntityDao() {
        entityType = ReflectionUtil.getGenericArgumentType(getClass());
        idType = ReflectionUtil.getGenericArgumentType(getClass(), 1);
    }

    public EntityManager getEntityManager() {
        return entityManager;
    }

    protected Class<T> getEntityType() {
        if (entityType == null) {
            throw new UnsupportedOperationException();
        }

        return entityType;
    }

    protected Class<ID> getIdType() {
        if (idType == null) {
            throw new UnsupportedOperationException();
        }

        return idType;
    }

    @Transactional
    public void remove(T entity) {
        T attachedEntity = getReference(entity);
        getEntityManager().remove(attachedEntity);
    }

    public T getReference(T entity) {
        Object primaryKey = ((IdentifiableEntity) entity).getId();
        return getEntityManager().getReference(getEntityType(), primaryKey);
    }

    public List<T> findAll() {
        return executeQuery("select c from " + getEntityType().getSimpleName() + " c");
    }

    public List<T> findAll(Class<T> t) {
        return executeQuery("select c from " + t.getSimpleName() + " c");
    }

    public List<T> executeQuery(String query) {
        return getEntityManager().createQuery(query).getResultList();
    }

    public List<T> executeNativeQuery(String query) {
        return getEntityManager().createNativeQuery(query).getResultList();
    }

    public List<T> executeQuery(String query, int firstResult, int maxResults) {
        Query q = getEntityManager().createQuery(query);
        q.setFirstResult(firstResult);
        q.setMaxResults(maxResults);

        return q.getResultList();
    }

    public T find(ID id) {
        return getEntityManager().find(getEntityType(), id);
    }

    public T find(Class<T> t, ID id) {
        return getEntityManager().find(t, id);
    }

    public T findByBusinessKey(String propertyName, Object propertyValue) {
        Session session = (Session) getEntityManager().getDelegate();

        Criteria criteria = session.createCriteria(getEntityType());
        criteria.add(Restrictions.naturalId().set(propertyName, propertyValue));
        criteria.setCacheable(true);

        return (T) criteria.uniqueResult();
    }

    public List<T> findByProperty(String propertyName, Object propertyValue) {
        Query query = getEntityManager().createQuery("select e from " + getEntityType().getSimpleName()
                + " e WHERE e." + propertyName + "=:propertyValue");

        query.setParameter("propertyValue", propertyValue);

        return query.getResultList();
    }

    @Transactional
    public void delete(T entity) {
        Query query = getEntityManager().createQuery("delete from " + getEntityType().getSimpleName()
                + " c where c = :entity");

        query.setParameter("entity", entity);

        query.executeUpdate();
    }

    @Transactional
    public T merge(T entity) {
        return getEntityManager().merge(entity);
    }

    @Transactional
    public void persist(T entity) {
        getEntityManager().persist(entity);
    }

    @Transactional
    public void persist(Collection<T> entities) {
        for (T entity : entities) {
            persist(entity);
        }
    }

    public void refresh(T entity) {
        getEntityManager().refresh(entity);
    }

    public T persistOrMerge(T entity) {
        if (isPersistent(entity)) {
            return merge(entity);
        } else {
            persist(entity);
            return entity;
        }
    }

    public boolean isPersistent(T entity) {
        ID id = (ID) getEntityManager().getEntityManagerFactory().getPersistenceUnitUtil().getIdentifier(entity);
        if (id == null) {
            return false;
        } else {
            T existingEntity = find(id);
            return existingEntity != null;
        }
    }

    public List<T> execute(StructuredEntityQuery structuredEntityQuery) {
        return new StructuredQueryExecutor(structuredEntityQuery).execute();
    }

    public List<T> execute(ToManyRelationshipQuery toManyRelationshipQuery) {
        return new ToManyRelationshipQueryExecutor(toManyRelationshipQuery).execute();
    }

    public class StructuredQueryExecutor {

        private StructuredEntityQuery structuredQuery;

        public StructuredQueryExecutor(StructuredEntityQuery structuredQuery) {
            this.structuredQuery = structuredQuery;
        }

        public StructuredEntityQuery getStructuredQuery() {
            return structuredQuery;
        }

        public List<T> execute() {
            List<ID> count = executeImpl(true);
            structuredQuery.setResultCount((Long) count.get(0));

            if (structuredQuery.getResultCount() > 0) {
                List<ID> ids = executeImpl(false);
                return findByIds(ids);
            } else {
                return new ArrayList<T>();
            }
        }

        private List<ID> executeImpl(boolean isCount) {
            CriteriaBuilder builder = getEntityManager().getCriteriaBuilder();
            CriteriaQuery c = builder.createQuery();
            Root<T> rootEntity = c.from(getEntityType());

            if (isCount) {
                c.select(builder.count(rootEntity));
            } else {
                c.select(rootEntity.get("id"));
            }

            List<Predicate> criteria = structuredQuery.buildCriteria(builder, rootEntity);
            c.where(builder.and(criteria.toArray(new Predicate[0])));

            if (!isCount && structuredQuery.getOrderByPropertyId() != null) {
                Path path = structuredQuery.buildOrderBy(rootEntity);
                if (path == null) {
                    path = rootEntity.get(structuredQuery.getOrderByPropertyId());
                }
                if (structuredQuery.getOrderDirection().equals(EntityQuery.OrderDirection.ASC)) {
                    c.orderBy(builder.asc(path));
                } else {
                    c.orderBy(builder.desc(path));
                }
            }

            TypedQuery<ID> typedQuery = getEntityManager().createQuery(c);
            structuredQuery.setParameters(typedQuery);

            if (!isCount) {
                typedQuery.setFirstResult(structuredQuery.getFirstResult());
                typedQuery.setMaxResults(structuredQuery.getPageSize());
            }

            return typedQuery.getResultList();
        }

        private List<T> findByIds(List<ID> ids) {
            CriteriaBuilder builder = getEntityManager().getCriteriaBuilder();
            CriteriaQuery<T> c = builder.createQuery(getEntityType());
            Root<T> rootEntity = c.from(getEntityType());
            c.select(rootEntity);

            structuredQuery.addFetchJoins(rootEntity);

            List<Predicate> criteria = new ArrayList<Predicate>();
            ParameterExpression<List> p = builder.parameter(List.class, "ids");
            criteria.add(builder.in(rootEntity.get("id")).value(p));

            c.where(builder.and(criteria.toArray(new Predicate[0])));

            if (structuredQuery.getOrderByPropertyId() != null) {
                Path path = structuredQuery.buildOrderBy(rootEntity);
                if (path == null) {
                    path = rootEntity.get(structuredQuery.getOrderByPropertyId());
                }
                if (structuredQuery.getOrderDirection().equals(EntityQuery.OrderDirection.ASC)) {
                    c.orderBy(builder.asc(path));
                } else {
                    c.orderBy(builder.desc(path));
                }
            }

            TypedQuery<T> q = getEntityManager().createQuery(c);
            q.setParameter("ids", ids);

            return q.getResultList();
        }
    }

    public class ToManyRelationshipQueryExecutor extends StructuredQueryExecutor {
        public ToManyRelationshipQueryExecutor(ToManyRelationshipQuery toManyRelationshipQuery) {
            super(toManyRelationshipQuery);
        }

        @Override
        public ToManyRelationshipQuery getStructuredQuery() {
            return (ToManyRelationshipQuery) super.getStructuredQuery();
        }

        @Override
        public List<T> execute() {
            if (getStructuredQuery().getParent() == null) {
                return new ArrayList();
            } else {
                return super.execute();
            }
        }
    }
}
