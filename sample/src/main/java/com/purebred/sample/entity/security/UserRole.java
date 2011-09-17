package com.purebred.sample.entity.security;

import com.purebred.core.entity.security.AbstractRole;
import com.purebred.core.entity.security.AbstractUser;
import com.purebred.core.entity.security.AbstractUserRole;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Index;
import org.hibernate.annotations.NaturalId;

import javax.persistence.*;

@Entity
@Table
public class UserRole extends AbstractUserRole {
    public UserRole() {
    }

    public UserRole(User user, Role role) {
        super(user, role);
    }
}
