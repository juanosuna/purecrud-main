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

import com.purebred.domain.geoplanet.GeoPlanetService;
import org.junit.Ignore;
import org.junit.Test;

import javax.annotation.Resource;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * User: Juan
 * Date: 7/30/11
 */
@Ignore
public class GeoPlanetServiceTest extends AbstractServiceTest {
    @Resource
    private GeoPlanetService geoPlanetService;

    @Test
    public void getCountries() {
        Set<GeoPlanetService.CountryInfo> countries = geoPlanetService.getCountries();
        assertNotNull(countries);
        assertTrue(countries.size() > 0);
    }

    @Test
    public void getCurrencyCodes() {
        Set<String> countriesWithStates = new HashSet<String>();
        countriesWithStates.add("US");
        countriesWithStates.add("CA");

        Set<GeoPlanetService.Place> states = geoPlanetService.getStates(countriesWithStates);
        assertNotNull(states);
        assertTrue(states.size() > 0);
    }
}
