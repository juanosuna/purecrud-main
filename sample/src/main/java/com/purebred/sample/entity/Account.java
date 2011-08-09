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
import com.purebred.core.view.entity.field.format.DefaultFormats;
import com.purebred.sample.service.ecbfx.EcbfxService;
import com.purebred.sample.util.ValidPhone;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Index;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotBlank;
import org.joda.money.IllegalCurrencyException;

import javax.annotation.Resource;
import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashSet;
import java.util.Set;

import static com.purebred.core.util.ObjectUtil.isEqual;

@Entity
@Table
public class Account extends WritableEntity {

    @Resource
    @Transient
    private DefaultFormats defaultFormat;

    @Resource
    @Transient
    private EcbfxService ecbfxService;

    private String name;

    private String website;

    private String tickerSymbol;

    private String email;

    private Phone mainPhone;

    private Integer numberOfEmployees;

    private BigDecimal annualRevenue;

    private BigDecimal annualRevenueInUSD;

    @Lob
    private String description;

    @Index(name = "IDX_ACCOUNT_CURRENCY")
    @ForeignKey(name = "FK_ACCOUNT_CURRENCY")
    @ManyToOne(fetch = FetchType.LAZY)
    private Currency currency;

    @ForeignKey(name = "FK_ACCOUNT_ACCOUNT", inverseName = "FK_ACCOUNT_ACCOUNT_TYPES")
    @ManyToMany(fetch = FetchType.LAZY)
    private Set<AccountType> accountTypes = new HashSet<AccountType>();

    @Index(name = "IDX_ACCOUNT_ASSIGNED_TO")
    @ForeignKey(name = "FK_ACCOUNT_ASSIGNED_TO")
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.REFRESH)
    private User assignedTo;

    @Index(name = "IDX_ACCOUNT_INDUSTRY")
    @ForeignKey(name = "FK_ACCOUNT_INDUSTRY")
    @ManyToOne(fetch = FetchType.LAZY)
    private Industry industry;

    @Index(name = "IDX_ACCOUNT_BILLING_ADDRESS")
    @ForeignKey(name = "FK_ACCOUNT_BILLING_ADDRESS")
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private Address billingAddress = new Address(AddressType.BILLING);

    @Index(name = "IDX_ACCOUNT_SHIPPING_ADDRESS")
    @ForeignKey(name = "FK_ACCOUNT_SHIPPING_ADDRESS")
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private Address mailingAddress;

    @OneToMany(mappedBy = "account")
    private Set<Contact> contacts = new HashSet<Contact>();

    @OneToMany(mappedBy = "account")
    private Set<Opportunity> opportunities = new HashSet<Opportunity>();

    public Account() {
    }

    @NotBlank
    @NotNull
    @Size(min = 1, max = 64)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Size(min = 4, max = 64)
    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    @Size(min = 1, max = 25)
    public String getTickerSymbol() {
        return tickerSymbol;
    }

    public void setTickerSymbol(String tickerSymbol) {
        this.tickerSymbol = tickerSymbol;
    }

    @Email
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @NotNull
    @ValidPhone
    public Phone getMainPhone() {
        return mainPhone;
    }

    public void setMainPhone(Phone mainPhone) {
        this.mainPhone = mainPhone;
    }

    @Min(0)
    public Integer getNumberOfEmployees() {
        return numberOfEmployees;
    }

    public void setNumberOfEmployees(Integer numberOfEmployees) {
        this.numberOfEmployees = numberOfEmployees;
    }

    @Min(0)
    public BigDecimal getAnnualRevenue() {
        return annualRevenue;
    }

    public void setAnnualRevenue(BigDecimal annualRevenue) {
        if (!isEqual(this.annualRevenue, annualRevenue)) {
            this.annualRevenue = annualRevenue;
            annualRevenueInUSD = calculateAnnualRevenueInUSD();
        }
    }

    public void setAnnualRevenue(double annualRevenue) {
        setAnnualRevenue(new BigDecimal(annualRevenue));
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        if (!isEqual(this.currency, currency)) {
            this.currency = currency;
            annualRevenueInUSD = calculateAnnualRevenueInUSD();
        }
    }

    public BigDecimal getAnnualRevenueInUSD() {
        return annualRevenueInUSD;
    }

    private BigDecimal calculateAnnualRevenueInUSD() {
        if (getAnnualRevenue() == null || getCurrency() == null) {
            return null;
        } else {
            try {
                BigDecimal annualRevenueInUSD = ecbfxService.convert(getAnnualRevenue(), getCurrency().getId(), "USD");
                return annualRevenueInUSD.setScale(0, RoundingMode.HALF_EVEN);
            } catch (IllegalCurrencyException e) {
                return null;
            }
        }
    }

    public String getAnnualRevenueInUSDFormatted() {
        BigDecimal amount = getAnnualRevenueInUSD();
        if (amount == null) {
            return null;
        } else {
            return "$" + defaultFormat.getNumberFormat().format(amount);
        }
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Set<AccountType> getAccountTypes() {
        return accountTypes;
    }

    public void setAccountTypes(Set<AccountType> accountType) {
        this.accountTypes = accountType;
    }

    public void addAccountType(AccountType accountType) {
        getAccountTypes().add(accountType);
    }

    public User getAssignedTo() {
        return assignedTo;
    }

    public void setAssignedTo(User assignedTo) {
        this.assignedTo = assignedTo;
    }

    public Industry getIndustry() {
        return industry;
    }

    public void setIndustry(Industry industry) {
        this.industry = industry;
    }

    @Valid
    @NotNull
    public Address getBillingAddress() {
        return billingAddress;
    }

    public void setBillingAddress(Address billingAddress) {
        if (billingAddress != null) {
            billingAddress.setAddressType(AddressType.BILLING);
        }
        this.billingAddress = billingAddress;
    }

    @Valid
    public Address getMailingAddress() {
        return mailingAddress;
    }

    public void setMailingAddress(Address mailingAddress) {
        if (mailingAddress != null) {
            mailingAddress.setAddressType(AddressType.MAILING);
        }
        this.mailingAddress = mailingAddress;
    }

    public Set<Contact> getContacts() {
        return contacts;
    }

    public void setContacts(Set<Contact> contacts) {
        this.contacts = contacts;
    }

    public Set<Opportunity> getOpportunities() {
        return opportunities;
    }

    public void setOpportunities(Set<Opportunity> opportunities) {
        this.opportunities = opportunities;
    }

    @PreRemove
    public void preRemove() {
        for (Contact contact : getContacts()) {
            contact.setAccount(null);
        }
    }
}