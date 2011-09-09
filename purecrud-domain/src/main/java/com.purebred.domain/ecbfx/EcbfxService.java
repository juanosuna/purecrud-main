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

package com.purebred.domain.ecbfx;

import com.purebred.domain.RestClientService;
import org.apache.commons.lang.time.DateUtils;
import org.joda.money.CurrencyUnit;
import org.joda.money.IllegalCurrencyException;
import org.joda.money.Money;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.ws.rs.GET;
import javax.ws.rs.Produces;
import javax.xml.bind.annotation.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Configuration
@Service
public class EcbfxService extends RestClientService {

    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    private Date rateDay;
    private Date fetchDay;
    private Map<String, BigDecimal> rates;

    @Resource
    private ECBFXClient ecbfxClient;

    public BigDecimal convert(BigDecimal amount, String sourceCurrencyCode, String targetCurrencyCode)
            throws IllegalCurrencyException {

        if (sourceCurrencyCode.equals(targetCurrencyCode)) return amount;

        Map<String, BigDecimal> rates = getFXRates();
        BigDecimal inverseRate = rates.get(sourceCurrencyCode);
        if (inverseRate == null)
            throw new IllegalCurrencyException("Unknown currency: " + sourceCurrencyCode);

        BigDecimal sourceRate = new BigDecimal(1).divide(inverseRate, 10, RoundingMode.HALF_EVEN);
        CurrencyUnit sourceCurrencyUnit = CurrencyUnit.of(sourceCurrencyCode);
        Money amountInSourceCurrency = Money.of(sourceCurrencyUnit, amount, RoundingMode.HALF_EVEN);
        Money amountInEuros;
        if (sourceCurrencyUnit.getCurrencyCode().equals("EUR")) {
            amountInEuros = amountInSourceCurrency;
        } else {
            amountInEuros = amountInSourceCurrency.convertedTo(CurrencyUnit.of("EUR"), sourceRate, RoundingMode.HALF_EVEN);
        }

        BigDecimal targetRate = rates.get(targetCurrencyCode);
        if (targetRate == null) throw new IllegalCurrencyException("Unknown currency: " + targetCurrencyCode);

        Money amountInTargetCurrency = amountInEuros.convertedTo(CurrencyUnit.of(targetCurrencyCode), targetRate, RoundingMode.HALF_EVEN);

        return amountInTargetCurrency.getAmount();
    }

    public Map<String, BigDecimal> getFXRates() {
        if (rateDay == null || (DateUtils.truncatedCompareTo(rateDay, new Date(), Calendar.DAY_OF_MONTH) < 0
                && DateUtils.truncatedCompareTo(fetchDay, new Date(), Calendar.DAY_OF_MONTH) < 0)) {
            fetchFXRates();
        }

        return rates;
    }

    private void fetchFXRates() {
        rates = new HashMap<String, BigDecimal>();

        ECBFXResponse ecbfxResponse = ecbfxClient.getFXRates();
        try {
            rateDay = DATE_FORMAT.parse(ecbfxResponse.mainCube.quoteDate.time);
            fetchDay = DateUtils.truncate(new Date(), Calendar.DAY_OF_MONTH);

        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        for (ECBFXResponse.MainCube.QuoteDate.Rate rate : ecbfxResponse.mainCube.quoteDate.rates) {
            rates.put(rate.currency, new BigDecimal(rate.rate));
        }
        rates.put("EUR", new BigDecimal(1));
    }

    @Bean
    ECBFXClient getEcbfxClient(@Value("${ecbfxService.url}") String url) throws Exception {
        return create(url, ECBFXClient.class);
    }

    static interface ECBFXClient {
        @GET
        @Produces("application/xml")
        ECBFXResponse getFXRates();
    }

    private static final String NAMESPACE = "http://www.ecb.int/vocabulary/2002-08-01/eurofxref";

    @XmlRootElement(namespace = "http://www.gesmes.org/xml/2002-08-01", name = "Envelope")
    @XmlAccessorType(XmlAccessType.FIELD)
    static class ECBFXResponse {

        @XmlElement(name = "Cube", namespace = NAMESPACE)
        public MainCube mainCube;

        @XmlAccessorType(XmlAccessType.FIELD)
        public static class MainCube {

            @XmlElement(name = "Cube", namespace = NAMESPACE)
            public QuoteDate quoteDate;

            @XmlAccessorType(XmlAccessType.FIELD)
            public static class QuoteDate {

                @XmlAttribute
                public String time;

                @XmlElement(name = "Cube", namespace = NAMESPACE)
                public List<Rate> rates;

                @XmlAccessorType(XmlAccessType.FIELD)
                public static class Rate {

                    @XmlAttribute
                    public String currency;

                    @XmlAttribute
                    public String rate;
                }
            }
        }
    }
}
