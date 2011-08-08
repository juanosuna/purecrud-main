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
