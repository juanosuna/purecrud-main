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

package com.purebred.core.util.assertion;


import java.lang.reflect.InvocationTargetException;

public enum AssertionExceptionType {
    CONFIGURATION_EXCEPTION(ConfigurationException.class),
    PROGRAMMING_EXCEPTION(ProgrammingException.class),
    BUSINESS_EXCEPTION(BusinessException.class),
    DATABASE_EXCEPTION(DatabaseException.class),
    SYSTEM_EXCEPTION(SystemException.class);

    private Class<AssertionException> exceptionType;

    private AssertionExceptionType(Class exceptionType) {
        this.exceptionType = exceptionType;
    }

    /**
     * Reflectively instantiates exception based on type
     *
     * @return exception instance based on type
     */
    public AssertionException create() {
        try {
            return exceptionType.getConstructor().newInstance();
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Reflectively instantiates exception based on type
     *
     * @param message to pass to exception's constructor
     * @return instance of this exception's type
     */
    public AssertionException create(String message) {
        try {
            return exceptionType.getConstructor(String.class).newInstance(message);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Reflectively instantiates exception based on type
     *
     * @param cause to pass to exception's constructor
     * @return instance of this exception's type
     */
    public AssertionException create(Throwable cause) {
        try {
            return exceptionType.getConstructor(Throwable.class).newInstance(cause);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * Reflectively instantiates exception based on type
     *
     * @param message to pass to exception's constructor
     * @param cause   to pass to exception's constructor
     * @return instance of this exception's type
     */
    public AssertionException create(String message, Throwable cause) {
        try {
            return exceptionType.getConstructor(String.class, Throwable.class).newInstance(message, cause);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        }
    }
}
