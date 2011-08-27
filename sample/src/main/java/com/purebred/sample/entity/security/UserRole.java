package com.purebred.sample.entity.security;

import com.purebred.core.entity.security.AbstractUserRole;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Index;
import org.hibernate.annotations.NaturalId;

import javax.persistence.*;

/**
 * User: Juan
 * Date: 8/18/11
 */
@Entity
@Table
public class UserRole extends AbstractUserRole {

//    @Index(name = "IDX_USER_ROLE_USER")
    @ForeignKey(name = "FK_USER_ROLE_USER")
//    @NaturalId
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(insertable = false, updatable = false)
    private User user;

//    @Index(name = "IDX_USER_ROLE_ROLE")
    @ForeignKey(name = "FK_USER_ROLE_ROLE")
//    @NaturalId
    @JoinColumn(insertable = false, updatable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Role role;

    public UserRole() {
    }

    public UserRole(User user, Role role) {
        super(user.getId(), role.getId());
        this.user = user;
        this.role = role;
    }

    public User getUser() {
        return user;
    }

    public Role getRole() {
        return role;
    }
}
