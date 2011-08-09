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


import com.purebred.core.entity.ReferenceEntity;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Index;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import static com.purebred.core.entity.ReferenceEntity.CACHE_REGION;

@Entity
@Table
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = CACHE_REGION)
public class State extends ReferenceEntity {

    private String code;
    private String stateType;

    @Index(name = "IDX_STATE_COUNTRY")
    @ForeignKey(name = "FK_STATE_COUNTRY")
    @ManyToOne(fetch = FetchType.LAZY)
    private Country country;

    public State() {
    }

    public State(String id) {
        super(id);
    }

    public State(String id, String displayName, Country country) {
        super(id, displayName);
        this.country = country;
        this.code = extractStateCode();
    }

    private String extractStateCode() {
        if (getId().contains("-")) {
            String[] codeParts = getId().split("-");
            if (codeParts.length == 2 && codeParts[0].equals(getCountry().getId())) {
                return codeParts[1];
            }
        }

        return null;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getStateType() {
        return stateType;
    }

    public void setStateType(String stateType) {
        this.stateType = stateType;
    }

    @NotNull
    public Country getCountry() {
        return country;
    }

    public void setCountry(Country country) {
        this.country = country;
    }
}