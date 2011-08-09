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

import org.apache.commons.lang.StringUtils;

import javax.swing.*;
import java.awt.*;

public class StringUtil {

    public static final FontMetrics FONT_METRICS;

    static {
        JTextField jTextField = new JTextField("");
        Font font = new Font("Helvetica", Font.PLAIN, 12);
        FONT_METRICS = jTextField.getFontMetrics(font);
    }


    public static boolean isEqual(String s, String... args) {
        for (String arg : args) {
            if (s.equals(arg)) {
                return true;
            }
        }

        return false;
    }

    public static int approximateColumnWidth(String s) {
        return (int) Math.ceil(FONT_METRICS.stringWidth(s)* 0.083);
    }

    public static String extractAfterPeriod(String str) {
        int periodIndex = str.indexOf(".");
        if (periodIndex < 0) {
            return str;
        } else {
            return str.substring(periodIndex + 1);
        }
    }

    public static String humanizeCamelCase(String camelCase) {
        String[] camelCaseParts = StringUtils.splitByCharacterTypeCamelCase(camelCase);
        String joined = StringUtils.join(camelCaseParts, " ");
        return capitaliseFirstLetter(joined);
    }

    public static String capitaliseFirstLetter(String str) {
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

    public static boolean isEmpty(Object s) {
        if (s != null && s instanceof String) {
            return ((String) s).isEmpty();
        } else {
            return s == null;
        }
    }
}
