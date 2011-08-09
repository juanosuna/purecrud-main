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

package com.vaadin.data.util;

import com.vaadin.terminal.ErrorMessage;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

public class EnhancedNestedMethodProperty extends NestedMethodProperty {

    private Object instance;

    public EnhancedNestedMethodProperty(Object instance, String propertyName) {
        super(instance, propertyName);
        this.instance = instance;
    }

    public EnhancedNestedMethodProperty(Class<?> instanceClass, String propertyName) {
        super(instanceClass, propertyName);
    }

    @Override
    public Object getValue() {
        if (hasNullInPropertyPath()) {
            return null;
        } else {
            return super.getValue();
        }
    }

    public boolean hasNullInPropertyPath() {
        try {
            List<Method> getMethods = getGetMethods();
            Object object = instance;
            for (Method getMethod : getMethods) {
                if (object == null) {
                    return true;
                } else {
                    object = getMethod.invoke(object);
                }
            }
            return false;
        } catch (final InvocationTargetException e) {
            throw new MethodProperty.MethodException(this, e.getTargetException());
        } catch (final Exception e) {
            throw new MethodProperty.MethodException(this, e);
        }
    }

    @Override
    protected void invokeSetMethod(Object value) {
        if (hasNullInPropertyPath()) {
            if (value != null) {
                fillNullsInPropertyPath();
                super.invokeSetMethod(value);
            }
        } else {
            super.invokeSetMethod(value);
        }
    }

    private void fillNullsInPropertyPath() {
        try {
            List<Method> getMethods = getGetMethods();
            Object parent = null;
            Method parentGetMethod = null;
            Object object = instance;
            for (Method getMethod : getMethods) {
                if (object == null && parent != null) {
                    Class returnType = parentGetMethod.getReturnType();
                    String getMethodName = parentGetMethod.getName();
                    Class declaringClass = parentGetMethod.getDeclaringClass();
                    Method setMethod = getSetMethod(declaringClass, returnType, getMethodName);
                    Object fillerInstance = returnType.newInstance();
                    setMethod.invoke(parent, fillerInstance);
                } else {
                    parent = object;
                    parentGetMethod = getMethod;
                    object = getMethod.invoke(parent);
                }
            }
        } catch (final InvocationTargetException e) {
            throw new MethodProperty.MethodException(this, e.getTargetException());
        } catch (final Exception e) {
            throw new MethodProperty.MethodException(this, e);
        }
    }

    private Method getSetMethod(Class containerType, Class propertyType, String getMethodName) {
        try {
            String setMethodName = "set" + getMethodName.substring(3);
            return containerType.getMethod(setMethodName, new Class[]{propertyType});
        } catch (NoSuchMethodException e) {
            throw new MethodProperty.MethodException(this, e);
        }
    }
}
