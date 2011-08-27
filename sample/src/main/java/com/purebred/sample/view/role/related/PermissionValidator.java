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

package com.purebred.sample.view.role.related;

import com.purebred.core.util.SpringApplicationContext;
import com.purebred.sample.dao.PermissionDao;
import com.purebred.sample.entity.security.Permission;

import javax.annotation.Resource;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.List;

public class PermissionValidator implements ConstraintValidator<ValidPermission, Permission> {

    @Resource
    private PermissionDao permissionDao;

    private ValidPermission validPermission;

    public PermissionValidator() {
        SpringApplicationContext.autowire(this);
    }

    @Override
    public void initialize(ValidPermission constraintAnnotation) {
        validPermission = constraintAnnotation;
    }

    @Override
    public boolean isValid(Permission permission, ConstraintValidatorContext context) {
        if (permission == null) return true;

        if (permission.getEntityType() != null) {
            List<Permission> result = permissionDao.findByRoleEntityTypeAndField(permission.getRole(), permission.getEntityType(), permission.getField());
            if (result.isEmpty()) {
                return true;
            } else {
                Permission existingPermission = result.get(0);
                if (!existingPermission.equals(permission)) {
                    context.disableDefaultConstraintViolation();
                    context.buildConstraintViolationWithTemplate(
                            "Existing permission entity already found for selection").addConstraintViolation();
                    return false;
                } else {
                    return true;
                }
            }
        } else {
            return true;
        }
    }
}
