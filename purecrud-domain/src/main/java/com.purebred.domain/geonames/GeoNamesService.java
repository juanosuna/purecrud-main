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

package com.purebred.domain.geonames;

import com.purebred.domain.RestClientService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Configuration
@Service
public class GeoNamesService extends RestClientService {

    @Resource
    private GeoNamesClient geoNamesClient;

    public Map<String, CountryInfo> getCountries() {

        Map<String, CountryInfo> countries = new HashMap<String, CountryInfo>();

        GeoNamesResponse geoNamesResponse = geoNamesClient.getPostalCodeCountryInfo("josuna");
        for (CountryInfo country : geoNamesResponse.countries) {
            countries.put(country.countryCode, country);
        }

        return countries;
    }

    public Map<String, String> getCurrencyCodes() {

        Map<String, String> currencyCodes = new HashMap<String, String>();

        GeoNamesResponse geoNamesResponse = geoNamesClient.getCountryInfo("josuna");
        for (CountryInfo info : geoNamesResponse.countries) {
            currencyCodes.put(info.countryCode, info.currencyCode);
        }

        return currencyCodes;
    }

    @Bean
    public GeoNamesClient getGeoNamesClient(@Value("${geoNamesService.url}") String url) throws Exception {
        return create(url, GeoNamesClient.class);
    }

    @Path("/")
    public static interface GeoNamesClient {
        @Path("/postalCodeCountryInfo")
        @GET
        @Produces("application/xml")
        GeoNamesResponse getPostalCodeCountryInfo(@QueryParam("username") String username);

        @Path("/countryInfo")
        @GET
        @Produces("application/xml")
        GeoNamesResponse getCountryInfo(@QueryParam("username") String username);
    }

    @XmlRootElement(namespace = "", name = "geonames")
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class GeoNamesResponse {

        @XmlElement(name = "country")
        public List<CountryInfo> countries;
    }

    @XmlAccessorType(XmlAccessType.FIELD)
    public static class CountryInfo {

        public String countryCode;
        public String minPostalCode;
        public String maxPostalCode;
        public String currencyCode;
    }
}
