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
import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Index;
import org.hibernate.validator.constraints.NotBlank;
import org.joda.money.IllegalCurrencyException;

import javax.annotation.Resource;
import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;

import static com.purebred.core.util.ObjectUtil.*;

@Entity
@Table
public class Opportunity extends WritableEntity {

    @Resource
    @Transient
    private DefaultFormats defaultFormat;

    @Resource
    @Transient
    private EcbfxService ecbfxService;

    private String name;

    @Enumerated(EnumType.STRING)
    private OpportunityType opportunityType = OpportunityType.NEW;

    @Temporal(TemporalType.DATE)
    private Date expectedCloseDate;

    private BigDecimal amount;

    @Index(name = "IDX_OPPORTUNITY_CURRENCY")
    @ForeignKey(name = "FK_OPPORTUNITY_CURRENCY")
    @ManyToOne(fetch = FetchType.LAZY)
    private Currency currency;

    private double probability;

    private BigDecimal amountWeightedInUSD;

    @Lob
    private String description;

    @Index(name = "IDX_OPPORTUNITY_LEAD_SOURCE")
    @ForeignKey(name = "FK_OPPORTUNITY_LEAD_SOURCE")
    @ManyToOne(fetch = FetchType.LAZY)
    private LeadSource leadSource;

    @Index(name = "IDX_OPPORTUNITY_SALES_STAGE")
    @ForeignKey(name = "FK_OPPORTUNITY_SALES_STAGE")
    @ManyToOne(fetch = FetchType.LAZY)
    private SalesStage salesStage;

    @Index(name = "IDX_OPPORTUNITY_ACCOUNT")
    @ForeignKey(name = "FK_OPPORTUNITY_ACCOUNT")
    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.REFRESH)
    private Account account;

    @Index(name = "IDX_OPPORTUNITY_USER")
    @ForeignKey(name = "FK_OPPORTUNITY_USER")
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.REFRESH)
    private User assignedTo;

    public Opportunity() {
    }

    public Opportunity(String name) {
        this.name = name;
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

    public OpportunityType getOpportunityType() {
        return opportunityType;
    }

    public void setOpportunityType(OpportunityType opportunityType) {
        this.opportunityType = opportunityType;
    }

    public Date getExpectedCloseDate() {
        return expectedCloseDate;
    }

    public void setExpectedCloseDate(Date expectedCloseDate) {
        this.expectedCloseDate = expectedCloseDate;
    }

    @Min(0)
    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        if (!isEqual(this.amount, amount)) {
            this.amount = amount;
            amountWeightedInUSD = calculateAmountWeightedInUSD();
        }
    }

    public void setAmount(double amount) {
        setAmount(new BigDecimal(amount));
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        if (!isEqual(this.currency, currency)) {
            this.currency = currency;
            amountWeightedInUSD = calculateAmountWeightedInUSD();
        }
    }

    public double getProbability() {
        return probability;
    }

    public void setProbability(double probability) {
        if (this.probability != probability) {
            this.probability = probability;
            amountWeightedInUSD = calculateAmountWeightedInUSD();
        }
    }

    public BigDecimal getAmountWeightedInUSD() {
        return amountWeightedInUSD;
    }

    private BigDecimal calculateAmountWeightedInUSD() {
        if (getAmount() == null || getCurrency() == null) {
            return null;
        } else {
            try {
                BigDecimal amountInUSD = ecbfxService.convert(getAmount(), getCurrency().getId(), "USD");
                amountInUSD = amountInUSD.setScale(0, RoundingMode.HALF_EVEN);
                BigDecimal amountWeightedInUSD = amountInUSD.multiply(new BigDecimal(getProbability()));
                return amountWeightedInUSD.setScale(0, RoundingMode.HALF_EVEN);
            } catch (IllegalCurrencyException e) {
                return null;
            }
        }
    }

    public String getAmountWeightedInUSDFormatted() {
        BigDecimal amount = getAmountWeightedInUSD();
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

    public LeadSource getLeadSource() {
        return leadSource;
    }

    public void setLeadSource(LeadSource leadSource) {
        this.leadSource = leadSource;
    }

    public SalesStage getSalesStage() {
        return salesStage;
    }

    public void setSalesStage(SalesStage salesStage) {
        if (!isEqual(this.salesStage, salesStage)) {
            this.salesStage = salesStage;
            setProbability(salesStage.getProbability());
        }
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

    @PreRemove
    public void preRemove() {
        setAccount(null);
    }
}