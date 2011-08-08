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
 * patents in process, and are protected by trade secret or copyrightlaw.
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

public abstract class EntityQuery<T> {

    private Integer pageSize = 10;
    private Integer firstResult = 0;
    private Long resultCount = 0L;
    private String orderByPropertyId;
    private OrderDirection orderDirection = OrderDirection.ASC;

    public abstract List<T> execute();

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public void setFirstResult(Integer firstResult) {
        this.firstResult = firstResult;
    }

    public Integer getFirstResult() {
        return firstResult;
    }

    public Integer getLastResult() {
        return Math.min(firstResult + pageSize, resultCount.intValue());
    }

    public Long getResultCount() {
        return resultCount;
    }

    public void setResultCount(Long resultCount) {
        this.resultCount = resultCount;
    }

    public void firstPage() {
        firstResult = 0;
    }

    public void nextPage() {
        firstResult = Math.min(firstResult + pageSize, Math.max(resultCount.intValue() - pageSize, 0));
    }

    public boolean hasNextPage() {
        if (resultCount > 0) {
            return Math.min(firstResult + pageSize, Math.max(resultCount.intValue() - pageSize, 0)) > firstResult;
        } else {
            return false;
        }
    }

    public void previousPage() {
        firstResult = Math.max(firstResult - pageSize, 0);
    }

    public boolean hasPreviousPage() {
        return Math.max(firstResult - pageSize, 0) < firstResult;
    }

    public void lastPage() {
        firstResult = Math.max(resultCount.intValue() - pageSize, 0);
    }

    public String getOrderByPropertyId() {
        if (orderByPropertyId == null) {
            orderByPropertyId = "lastModified";
            setOrderDirection(OrderDirection.DESC);
        }

        return orderByPropertyId;
    }

    public void setOrderByPropertyId(String orderByPropertyId) {
        this.orderByPropertyId = orderByPropertyId;
    }

    public OrderDirection getOrderDirection() {
        return orderDirection;
    }

    public void setOrderDirection(OrderDirection orderDirection) {
        this.orderDirection = orderDirection;
    }

    public void clear() {
        setOrderByPropertyId(null);
        setOrderDirection(OrderDirection.ASC);

        try {
            PropertyDescriptor[] descriptors = PropertyUtils.getPropertyDescriptors(this);
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

    public enum OrderDirection {
        ASC,
        DESC
    }

    public boolean isEmpty(String s) {
        return s == null || s.isEmpty();
    }

    public boolean isEmpty(Object o) {
        return o == null;
    }

    public boolean isEmpty(Collection c) {
        return c == null || c.isEmpty();
    }

    @Override
    public String toString() {
        return "EntityQuery{" +
                "pageSize=" + pageSize +
                ", firstResult=" + firstResult +
                '}';
    }
}
