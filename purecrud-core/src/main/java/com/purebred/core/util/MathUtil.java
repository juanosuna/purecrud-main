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

/**
 * User: Juan
 * Date: 7/29/11
 */
public class MathUtil {
    public static Integer maxIgnoreNull(Integer a, Integer b) {
        if (a == null) return b;
        if (b == null) return a;

        return Math.max(a, b);
    }

    public static Integer minIgnoreNull(Integer a, Integer b) {
        if (a == null) return b;
        if (b == null) return a;

        return Math.min(a, b);
    }

    public static Integer maxDisallowNull(Integer a, Integer b) {
        if (a == null || b == null) return null;

        return Math.max(a, b);
    }

    public static Integer minDisallowNull(Integer a, Integer b) {
        if (a == null || b == null) return null;

        return Math.min(a, b);
    }

}
