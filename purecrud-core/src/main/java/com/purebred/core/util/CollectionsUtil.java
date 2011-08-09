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
 * Date: 5/9/11
 * Time: 9:26 PM
 */
public class CollectionsUtil {
    public static String[] toStringArray(Collection collection) {
        String[] stringArray = new String[collection.size()];

        int i = 0;
        for (Object string : collection) {
            stringArray[i++] = string.toString();
        }

        return stringArray;
    }
}
