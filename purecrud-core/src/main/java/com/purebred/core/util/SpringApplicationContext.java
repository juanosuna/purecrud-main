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
            if (argType != null && genericArgumentType.equals(argType)) {
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

    public static <T> Set<T> getBeansByTypeAndGenericArgumentType(Class<T> type, Class genericArgumentType) {
        Set<T> beans = getBeansByType(type);
        Set<T> beansWithGenericArgumentType = new HashSet<T>();

        for (T bean : beans) {
            Class argType = ReflectionUtil.getGenericArgumentType(bean.getClass());
            if (argType != null && genericArgumentType.isAssignableFrom(argType)) {
                beansWithGenericArgumentType.add(bean);
            }
        }

        return beansWithGenericArgumentType;
    }
}

