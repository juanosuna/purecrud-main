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

package com.purebred.core.validation;

import org.springframework.stereotype.Service;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import javax.validation.constraints.NotNull;
import javax.validation.metadata.BeanDescriptor;
import javax.validation.metadata.ConstraintDescriptor;
import javax.validation.metadata.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.util.HashSet;
import java.util.Set;

@Service
public class Validation {
    private Validator validator;
    private ValidatorFactory factory;

    public Validation() {
        factory = javax.validation.Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    public ValidatorFactory getFactory() {
        return factory;
    }

    public <T> Set<ConstraintViolation<T>> validate(T object, Class<?>... groups) {
        return validator.validate(object, groups);
    }

    private <T> ConstraintViolation findNotNullViolation(Set<ConstraintViolation<T>> violations) {
        for (ConstraintViolation violation : violations) {
            if (violation.getConstraintDescriptor().getAnnotation().annotationType().equals(NotNull.class)) {
                return violation;
            }
        }

        return null;
    }

    public <T> Set<ConstraintViolation<T>> validateProperty(T object, String propertyPath, Class<?>... groups) {

        Set<ConstraintViolation<T>> violations = new HashSet<ConstraintViolation<T>>();

        try {
            int currentIndex = -1;
            String currentPropertyPath;

            do {
                currentIndex = propertyPath.indexOf(".", ++currentIndex);
                if (currentIndex >= 0) {
                    currentPropertyPath = propertyPath.substring(0, currentIndex);
                } else {
                    currentPropertyPath = propertyPath;
                }

                Set<ConstraintViolation<T>> currentViolations = validator.validateProperty(object, currentPropertyPath, groups);

                ConstraintViolation<T> notNullViolation = findNotNullViolation(currentViolations);
                if (notNullViolation == null) {
                    violations = currentViolations;
                } else {
                    violations.add(notNullViolation);
                    break;
                }
            } while (currentIndex >= 0);
        } catch (IllegalArgumentException e) {
            // ignore null property path
        }

        return violations;
    }

    public <T> Set<ConstraintViolation<T>> validateValue(Class<T> beanType, String propertyName, Object value, Class<?>... groups) {
        return validator.validateValue(beanType, propertyName, value, groups);
    }

    public BeanDescriptor getConstraintsForClass(Class<?> clazz) {
        return validator.getConstraintsForClass(clazz);
    }

    public <T> T unwrap(Class<T> type) {
        return validator.unwrap(type);
    }

    public boolean isCascaded(Class beanClass, String propertyName) {
        PropertyDescriptor descriptor = validator.getConstraintsForClass(beanClass).getConstraintsForProperty(propertyName);
        return descriptor != null && descriptor.isCascaded();
    }

    public boolean isRequired(Class beanClass, String propertyName) {
        return hasAnnotation(beanClass, propertyName, NotNull.class);
    }

    public boolean hasAnnotation(Class beanClass, String propertyName, Class annotationClass) {
        PropertyDescriptor descriptor = validator.getConstraintsForClass(beanClass).getConstraintsForProperty(propertyName);
        if (descriptor != null) {
            for (ConstraintDescriptor<?> d : descriptor.getConstraintDescriptors()) {
                Annotation annotation = d.getAnnotation();
                if (annotationClass.isAssignableFrom(annotation.getClass())) {
                    return true;
                }
            }
        }

        return false;
    }
}
