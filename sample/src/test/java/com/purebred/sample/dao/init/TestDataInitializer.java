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

package com.purebred.sample.dao.init;

import com.purebred.sample.util.PhonePropertyFormatter;
import com.purebred.sample.dao.AccountDao;
import com.purebred.sample.dao.ContactDao;
import com.purebred.sample.dao.OpportunityDao;
import com.purebred.sample.dao.UserDao;
import com.purebred.sample.entity.*;
import com.purebred.sample.util.PhoneValidator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

@Service
@Transactional
public class TestDataInitializer {

    @Resource
    private ContactDao contactDao;

    @Resource
    private AccountDao accountDao;

    @Resource
    private OpportunityDao opportunityDao;

    @Resource
    private ReferenceDataInitializer referenceDataInitializer;

    @Resource
    private UserDao userDao;

    public void initializeUsers() {
        User user = new User("admin", "admin");
        userDao.persist(user);

        user = new User("guest", "guest");
        userDao.persist(user);
        userDao.getEntityManager().flush();
    }

    public void initialize(int count) {
        initializeUsers();

        for (Integer i = 0; i < count; i++) {
            Contact contact;
            contact = new Contact("first" + i, "last" + i);
            contact.setBirthDate(randomBirthDate());
            contact.setAssignedTo(ReferenceDataInitializer.random(userDao.findAll()));
            contact.setTitle("Vice President");
            contact.setDoNotCall(randomBoolean());
            contact.setEmail("customer@purecrud.com");
            contact.setDoNotEmail(randomBoolean());
            contact.setLeadSource(referenceDataInitializer.randomLeadSource());
            Address address = randomAddress(i);
            contact.setMailingAddress(address);

            if (randomBoolean()) {
                Address otherAddress = randomAddress(i);
                contact.setOtherAddress(otherAddress);
            }

            try {
                Phone phone = (Phone) new PhonePropertyFormatter().parse(PhoneValidator.getExampleNumber("US", address.getCountry().getId()));
                contact.setMainPhone(phone);
                contact.getMainPhone().setPhoneType(random(PhoneType.class));

                if (randomBoolean()) {
                    phone = (Phone) new PhonePropertyFormatter().parse(PhoneValidator.getExampleNumber("US", address.getCountry().getId()));
                    contact.setOtherPhone(phone);
                    contact.getOtherPhone().setPhoneType(random(PhoneType.class));
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }


            contact.setDescription("Description of contact");

            initializeAccount(contact, i);
            contactDao.persist(contact);
            if (i % 50 == 0) {
                contactDao.getEntityManager().flush();
                contactDao.getEntityManager().clear();
            }
        }
    }

    private void initializeAccount(Contact contact, int i) {
        Account account = new Account();
        account.setName("Purebred Solutions" + i);
        contact.setAccount(account);
        account.setWebsite("http://www.purecrud.com");
        account.setTickerSymbol("PCRUD");

        Address address = randomAddress(i);
        account.setBillingAddress(address);

        account.addAccountType(referenceDataInitializer.randomAccountType());
        account.addAccountType(referenceDataInitializer.randomAccountType());
        account.setAssignedTo(ReferenceDataInitializer.random(userDao.findAll()));
        account.setNumberOfEmployees(ReferenceDataInitializer.random(1, 1000000));
        account.setAnnualRevenue(ReferenceDataInitializer.random(1, 1000000000));
        account.setCurrency(referenceDataInitializer.randomCurrency());
        account.setDescription("Description of account");
        account.setEmail("info@purecrud.com");
        account.setIndustry(referenceDataInitializer.randomIndustry());

        try {
            Phone phone = (Phone) new PhonePropertyFormatter().parse(PhoneValidator.getExampleNumber("US", address.getCountry().getId()));
            account.setMainPhone(phone);
            account.getMainPhone().setPhoneType(random(PhoneType.class));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        if (randomBoolean()) {
            Address mailingAddress = randomAddress(i);
            account.setMailingAddress(mailingAddress);
        }
        accountDao.persist(account);

        initializeOpportunity(account, i);
    }

    private void initializeOpportunity(Account account, int i) {
        Opportunity opportunity = new Opportunity();
        opportunity.setName("opportunityName" + i);
        opportunity.setAccount(account);

        opportunity.setSalesStage(referenceDataInitializer.randomSalesStage());
        opportunity.setCurrency(referenceDataInitializer.randomCurrency());
        opportunity.setExpectedCloseDate(new Date());
        opportunity.setAssignedTo(ReferenceDataInitializer.random(userDao.findAll()));
        opportunity.setLeadSource(referenceDataInitializer.randomLeadSource());
        opportunity.setDescription("Description of opportunity");
        opportunity.setOpportunityType(random(OpportunityType.class));

        opportunity.setAmount(ReferenceDataInitializer.random(1, 1000000));

        opportunityDao.persist(opportunity);
    }

    private Address randomAddress(int i) {
        Address address = new Address();
        address.setStreet(i + " Main St");
        address.setCity("Mayberry" + i);
        State state = referenceDataInitializer.randomState();
        address.setCountry(state.getCountry());
        address.setState(state);
        if (state.getCountry().getId().equals("CA")) {
            address.setZipCode("A0A 0A0");
        } else {
            address.setZipCode(state.getCountry().getMinPostalCode());
        }

        return address;
    }

    public static Date randomBirthDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.HOUR, 0);
        calendar.set(Calendar.DAY_OF_MONTH, ReferenceDataInitializer.random(1, 28));
        calendar.set(Calendar.MONTH, ReferenceDataInitializer.random(1, 12));
        calendar.set(Calendar.YEAR, ReferenceDataInitializer.random(1920, 2010));

        return calendar.getTime();
    }

    public static boolean randomBoolean() {
        int i = ReferenceDataInitializer.random(0, 1);
        return i == 1;
    }

    public static <T extends Enum> T random(Class<T> enumType) {
        T[] enumConstants = enumType.getEnumConstants();
        Arrays.asList(enumConstants);

        return enumConstants[ReferenceDataInitializer.random(0, enumConstants.length - 1)];
    }
}
