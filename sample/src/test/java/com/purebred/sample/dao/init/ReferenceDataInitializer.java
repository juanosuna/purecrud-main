/*
 * Copyright (c) 2011 Brown Bag Consulting.
 * This file is part of the PureCRUD project.
 * Author: Juan Osuna
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License Version 3
 * as published by the Free Software Foundation with the addition of the
 * following permission added to Section 15 as permitted in Section 7(a):
 * FOR ANY PART OF THE COVERED WORK IN WHICH THE COPYRIGHT IS OWNED BY
 * Brown Bag Consulting, Brown Bag Consulting DISCLAIMS THE WARRANTY OF
 * NON INFRINGEMENT OF THIRD PARTY RIGHTS.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 * The interactive user interfaces in modified source and object code versions
 * of this program must display Appropriate Legal Notices, as required under
 * Section 5 of the GNU Affero General Public License.
 *
 * You can be released from the requirements of the license by purchasing
 * a commercial license. Buying such a license is mandatory as soon as you
 * develop commercial activities involving the PureCRUD software without
 * disclosing the source code of your own applications. These activities
 * include: offering paid services to customers as an ASP, providing
 * services from a web application, shipping PureCRUD with a closed
 * source product.
 *
 * For more information, please contact Brown Bag Consulting at this
 * address: juan@brownbagconsulting.com.
 */

package com.purebred.sample.dao.init;

import com.purebred.core.dao.EntityDao;
import com.purebred.sample.dao.*;
import com.purebred.sample.entity.*;
import com.purebred.sample.entity.Currency;
import com.purebred.domain.geonames.GeoNamesService;
import com.purebred.domain.geoplanet.GeoPlanetService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;

@Service
@Transactional
public class ReferenceDataInitializer {

    public static final Random RANDOM = new Random(System.currentTimeMillis());

    public static final String[] ACCOUNT_TYPES = {"Analyst", "Competitor", "Customer", "Integrator",
            "Investor", "Partner", "Press", "Prospect", "Reseller", "Other"};

    public static final String[] INDUSTRIES = {"Apparel", "Banking", "Biotechnology", "Chemicals",
            "Communication", "Construction", "Consulting", "Education", "Electronics", "Energy",
            "Engineering", "Entertainment", "Environmental", "Finance", "Food & Beverage", "Government",
            "Healthcare", "Hospitality", "Insurance", "Machinery", "Manufacturing", "Media",
            "Not for Profit", "Recreation", "Retail", "Shipping", "Technology", "Telecommunications",
            "Transportation", "Utilities", "Other"};

    public static final String[] LEAD_SOURCES = {"Cold Call", "Existing Customer", "Self-generated", "Employee",
            "Partner", "Public Relations", "Direct Mail", "Conference", "Trade Show", "Website", "Word of Mouth",
            "Other"};

    public static final String[] SALES_STAGES = {"Prospecting", "Qualification", "Needs Analysis", "Value Proposition",
            "Id. Decision Makers", "Perception Analysis", "Proposal/Price Quote", "Negotiation/Review", "Closed Won",
            "Closed Lost"};

    public static final Map<String, Double> SALES_STAGE_PROBABILITIES = new HashMap<String, Double>();

    static {
        SALES_STAGE_PROBABILITIES.put("Prospecting", .10);
        SALES_STAGE_PROBABILITIES.put("Qualification", .20);
        SALES_STAGE_PROBABILITIES.put("Needs Analysis", .25);
        SALES_STAGE_PROBABILITIES.put("Value Proposition", .30);
        SALES_STAGE_PROBABILITIES.put("Id. Decision Makers", .40);
        SALES_STAGE_PROBABILITIES.put("Perception Analysis", .50);
        SALES_STAGE_PROBABILITIES.put("Proposal/Price Quote", .65);
        SALES_STAGE_PROBABILITIES.put("Negotiation/Review", .80);
        SALES_STAGE_PROBABILITIES.put("Closed Won", 1.0);
        SALES_STAGE_PROBABILITIES.put("Closed Lost", 0.0);
    }

    public static Set<String> COUNTRIES_WITH_STATES = new HashSet<String>(Arrays.asList(
            "United States",
            "Canada",
            "Mexico",
            "Australia"
    ));

    @Resource
    private AccountTypeDao accountTypeDao;

    @Resource
    private IndustryDao industryDao;

    @Resource
    private LeadSourceDao leadSourceDao;

    @Resource
    private SalesStageDao salesStageDao;

    @Resource
    private StateDao stateDao;

    @Resource
    private CountryDao countryDao;

    @Resource
    private CurrencyDao currencyDao;

    @Resource
    private GeoPlanetService geoPlanetService;

    @Resource
    private GeoNamesService geoNamesService;

