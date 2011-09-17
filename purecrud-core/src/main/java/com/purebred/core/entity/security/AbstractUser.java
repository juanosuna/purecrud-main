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

package com.purebred.core.entity.security;

import com.purebred.core.entity.WritableEntity;
import org.hibernate.annotations.NaturalId;
import org.hibernate.validator.constraints.NotBlank;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.HashSet;
import java.util.Set;

@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Table
public abstract class AbstractUser extends WritableEntity {
    @NaturalId
    private String loginName;
    private String loginPassword;
    private boolean accountExpired = false;
    private boolean accountLocked = false;
    private boolean credentialsExpired = false;
    private boolean enabled = true;

    @OneToMany(mappedBy = "user")
    private Set<AbstractUserRole> userRoles = new HashSet<AbstractUserRole>();

    public AbstractUser() {
    }

    public AbstractUser(String loginName, String loginPassword) {
        this.loginName = loginName;
        this.loginPassword = loginPassword;
    }

    @NotBlank
    @NotNull
    @Size(min = 4, max = 16)
    public String getLoginName() {
        return loginName;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }

    @NotBlank
    @NotNull
    @Size(min = 4, max = 16)
    public String getLoginPassword() {
        return loginPassword;
    }

    public void setLoginPassword(String loginPassword) {
        this.loginPassword = loginPassword;
    }

    public boolean isAccountExpired() {
        return accountExpired;
    }

    public void setAccountExpired(boolean accountExpired) {
        this.accountExpired = accountExpired;
    }

    public boolean isAccountLocked() {
        return accountLocked;
    }

    public void setAccountLocked(boolean accountLocked) {
        this.accountLocked = accountLocked;
    }

    public boolean isCredentialsExpired() {
        return credentialsExpired;
    }

    public void setCredentialsExpired(boolean credentialsExpired) {
        this.credentialsExpired = credentialsExpired;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public Set<AbstractUserRole> getUserRoles() {
        return userRoles;
    }

    public void setUserRoles(Set<AbstractUserRole> userRoles) {
        this.userRoles = userRoles;
    }

    public Set<AbstractRole> getRoles() {
        Set<AbstractRole> roles = new HashSet<AbstractRole>();

        Set<AbstractUserRole> userRoles = (Set<AbstractUserRole>) getUserRoles();
        for (AbstractUserRole userRole : userRoles) {
            roles.add(userRole.getRole());
        }

        return roles;
    }

    public boolean isViewAllowed(String entityType) {
        Set<AbstractUserRole> roles = (Set<AbstractUserRole>) getUserRoles();
        for (AbstractUserRole role : roles) {
            if (role.getRole().isViewAllowed(entityType)) {
                return true;
            }
        }

        return false;
    }

    public boolean isEditAllowed(String entityType) {
        Set<AbstractUserRole> roles = (Set<AbstractUserRole>) getUserRoles();
        for (AbstractUserRole role : roles) {
            if (role.getRole().isEditAllowed(entityType)) {
                return true;
            }
        }

        return false;
    }

    public boolean isCreateAllowed(String entityType) {
        Set<AbstractUserRole> roles = (Set<AbstractUserRole>) getUserRoles();
        for (AbstractUserRole role : roles) {
            if (role.getRole().isCreateAllowed(entityType)) {
                return true;
            }
        }

        return false;
    }

    public boolean isDeleteAllowed(String entityType) {
        Set<AbstractUserRole> roles = (Set<AbstractUserRole>) getUserRoles();
        for (AbstractUserRole role : roles) {
            if (role.getRole().isDeleteAllowed(entityType)) {
                return true;
            }
        }

        return false;
    }

    public boolean isViewAllowed(String entityType, String field) {
        Set<AbstractUserRole> roles = (Set<AbstractUserRole>) getUserRoles();
        for (AbstractUserRole role : roles) {
            if (role.getRole().isViewAllowed(entityType, field)) {
                return true;
            }
        }

        return false;
    }

    public boolean isEditAllowed(String entityType, String field) {
        Set<AbstractUserRole> roles = (Set<AbstractUserRole>) getUserRoles();
        for (AbstractUserRole role : roles) {
            if (role.getRole().isEditAllowed(entityType, field)) {
                return true;
            }
        }

        return false;
    }
}
