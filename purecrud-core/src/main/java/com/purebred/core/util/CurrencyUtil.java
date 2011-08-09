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

package com.purebred.core.util;

import java.util.ArrayList;
import java.util.Currency;
import java.util.List;
import java.util.Locale;

/**
 * User: Juan
 * Date: 7/12/11
 * Time: 3:51 PM
 */
public class CurrencyUtil {

    public static List<Currency> getAvailableCurrencies() {
        List<Currency> currencies = new ArrayList<Currency>();
        Locale[] locales = Locale.getAvailableLocales();
        for (Locale locale : locales) {
            if (!StringUtil.isEmpty(locale.getCountry())) {
                Currency currency = Currency.getInstance(locale);
                currencies.add(currency);
            }
        }

        return currencies;
    }
}
