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

package com.purebred.sample.entity;


import com.purebred.core.entity.WritableEntity;
import com.purebred.sample.util.ValidPhone;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Index;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotBlank;

import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import javax.validation.constraints.Size;
import java.util.Date;

@Entity
@Table
public class Contact extends WritableEntity {

    public static final String DEFAULT_PHONE_COUNTRY = "US";

    private String firstName;

    private String lastName;

    private String title;

    @Temporal(TemporalType.DATE)
    private Date birthDate;

    private String department;

    private String email;

    private boolean doNotEmail;

    @Embedded
    private Phone mainPhone;

    @Embedded
    private Phone otherPhone;

    private boolean doNotCall;

    @Index(name = "IDX_CONTACT_LEAD_SOURCE")
    @ForeignKey(name = "FK_CONTACT_LEAD_SOURCE")
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.REFRESH)
    private LeadSource leadSource;

    @Index(name = "IDX_CONTACT_ACCOUNT")
    @ForeignKey(name = "FK_CONTACT_ACCOUNT")
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.REFRESH)
    private Account account;

    @Index(name = "IDX_CONTACT_ASSIGNED_TO")
    @ForeignKey(name = "FK_CONTACT_ASSIGNED_TO")
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.REFRESH)
    private User assignedTo;

    @Index(name = "IDX_CONTACT_REPORTS_TO")
    @ForeignKey(name = "FK_CONTACT_REPORTS_TO")
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.REFRESH)
    private Contact reportsTo;

    @Index(name = "IDX_CONTACT_MAILING_ADDRESS")
    @ForeignKey(name = "FK_CONTACT_MAILING_ADDRESS")
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private Address mailingAddress = new Address(AddressType.MAILING);

    @Index(name = "IDX_CONTACT_OTHER_ADDRESS")
    @ForeignKey(name = "FK_CONTACT_OTHER_ADDRESS")
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private Address otherAddress;

    @Lob
    private String description;

    public Contact() {
    }

    public Contact(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
    }

    @NotBlank
    @NotNull
    @Size(min = 1, max = 64)
    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    @NotBlank
    @NotNull
    @Size(min = 1, max = 64)
    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getName() {
        return getLastName() + ", " + getFirstName();
    }

    @Size(min = 1, max = 64)
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Past
    public Date getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(Date birthDate) {
        this.birthDate = birthDate;
    }

    @Size(min = 1, max = 64)
    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    @Email
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isDoNotEmail() {
        return doNotEmail;
    }

    public void setDoNotEmail(boolean doNotEmail) {
        this.doNotEmail = doNotEmail;
    }

    @NotNull
    @ValidPhone
    public Phone getMainPhone() {
        return mainPhone;
    }

    public void setMainPhone(Phone mainPhone) {
        this.mainPhone = mainPhone;
    }

    public Phone getOtherPhone() {
        return otherPhone;
    }

    public void setOtherPhone(Phone otherPhone) {
        this.otherPhone = otherPhone;
    }

    public boolean isDoNotCall() {
        return doNotCall;
    }

    public void setDoNotCall(boolean doNotCall) {
        this.doNotCall = doNotCall;
    }

    public LeadSource getLeadSource() {
        return leadSource;
    }

    public void setLeadSource(LeadSource leadSource) {
        this.leadSource = leadSource;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public User getAssignedTo() {
        return assignedTo;
    }

    public void setAssignedTo(User assignedTo) {
        this.assignedTo = assignedTo;
    }

    public Contact getReportsTo() {
        return reportsTo;
    }

    public void setReportsTo(Contact reportsTo) {
        this.reportsTo = reportsTo;
    }

    @Valid
    @NotNull
    public Address getMailingAddress() {
        return mailingAddress;
    }

    public void setMailingAddress(Address mailingAddress) {
        if (mailingAddress != null) {
            mailingAddress.setAddressType(AddressType.MAILING);
        }
        this.mailingAddress = mailingAddress;
    }

    @Valid
    public Address getOtherAddress() {
        return otherAddress;
    }

    public void setOtherAddress(Address otherAddress) {
        if (otherAddress != null) {
            otherAddress.setAddressType(AddressType.OTHER);
        }
        this.otherAddress = otherAddress;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @PreRemove
    public void preRemove() {
        setAccount(null);
    }
}