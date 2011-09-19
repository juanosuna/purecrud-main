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

package com.purebred.core.util;

import com.purebred.core.util.assertion.Assert;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * User: Juan
 * Date: 6/4/11
 * Time: 11:25 PM
 */
public class MethodDelegate {
    private Object target;
    private Method method;

    public MethodDelegate(Object target, String methodName, Class<?>... parameterTypes) {
        this.target = target;
        method = ReflectionUtil.getMethod(target.getClass(), methodName, parameterTypes);
        Assert.PROGRAMMING.assertTrue(method != null, "Cannot find method " + target.getClass().getName()
                +"." + methodName + parameterTypes == null ? "" : " (" + parameterTypes + ")");
    }

    public Object execute(Object... args) {
        try {
            method.setAccessible(true);
            return method.invoke(target, args);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }
}
