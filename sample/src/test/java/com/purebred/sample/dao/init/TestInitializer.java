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

package com.purebred.sample.dao.init;

import com.purebred.sample.dao.AbstractDomainTest;
import org.junit.Test;
import org.springframework.test.annotation.IfProfileValue;
import org.springframework.test.context.transaction.TransactionConfiguration;

import javax.annotation.Resource;

@TransactionConfiguration(transactionManager = "transactionManager", defaultRollback = false)
public class TestInitializer extends AbstractDomainTest {

    @Resource
    private ReferenceDataInitializer referenceDataInitializer;

    @Resource
    private TestDataInitializer testDataInitializer;

    @Test
    @IfProfileValue(name="regenerateDB", value="true")
    public void initialize() throws Exception {
        referenceDataInitializer.initialize();
        testDataInitializer.initialize(100);
    }
}
