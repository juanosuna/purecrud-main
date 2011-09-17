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

import com.purebred.core.util.ReflectionUtil;
import org.apache.commons.beanutils.PropertyUtils;

import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.List;

/**
 * Query that keeps track of the page while walking result set, page display size, sort property and other parameter
 * values for executing the JPA query. Subclasses should keep track of any specific parameter values required for each
 * entity type.
 *
 * @param <T> type of entity being queried
 */
public abstract class EntityQuery<T> {

    private Integer pageSize = 10;
    private Integer firstResult = 0;
    private Long resultCount = 0L;
    private String orderByPropertyId;
    private OrderDirection orderDirection = OrderDirection.ASC;

    private PropertyDescriptor[] descriptors;

    protected EntityQuery() {
        descriptors = PropertyUtils.getPropertyDescriptors(this);
    }

    /**
     * Execute the query. Subclass implementation should make the appropriate call to the DAO for this entity type.
     *
     * @return list of matching entities for the page range specified by this query
     */
    public abstract List<T> execute();

    /**
     * Can be overridden if any initialization is required after all Spring beans have been wired.
     * Overriding methods should call super.
     */
    public void postWire() {
    }

    /**
     * Get the number of records to display to the user in a given page.
     *
     * @return Number of records
     */
    public Integer getPageSize() {
        return pageSize;
    }

    /**
     * Set the number of records to display to the user in a given page.
     *
     * @param pageSize number of records
     */
    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    /**
     * Get the offset of the first record to display
     *
     * @return offset of the first record
     */
    public Integer getFirstResult() {
        return firstResult;
    }

    /**
     * Set the offset of the first record to display
     *
     * @param firstResult offset of the first record
     */
    public void setFirstResult(Integer firstResult) {
        this.firstResult = firstResult;
    }

    /**
     * Get the offset of the last record to display. This is derived from the first result and the page size.
     *
     * @return offset of the last record
     */
    public Integer getLastResult() {
        return Math.min(firstResult + pageSize, resultCount.intValue());
    }

    /**
     * Get a count of the number of results found after executing the query.
     *
     * @return number of results found
     */
    public Long getResultCount() {
        return resultCount;
    }

    void setResultCount(Long resultCount) {
        this.resultCount = resultCount;
    }

    /**
     * Set to first page in the result set
     */
    public void firstPage() {
        firstResult = 0;
    }

    /**
     * Set to next page in the result set
     */
    public void nextPage() {
        firstResult = Math.min(firstResult + pageSize, Math.max(resultCount.intValue() - pageSize, 0));
    }

    /**
     * Ask if there is a next page.
     *
     * @return true if there are some results after the current page
     */
    public boolean hasNextPage() {
        if (resultCount > 0) {
            return Math.min(firstResult + pageSize, Math.max(resultCount.intValue() - pageSize, 0)) > firstResult;
        } else {
            return false;
        }
    }

    /**
     * Set to previous page.
     */
    public void previousPage() {
        firstResult = Math.max(firstResult - pageSize, 0);
    }

    /**
     * Ask if there is previous page.
     *
     * @return true if there are some results before the current page
     */
    public boolean hasPreviousPage() {
        return Math.max(firstResult - pageSize, 0) < firstResult;
    }

    /**
     * Set to last page.
     */
    public void lastPage() {
        firstResult = Math.max(resultCount.intValue() - pageSize, 0);
    }

    /**
     * Get the property to used in the ORDER BY clause in the query
     *
     * @return name of the bean property
     */
    public String getOrderByPropertyId() {
        if (orderByPropertyId == null) {
            orderByPropertyId = "lastModified";
            setOrderDirection(OrderDirection.DESC);
        }

        return orderByPropertyId;
    }

    /**
     * Set the property to be used in the ORDER BY clause in the query.
     *
     * @param orderByPropertyId name of the bean property
     */
    public void setOrderByPropertyId(String orderByPropertyId) {
        this.orderByPropertyId = orderByPropertyId;
    }

    /**
     * Get the ORDER BY direction, i.e. ascending or descending
     *
     * @return ORDER BY direction
     */
    public OrderDirection getOrderDirection() {
        return orderDirection;
    }

    /**
     * Set ORDER BY direction, i.e. ascending or descending
     *
     * @param orderDirection ORDER BY direction
     */
    public void setOrderDirection(OrderDirection orderDirection) {
        this.orderDirection = orderDirection;
    }

    /**
     * Clear this query so that all filters and sort-criteria are removed. The method uses reflection
     * to clear any filters defined as bean properties by subclasses. Once cleared, re-execution of query
     * results in all records being found.
     * <p/>
     * If a subclass wants to apply a default filter that is always applied,
     * then subclass should override this method and re-apply this filter after calling super.clear().
     */
    public void clear() {
        setOrderByPropertyId(null);
        setOrderDirection(OrderDirection.ASC);

        try {
            for (PropertyDescriptor descriptor : descriptors) {
                Method writeMethod = descriptor.getWriteMethod();
                Method readMethod = descriptor.getReadMethod();
                if (readMethod != null && writeMethod != null
                        && !writeMethod.getDeclaringClass().equals(EntityQuery.class)
                        && !writeMethod.getDeclaringClass().equals(Object.class)) {
                    Class type = descriptor.getPropertyType();
                    if (type.isPrimitive() && !type.isArray()) {
                        if (ReflectionUtil.isNumberType(type)) {
                            writeMethod.invoke(this, 0);
                        } else if (Boolean.class.isAssignableFrom(type)) {
                            writeMethod.invoke(this, false);
                        }
                    } else {
                        writeMethod.invoke(this, new Object[]{null});
                    }
                }
            }
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Ask if given string is empty or null. Subclasses may use this convenient, utility method to determine if
     * parameter values are not empty and should be applied to the query.
     *
     * @param s string to check
     * @return true if empty or null
     */
    public static boolean isEmpty(String s) {
        return s == null || s.isEmpty();
    }

    /**
     * Ask if given object is null. Subclasses may use this convenient, utility method to determine if
     * parameter values are not empty and should be applied to the query.
     *
     * @param o object to check
     * @return true if null
     */
    public static boolean isEmpty(Object o) {
        return o == null;
    }

    /**
     * Ask if given collection is empty or null. Subclasses may use this convenient, utility method to determine if
     * parameter values are not empty and should be applied to the query.
     *
     * @param c collection to check
     * @return true if empty or null
     */
    public static boolean isEmpty(Collection c) {
        return c == null || c.isEmpty();
    }

    @Override
    public String toString() {
        return "EntityQuery{" +
                "pageSize=" + pageSize +
                ", firstResult=" + firstResult +
                '}';
    }

    /**
     * Order direction, i.e. ascending or descending
     */
    public enum OrderDirection {
        /**
         * Ascending
         */
        ASC,
        /**
         * Descending
         */
        DESC
    }
}
