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

package com.purebred.core.util;

import com.purebred.core.util.assertion.Assert;
import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.DynaProperty;
import org.apache.commons.beanutils.WrapDynaBean;
import org.apache.commons.lang.ClassUtils;
import org.springframework.beans.BeanUtils;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;

/**
 * User: Juan
 * Date: 5/9/11
 * Time: 2:09 PM
 */
public class ReflectionUtil {
    public static <T> T newInstance(Class<T> type) {
        try {
            return type.newInstance();
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public static Class getGenericArgumentType(Class clazz) {
        return getGenericArgumentType(clazz, 0);
    }

    public static Class getGenericArgumentType(Class clazz, int argIndex) {
        Type type = clazz.getGenericSuperclass();

        if (type != null && type instanceof ParameterizedType) {
            return (Class) ((ParameterizedType) type).getActualTypeArguments()[argIndex];
        } else {
            if (!(type instanceof Class) || type.equals(Object.class)) {
                return null;
            } else {
                return getGenericArgumentType((Class) type, argIndex);
            }
        }
    }

    public static Class getCollectionValueType(Class beanType, String beanProperty) {
        PropertyDescriptor descriptor = BeanUtils.getPropertyDescriptor(beanType, beanProperty);
        Class propertyType = descriptor.getPropertyType();
        Assert.PROGRAMMING.assertTrue(Collection.class.isAssignableFrom(propertyType),
                "Bean property not a collection type: " + beanType + "." + beanProperty);

        Type genericPropertyType = descriptor.getReadMethod().getGenericReturnType();
        Class collectionValueType = null;
        if (genericPropertyType != null && genericPropertyType instanceof ParameterizedType) {
            Type[] typeArgs = ((ParameterizedType) genericPropertyType).getActualTypeArguments();
            if (typeArgs != null && typeArgs.length > 0) {
                if (typeArgs.length == 1) {
                    collectionValueType = (Class) typeArgs[0];
                } else if (typeArgs.length == 2) {
                    collectionValueType = (Class) typeArgs[1];
                } else {
                    Assert.PROGRAMMING.fail("Collection type has more than two generic arguments");
                }
            }
        }

        return collectionValueType;
    }

    public static <T> T newInstance(Class<T> type, Class[] parameterTypes, Object[] args) {
        try {
            Constructor constructor = type.getDeclaredConstructor(parameterTypes);
            constructor.setAccessible(true);
            return (T) constructor.newInstance(args);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean isBeanEmpty(Object bean) {
        if (bean == null) {
            return true;
        }

        WrapDynaBean wrapDynaBean = new WrapDynaBean(bean);
        DynaProperty[] properties = wrapDynaBean.getDynaClass().getDynaProperties();
        for (DynaProperty property : properties) {
            String propertyName = property.getName();
            Class propertyType = property.getType();

            Object value = wrapDynaBean.get(propertyName);
            if (propertyType.isPrimitive()) {
                if (value instanceof Number && !value.toString().equals("0")) {
                    return false;
                } else if (value instanceof Boolean && !value.toString().equals("false")) {
                    return false;
                } else if (!value.toString().isEmpty()) {
                    return false;
                }
            } else if (value != null) {
                if (!(value instanceof Collection)) {
                    String convertedStringValue = ConvertUtils.convert(value);
                    if (!StringUtil.isEmpty(convertedStringValue)) {
                        return false;
                    }
                }
            }
        }

        return true;
    }

    public static Collection<String> findComplexProperties(Object bean) {
        Collection<String> complexProperties = new ArrayList<String>();

        WrapDynaBean wrapDynaBean = new WrapDynaBean(bean);
        DynaProperty[] properties = wrapDynaBean.getDynaClass().getDynaProperties();
        for (DynaProperty property : properties) {
            String propertyName = property.getName();
            Class propertyType = property.getType();
            if (!BeanUtils.isSimpleValueType(propertyType)) {
                complexProperties.add(propertyName);
            }
        }

        return complexProperties;
    }

    public static <T> T convertValue(Object value, Class<T> type) throws InvocationTargetException, IllegalAccessException, InstantiationException, NoSuchMethodException {
        Class clazz;
        if (type.isPrimitive()) {
            clazz = ClassUtils.primitiveToWrapper(type);
        } else {
            clazz = type;
        }

        if (null == value || clazz.isAssignableFrom(value.getClass())) {
            return (T) value;
        }

        Constructor<T> constructor = clazz.getConstructor(new Class[]{String.class});

        return constructor.newInstance(new Object[]{value.toString()});
    }

    public static boolean isNumberType(Class type) {
        Class clazz;
        if (type.isPrimitive()) {
            clazz = ClassUtils.primitiveToWrapper(type);
        } else {
            clazz = type;
        }

        return Number.class.isAssignableFrom(clazz);
    }
}
