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

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;

import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * User: Juan
 * Date: 7/19/11
 */
@Embeddable
public class Phone implements Serializable {
    private Integer countryCode;
    private Long phoneNumber;

    @Enumerated(EnumType.STRING)
    private PhoneType phoneType = PhoneType.BUSINESS;

    public Phone() {
    }

    public Phone(String fullNumber, String defaultRegionCode) throws NumberParseException {
        if (fullNumber.matches(".*[a-zA-Z]+.*")) {
            throw new NumberParseException(NumberParseException.ErrorType.NOT_A_NUMBER, "Phone number may not contain letters");
        }

        PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
        Phonenumber.PhoneNumber phoneNumber = phoneUtil.parse(fullNumber, defaultRegionCode);

        this.countryCode = phoneNumber.getCountryCode();
        this.phoneNumber = phoneNumber.getNationalNumber();
    }

    public String getFormatted(String defaultRegionCode) {
        Phonenumber.PhoneNumber phoneNumber = new Phonenumber.PhoneNumber();
        phoneNumber.setCountryCode(countryCode);
        phoneNumber.setNationalNumber(this.phoneNumber);

        PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
        PhoneNumberUtil.PhoneNumberFormat format;
        if (phoneUtil.getRegionCodeForNumber(phoneNumber).equals(defaultRegionCode)) {
            format = PhoneNumberUtil.PhoneNumberFormat.NATIONAL;
        } else {
            format = PhoneNumberUtil.PhoneNumberFormat.INTERNATIONAL;
        }

        return phoneUtil.format(phoneNumber, format);
    }

    @NotNull
    public Integer getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(Integer countryCode) {
        this.countryCode = countryCode;
    }

    @NotNull
    public Long getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(Long phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    @NotNull
    public PhoneType getPhoneType() {
        return phoneType;
    }

    public void setPhoneType(PhoneType phoneType) {
        this.phoneType = phoneType;
    }
}
