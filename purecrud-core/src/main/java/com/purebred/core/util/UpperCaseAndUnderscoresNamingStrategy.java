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

package com.purebred.core.util;

import org.hibernate.AssertionFailure;
import org.hibernate.cfg.DefaultComponentSafeNamingStrategy;

public class UpperCaseAndUnderscoresNamingStrategy extends DefaultComponentSafeNamingStrategy {

    public static final String TABLE_PREFIX = "";

    public UpperCaseAndUnderscoresNamingStrategy() {
        super();
    }

    protected String insertUnderscores(String name) {
        StringBuffer buf = new StringBuffer(name.replace('.', '_'));
        for (int i = 1; i < buf.length() - 1; i++) {
            if (
                    Character.isLowerCase(buf.charAt(i - 1)) &&
                            Character.isUpperCase(buf.charAt(i)) &&
                            Character.isLowerCase(buf.charAt(i + 1))
                    ) {
                buf.insert(i++, '_');
            }
        }
        return buf.toString().toLowerCase();
    }

    @Override
    public String classToTableName(String className) {
        return TABLE_PREFIX + insertUnderscores(className).toUpperCase();
    }

    @Override
    public String propertyToColumnName(String propertyName) {
        return insertUnderscores(propertyName).toUpperCase();
    }

    @Override
    public String tableName(String tableName) {
        return TABLE_PREFIX + insertUnderscores(tableName).toUpperCase();
    }

    @Override
    public String columnName(String columnName) {
        return insertUnderscores(columnName).toUpperCase();
    }

    @Override
    public String collectionTableName(String ownerEntity, String ownerEntityTable, String associatedEntity, String associatedEntityTable, String propertyName) {
        return tableName(
                new StringBuilder(ownerEntityTable).append("_")
                        .append(
                                associatedEntityTable != null ?
                                        associatedEntityTable :
                                        insertUnderscores(propertyName)
                        ).toString()
        ).toUpperCase();
    }

    @Override
    public String foreignKeyColumnName(String propertyName, String propertyEntityName, String propertyTableName, String referencedColumnName) {
        String header = propertyName != null ? insertUnderscores(propertyName) : propertyTableName;
        if (header == null) throw new AssertionFailure("NamingStrategy not properly filled");
        return columnName(header + "_" + referencedColumnName).toUpperCase();
    }

    @Override
    public String logicalColumnName(String columnName, String propertyName) {
        return insertUnderscores(super.logicalColumnName(columnName, propertyName)).toUpperCase();
    }

    @Override
    public String logicalCollectionTableName(String tableName, String ownerEntityTable, String associatedEntityTable, String propertyName) {
        return (TABLE_PREFIX + super.logicalCollectionTableName(tableName, ownerEntityTable, associatedEntityTable, propertyName)).toUpperCase();
    }

    @Override
    public String logicalCollectionColumnName(String columnName, String propertyName, String referencedColumn) {
        return super.logicalCollectionColumnName(columnName, propertyName, referencedColumn).toUpperCase();
    }


    @Override
    public String joinKeyColumnName(String joinedColumn, String joinedTable) {
        return super.joinKeyColumnName(joinedColumn, joinedTable).toUpperCase();
    }
}
