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
 * patents in process, and are protected by trade secret or copyrightlaw.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Brown Bag Consulting LLC.
 */

package com.purebred.core.view;

import org.springframework.context.support.ReloadableResourceBundleMessageSource;

import java.util.Locale;

public class MessageSource extends ReloadableResourceBundleMessageSource {

    public String getMessageWithDefault(String code) {
        return getMessage(code, null, code);
    }

    public String getMessage(String code) {
        Locale locale;
        if (MainApplication.getInstance() == null) {
            locale = Locale.getDefault();
        } else {
            locale = MainApplication.getInstance().getLocale();
        }
        return getMessage(code, null, null, locale);
    }

    public String getMessage(String code, Object[] args) {
        return getMessage(code, args, code);
    }

    public String getMessage(String code, String defaultMessage) {
        return getMessage(code, null, defaultMessage);
    }

    public String getMessage(String code, Object[] args, String defaultMessage) {
        Locale locale;
        if (MainApplication.getInstance() == null) {
            locale = Locale.getDefault();
        } else {
            locale = MainApplication.getInstance().getLocale();
        }
        return getMessage(code, args, defaultMessage, locale);
    }
}
