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

package com.purebred.sample.util;

import com.purebred.sample.entity.Phone;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import com.vaadin.data.util.PropertyFormatter;

public class PhonePropertyFormatter extends PropertyFormatter {

    public static final String DEFAULT_PHONE_COUNTRY = "US";

    @Override
    public String format(Object value) {
        Phone phone = (Phone) value;

        Phonenumber.PhoneNumber phoneNumber = new Phonenumber.PhoneNumber();
        phoneNumber.setCountryCode(phone.getCountryCode());
        phoneNumber.setNationalNumber(phone.getPhoneNumber());

        PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
        PhoneNumberUtil.PhoneNumberFormat format;
        String regionCodeForNumber = phoneUtil.getRegionCodeForNumber(phoneNumber);
        if (regionCodeForNumber == null || regionCodeForNumber.equals(DEFAULT_PHONE_COUNTRY)) {
            format = PhoneNumberUtil.PhoneNumberFormat.NATIONAL;
        } else {
            format = PhoneNumberUtil.PhoneNumberFormat.INTERNATIONAL;
        }

        return phoneUtil.format(phoneNumber, format);
    }

    @Override
    public Object parse(String formattedValue) throws Exception {
        return new Phone(formattedValue, DEFAULT_PHONE_COUNTRY);
    }
}
