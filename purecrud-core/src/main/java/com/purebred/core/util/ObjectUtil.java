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

import java.util.Collection;

/**
 * User: Juan
 * Date: 8/5/11
 */
public class ObjectUtil {
    public static boolean isEqual(Object a, Object b) {
        if (a == null && b == null) return true;
        if (a == null && b != null) return false;
        if (a != null && b == null) return false;

        return a.equals(b);
    }

    public static boolean isEqualDeep(Collection a, Collection b) {
        if (a == null && b == null) return true;
        if (a == null && b != null) return false;
        if (a != null && b == null) return false;

        for (Object o : a) {
            if (!b.contains(a)) return false;
        }
        for (Object o : b) {
            if (!a.contains(b)) return false;
        }
        return true;
    }

    public static int hashCodeDeep(Collection a) {
        int hashCode = 0;

        for (Object o : a) {
            hashCode += o.hashCode();
        }

        return hashCode;
    }
}
