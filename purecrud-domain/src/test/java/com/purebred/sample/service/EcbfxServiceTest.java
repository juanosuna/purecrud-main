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

package com.purebred.sample.service;

import com.purebred.domain.ecbfx.EcbfxService;
import org.junit.Ignore;
import org.junit.Test;

import javax.annotation.Resource;

import java.math.BigDecimal;
import java.util.Map;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * User: Juan
 * Date: 7/30/11
 */
@Ignore
public class EcbfxServiceTest extends AbstractServiceTest {
    @Resource
    private EcbfxService ecbfxService;

    @Test
    public void getFXRates() {
        Map<String, BigDecimal> fxRates = ecbfxService.getFXRates();
        assertNotNull(fxRates);
        assertTrue(fxRates.size() > 0);
    }
}