    public AccountType randomAccountType() {
        return random(accountTypeDao.findAll());
    }

    public Industry randomIndustry() {
        return random(industryDao.findAll());
    }

    public LeadSource randomLeadSource() {
        return random(leadSourceDao.findAll());
    }

    public SalesStage randomSalesStage() {
        return random(salesStageDao.findAll());
    }

    public Currency randomCurrency() {
        return random(currencyDao.findAll());
    }

    public Country randomCountry() {
        return random(countryDao.findAll());
    }

    public State randomState() {
        return random(stateDao.findAll());
    }

    public static int random(int start, int end) {
        if (start == end) {
            return start;
        } else {
            return RANDOM.nextInt(++end - start) + start;
        }
    }

    public static <T> T random(List<T> entities) {
        return entities.get(ReferenceDataInitializer.random(0, entities.size() - 1));
    }

    public static boolean hasExistingEntities(EntityDao dao) {
        return dao.countAll() > 0;
    }

    public void initialize() {
        initializeReferenceEntities();
        if (!hasExistingEntities(countryDao) && !hasExistingEntities(currencyDao)) {
            initializeCountriesAndCurrencies();
        }
        if (!hasExistingEntities(stateDao)) {
            initializeStates();
        }
    }

    private void initializeReferenceEntities() {
        if (!hasExistingEntities(accountTypeDao)) {
            for (String accountType : ACCOUNT_TYPES) {
                AccountType referenceEntity = new AccountType(accountType);
                accountTypeDao.persist(referenceEntity);
            }
            accountTypeDao.flush();
        }

        if (!hasExistingEntities(industryDao)) {
            for (String industry : INDUSTRIES) {
                Industry referenceEntity = new Industry(industry);
                industryDao.persist(referenceEntity);
            }
            industryDao.flush();
        }

        if (!hasExistingEntities(leadSourceDao)) {
            for (int i = 0, lead_sourcesLength = LEAD_SOURCES.length; i < lead_sourcesLength; i++) {
                String leadSource = LEAD_SOURCES[i];
                LeadSource referenceEntity = new LeadSource(leadSource);
                referenceEntity.setSortOrder(i);
                leadSourceDao.persist(referenceEntity);
            }
            leadSourceDao.flush();
        }

        if (!hasExistingEntities(salesStageDao)) {
            for (int i = 0, sales_stagesLength = SALES_STAGES.length; i < sales_stagesLength; i++) {
                String salesStage = SALES_STAGES[i];
                SalesStage referenceEntity = new SalesStage(salesStage);
                referenceEntity.setSortOrder(i);
                referenceEntity.setProbability(SALES_STAGE_PROBABILITIES.get(salesStage));
                salesStageDao.persist(referenceEntity);
            }
            salesStageDao.flush();
        }
    }

    private void initializeCountriesAndCurrencies() {
        Set<GeoPlanetService.CountryInfo> geoPlanetCountries = geoPlanetService.getCountries();
        Map<String, GeoNamesService.CountryInfo> geoNamesCountries = geoNamesService.getCountries();
        Map<String, String> geoNamesCurrencies = geoNamesService.getCurrencyCodes();

        for (GeoPlanetService.CountryInfo geoPlanetCountry : geoPlanetCountries) {
            GeoNamesService.CountryInfo geoNamesCountry = geoNamesCountries.get(geoPlanetCountry.code);
            if (geoNamesCountry != null) {
                Country country = new Country(geoPlanetCountry.code, geoPlanetCountry.name);
                country.setMinPostalCode(geoNamesCountry.minPostalCode);
                country.setMaxPostalCode(geoNamesCountry.maxPostalCode);

                String currencyCode = geoNamesCurrencies.get(country.getId());
                Currency currency = new Currency(currencyCode);
                if (!currencyDao.isPersistent(currency)) {
                    if (currency.getId().equals("EUR")) {
                        currency.setDisplayName(currency.getId() + "-Europe");
                    } else if (currency.getId().equals("USD")) {
                        currency.setDisplayName(currency.getId() + "-United States");
                    } else {
                        currency.setDisplayName(currency.getId() + "-" + country.getDisplayName());
                    }
                    currencyDao.persist(currency);
                }
                countryDao.persist(country);
            }
        }

        countryDao.flush();
    }

    private void initializeStates() {
        Set<GeoPlanetService.Place> geoPlanetStates = geoPlanetService.getStates(COUNTRIES_WITH_STATES);
        for (GeoPlanetService.Place geoPlanetState : geoPlanetStates) {
            Country country = new Country(geoPlanetState.country.code);
            State state = new State(geoPlanetState.admin1.code, geoPlanetState.admin1.name, country);
            stateDao.persist(state);
        }

        stateDao.flush();
    }
}
