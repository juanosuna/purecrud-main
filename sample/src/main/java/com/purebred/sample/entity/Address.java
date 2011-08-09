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
import com.purebred.core.validation.AssertTrueForProperties;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Index;
import org.hibernate.validator.constraints.NotBlank;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import static com.purebred.core.util.StringUtil.isEmpty;
import static com.purebred.core.util.StringUtil.isEqual;

@Entity
@Table
public class Address extends WritableEntity {

    @Enumerated(EnumType.STRING)
    private AddressType addressType;

    private String street;

    private String city;

    private String zipCode;

    @Index(name = "IDX_ADDRESS_STATE")
    @ForeignKey(name = "FK_ADDRESS_STATE")
    @ManyToOne(fetch = FetchType.LAZY)
    private State state;

    @Index(name = "IDX_ADDRESS_COUNTRY")
    @ForeignKey(name = "FK_ADDRESS_COUNTRY")
    @ManyToOne(fetch = FetchType.LAZY)
    private Country country = new Country("US");

    public Address() {
    }

    public Address(AddressType addressType) {
        this.addressType = addressType;
    }

    @NotNull
    public AddressType getAddressType() {
        return addressType;
    }

    public void setAddressType(AddressType type) {
        this.addressType = type;
    }

    @NotNull
    @NotBlank
    @Size(min = 1, max = 16)
    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    @NotNull
    @NotBlank
    @Size(min = 1, max = 16)
    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    @AssertTrueForProperties(errorProperty = "zipCode", message = "US zip code must be 5 or 9 digits")
    public boolean isUsZipCodeValid() {
        if (!isEmpty(getZipCode()) && isCountryId("US")) {
            return getZipCode().matches("^\\d{5}$|^\\d{5}$");
        } else {
            return true;
        }
    }

    @AssertTrueForProperties(errorProperty = "zipCode", message = "CA zip code must be have the format: A0A 0A0")
    public boolean isCaZipCodeValid() {
        if (!isEmpty(getZipCode()) && isCountryId("CA")) {
            return getZipCode().matches("^[a-zA-Z]\\d[a-zA-Z] \\d[a-zA-Z]\\d$");
        } else {
            return true;
        }
    }

    @AssertTrueForProperties(errorProperty = "zipCode", message = "Zip code invalid for selected country")
    public boolean isZipCodeValidForCountry() {
        if (!isEmpty(getZipCode()) && getCountry() != null && !isCountryId("US", "CA")) {
            return getCountry().isZipCodeValid(getZipCode());
        } else {
            return true;
        }
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    @AssertTrueForProperties(errorProperty = "state", message = "State is required for selected country")
    public boolean isStateValid() {
        if (getCountry() != null && isEqual(getCountry().getId(), "US", "CA", "MX", "AU")) {
            return getState() != null;
        } else {
            return true;
        }
    }

    @NotNull
    public Country getCountry() {
        return country;
    }

    public void setCountry(Country country) {
        this.country = country;
    }

    public boolean isCountryId(String... countryId) {
        if (getCountry() != null) {
            for (String id : countryId) {
                if (getCountry().getId().equals(id)) {
                    return true;
                }
            }
        }

        return false;
    }
}