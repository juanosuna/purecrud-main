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

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Component
public class SpringApplicationContext implements ApplicationContextAware {

    private static ApplicationContext applicationContext;

    public void setApplicationContext(ApplicationContext context) throws BeansException {
        applicationContext = context;
    }

    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    public static Object getBean(String beanName) {
        return applicationContext.getBean(beanName);
    }

    public static void autowire(Object target) {
        if (getApplicationContext() != null && getApplicationContext().getAutowireCapableBeanFactory() != null) {
            SpringApplicationContext.getApplicationContext().getAutowireCapableBeanFactory().autowireBean(target);
        }
    }

    public static <T> Set<T> getBeansByType(Class<T> type) {
        Map beans = BeanFactoryUtils.beansOfTypeIncludingAncestors(applicationContext, type);
        return new HashSet(beans.values());
    }

    public static <T> T getBeanByTypeAndGenericArgumentType(Class<T> type, Class genericArgumentType) {
        Set<T> beans = getBeansByType(type);

        T foundBean = null;
        for (T bean : beans) {
            Class argType = ReflectionUtil.getGenericArgumentType(bean.getClass());
            if (argType != null && genericArgumentType.isAssignableFrom(argType)) {
                if (foundBean == null) {
                    foundBean = bean;
                } else {
                    throw new RuntimeException("More than on one bean found for type " + type
                            + " and generic argument type " + genericArgumentType);
                }
            }
        }

        if (foundBean != null) {
            return foundBean;
        } else {
            throw new RuntimeException("No bean found for type " + type
                    + " and generic argument type " + genericArgumentType);
        }
    }
}

